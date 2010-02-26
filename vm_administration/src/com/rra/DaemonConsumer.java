package com.rra;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.rra.configuration.Settings;

public class DaemonConsumer
	implements
		Runnable
{

	public void run()
	{
		boolean active = true;
		while (active)
		{
			System.out.println("Executing consumer...");
			new Thread(new Runnable()
			{
				public void run()
				{
					Consumer c;
					try
					{
						c = new Consumer(hostName,
								Settings.DEFAULT_PORT);
						c.bash = bashPath;
						c.command = command;
						c.consume(queue);
						c.free();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}).start();
			try
			{
				Thread.sleep(1000 * 30);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	protected String	hostName;
	protected String	bashPath;
	protected String	command;
	protected String	queue;

	public static void main(String[] args)
	{
		try
		{
			final Options o = new Options();
			o.addOption(Settings.HOST_OPTION, true,
				"Server to connect to after launch");
			o.addOption(Settings.BASH_OPTION, true,
				"Path to bash (normally /bin/bash)");
			o.addOption(Settings.COMMAND_OPTION, true, "Command to execute");
			o.addOption(Settings.QUEUE_OPTION, true, "Queue to consume");

			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(o, args);

			final DaemonConsumer dc = new DaemonConsumer();
			dc.hostName = cmd.getOptionValue(Settings.HOST_OPTION);
			dc.bashPath = cmd.getOptionValue(Settings.BASH_OPTION);
			dc.command = cmd.getOptionValue(Settings.COMMAND_OPTION);
			dc.queue = cmd.getOptionValue(Settings.QUEUE_OPTION);
			if (dc.hostName == null || dc.bashPath == null
					|| dc.command == null || dc.queue == null)
			{
				System.err.println("Incorrect usage.");
				System.exit(2);
			}

			new Thread(dc).start();
		}
		catch (Exception ex)
		{
			System.err.println("Main thread caught exception: " + ex);
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
