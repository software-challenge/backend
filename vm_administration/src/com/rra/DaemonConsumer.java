package com.rra;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.rra.configuration.Settings;

public class DaemonConsumer
	implements
		Runnable, IConsumerListener
{

	public void run()
	{
		boolean active = true;
		System.out.println("Consumer daemon started...");
		while (active)
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					if (Settings.DEBUG_MODE) {
						System.out.println(DaemonConsumer.this.currentProcs + "/" + DaemonConsumer.this.maxProcs + " running");
					}
					if (DaemonConsumer.this.currentProcs < DaemonConsumer.this.maxProcs) {
						if (Settings.DEBUG_MODE) {
							System.out.println("Checking queue...");
						}
						Consumer c;
						DaemonConsumer.this.currentProcs += 1;
						try
						{
							c = new Consumer(hostName,
									Settings.DEFAULT_PORT);
							c.addConsumerListener(DaemonConsumer.this);
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
						finally {
							DaemonConsumer.this.currentProcs -= 1;
						}
					}
				}

			}).start();
			try
			{
				Thread.sleep(interval * 1000);
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
	protected int		interval;
	protected int		maxProcs;
	protected int		currentProcs = 0;

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
			o.addOption(Settings.INTERVAL_OPTION, true, "How long to wait before consuming next message");
			o.addOption(Settings.MAX_PROC_OPTION, true, "How many processes at same time");
			o.addOption(Settings.DEBUG_OPTION, false, "Enable debug mode");

			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(o, args);

			final DaemonConsumer dc = new DaemonConsumer();
			dc.hostName = cmd.getOptionValue(Settings.HOST_OPTION);
			dc.bashPath = cmd.getOptionValue(Settings.BASH_OPTION);
			dc.command = cmd.getOptionValue(Settings.COMMAND_OPTION);
			dc.queue = cmd.getOptionValue(Settings.QUEUE_OPTION);
			dc.interval = Integer.valueOf(cmd.getOptionValue(Settings.INTERVAL_OPTION, "5"));
			dc.maxProcs = Integer.valueOf(cmd.getOptionValue(Settings.MAX_PROC_OPTION, "12"));
			Settings.DEBUG_MODE = cmd.hasOption(Settings.DEBUG_OPTION);
			
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

	public void onStartConsuming() {
		System.out.println("Starting process " + currentProcs + "/" + maxProcs);
	}
}
