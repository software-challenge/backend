package sc.protocol.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import sc.api.plugins.host.IRequestResult;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;

public class AsyncResultManager
{
	private Map<Class<? extends ProtocolMessage>, Collection<IRequestResult>>	handlers		= new HashMap<>();

	public void invokeHandlers(ProtocolMessage response)
	{
		Collection<IRequestResult> responseHandlers = getHandlers(response.getClass());
		this.handlers.remove(response.getClass());

		for (IRequestResult handler : responseHandlers)
		{
			if (response instanceof ProtocolErrorMessage)
			{
				handler.handleError((ProtocolErrorMessage) response);
			}
			else if (response != null)
			{
				handler.operate(response);
			}
		}
	}

	private Collection<IRequestResult> getHandlers(Class<? extends ProtocolMessage> responseClass)
	{
		Collection<IRequestResult> current = this.handlers.get(responseClass);
		Collection<IRequestResult> result = new LinkedList<>();

		if (current != null)
		{
			for (IRequestResult handler : current)
			{
				result.add(handler);
			}
		}

		return result;
	}

	public void addHandler(Class<? extends ProtocolMessage> response, IRequestResult handler)
	{
		Collection<IRequestResult> current = this.handlers.get(response);

		if (current == null)
		{
			current = new LinkedList<>();
			this.handlers.put(response, current);
		}

		current.add(handler);
	}
}
