package com.rra.configuration;

import com.rabbitmq.client.AMQP;

public class Settings
{
	public static final int		DEFAULT_PORT	= AMQP.PROTOCOL.PORT;
	public static final String	DEFAULT_HOST	= "127.0.0.1";

	public static final String	VM_QUEUE		= "vm-queue";
	public static final String	SWC_QUEUE		= "swc-job-queue";

	public static final String	BASH_OPTION		= "b";
	public static final String	COMMAND_OPTION	= "c";
	public static final String	HOST_OPTION		= "h";
	public static final String	QUEUE_OPTION	= "q";
}
