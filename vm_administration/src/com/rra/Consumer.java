package com.rra;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.SysexMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rra.configuration.Settings;

public class Consumer
{
	private final Connection	conn;
	protected String			bash;
	protected String			command;
	protected String			queue;
	protected List<IConsumerListener> listeners = new LinkedList<IConsumerListener>();

	public Consumer(String hostname, int port) throws IOException
	{
		this.conn = new ConnectionFactory().newConnection(hostname, port);
	}
	
	public void addConsumerListener(IConsumerListener listener) {
		listeners.add(listener);
	}
	
	public void removeConsumerListener(IConsumerListener listener) {
		listeners.remove(listener);
	}
	
	protected void notifyOnStartConsuming() {
		for (IConsumerListener listener : listeners) {
			listener.onStartConsuming();
		}
	}

	protected void consume(String queue) throws IOException
	{
		final Channel ch = conn.createChannel();
		ch.queueDeclare(queue);

		boolean noAck = true;
		GetResponse response = ch.basicGet(queue, noAck);
		if (response == null)
		{
			// Do nothing
		} else
		{
			System.out.println("Consuming message...");
			notifyOnStartConsuming();
			onConsume(new String(response.getBody()));
		}
		ch.close();
	}

	protected void onConsume(String message)
	{
		int exitValue = -1;
		try
		{
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(new String[]{ this.bash, this.command, message });
			process.waitFor();
			exitValue = process.exitValue();
		}
		catch (IOException e)
		{
			System.err.println("Unable to consume message!");
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			System.err.println("Unable to consume message!");
			e.printStackTrace();
		} finally {
			System.out.println("Message consumed: " + exitValue);
		}
	}

	protected void free() throws IOException
	{
		this.conn.close();
	}
	
	public static void main(String[] args)
	{
		try
		{
			final Options o = new Options();
			o.addOption(Settings.HOST_OPTION, true, "Server to connect to after launch");
			o.addOption(Settings.BASH_OPTION, true, "Path to bash (normally /bin/bash)");
			o.addOption(Settings.COMMAND_OPTION, true, "Command to execute");
			o.addOption(Settings.QUEUE_OPTION, true, "Queue to consume");
			
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(o, args);
			
			String hostName = cmd.getOptionValue(Settings.HOST_OPTION);
			String bashPath = cmd.getOptionValue(Settings.BASH_OPTION);
			String command = cmd.getOptionValue(Settings.COMMAND_OPTION);
			String queue = cmd.getOptionValue(Settings.QUEUE_OPTION);
			int portNumber = Settings.DEFAULT_PORT;
			if (hostName == null || bashPath == null || command == null || queue == null) {
				System.err.println("Incorrect usage.");
				System.exit(2);
			}

			final Consumer c = new Consumer(hostName, portNumber);
			c.bash = bashPath;
			c.command = command;
			c.consume(queue);
			c.free();
		}
		catch (Exception ex)
		{
			System.err.println("Main thread caught exception: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
