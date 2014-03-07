package com.rra;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rra.configuration.Settings;

public class Producer
	implements
		Runnable
{
	private final Connection	conn;
	private File				toWatch;
	private File				toTmp;

	public void setTmp(File folder)
	{
		this.toTmp = folder;
	}

	public void setWatch(File folder)
	{
		this.toWatch = folder;
	}

	public Producer(String hostname, int port) throws IOException
	{
		this.conn = new ConnectionFactory().newConnection(hostname, port);
	}

	private void publish(String message, String toQueue) throws IOException
	{
		String exchange = "";

		Channel ch = conn.createChannel();

		if (exchange.equals(""))
		{
			ch.queueDeclare(toQueue);
		}
		
		System.out.println(message);
		System.out.println("published to queue: " + toQueue);

		ch.basicPublish(exchange, toQueue,
			MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		
		ch.close();
	}

	private void free() throws IOException
	{
		this.conn.close();
	}

	private static String	FOLDER_WATCH	= "w";
	private static String	FOLDER_TMP		= "t";

	public static void main(String[] args)
	{
		try
		{
			final Options o = new Options();
			o.addOption(Settings.HOST_OPTION, true, "Server to connect to after launch");
			o.addOption(FOLDER_WATCH, true, "Folder to watch");
			o.addOption(FOLDER_TMP, true, "Tmp client folder");

			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(o, args);
			String hostAddress = cmd.getOptionValue(Settings.HOST_OPTION);
			String folder = cmd.getOptionValue(FOLDER_WATCH);
			String tmp = cmd.getOptionValue(FOLDER_TMP);

			int portNumber = Settings.DEFAULT_PORT;
			if (hostAddress == null)
				hostAddress = Settings.DEFAULT_HOST;
			if (folder == null || tmp == null)
			{
				System.err.println("Folder to watch does not exist!");
				System.exit(2);
			}

			final File f = new File(folder);
			final File t = new File(tmp);
			System.out.println("Connecting to RabbitMQ at " + hostAddress + ":"
					+ portNumber);

			final Producer p = new Producer(hostAddress, portNumber);
			p.setWatch(f);
			p.setTmp(t);

			new Thread(p).start();
		}
		catch (Exception e)
		{
			System.err.println("Main thread caught exception: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run()
	{
		try
		{
			boolean active = true;
			while (active)
			{
				String[] files = toWatch.list();
				if (files != null && files.length > 0)
				{
					for (String file : files)
					{
						if (file.startsWith("stop"))
							active = false;

						if (!file.endsWith(".zip"))
							continue;

						File oldZIP = new File(toWatch.getAbsolutePath()
								+ File.separator + file);
						File newZIP = new File(toTmp.getAbsolutePath()
								+ File.separator + file);

						// Move zip to tmp folder
						Runtime.getRuntime().exec(
							new String[]
							{ "/bin/mv", oldZIP.getAbsolutePath(),
									newZIP.getAbsolutePath() });

						String message = newZIP.getAbsolutePath();
						publish(message, Settings.SWC_QUEUE);
						publish("Start a fresh VM", Settings.VM_QUEUE);
					}
				}
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				free();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}
}
