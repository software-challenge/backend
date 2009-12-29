package sc.protocol.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import sc.api.plugins.host.IRequestResult;
import sc.protocol.responses.ErrorResponse;

public class AsyncResultManager
{
	private Map<Class<?>, Collection<IRequestResult<?>>>	handlers		= new HashMap<Class<?>, Collection<IRequestResult<?>>>();
	
	@SuppressWarnings("unchecked")
	private <T> Collection<IRequestResult<T>> getHandlers(Class<T> response)
	{
		Collection<IRequestResult<?>> current = this.handlers.get(response);
		Collection<IRequestResult<T>> result = new LinkedList<IRequestResult<T>>();

		if (current != null)
		{
			for (IRequestResult<?> handler : current)
			{
				result.add((IRequestResult<T>) handler);
			}
		}

		return result;
	}

	public <T extends Object> void invokeHandlers(Class<T> responseType,
			T response, ErrorResponse error)
	{
		if (!(error == null ^ response == null)) // XOR
		{
			throw new RuntimeException("Either error or response must be null.");
		}

		Collection<IRequestResult<T>> responseHandlers = getHandlers(responseType);
		this.handlers.remove(responseType);

		for (IRequestResult<T> handler : responseHandlers)
		{
			if (error != null)
			{
				handler.handleError(error);
			}
			else if (response != null)
			{
				handler.operate(response);
			}
		}
	}

	public <T> void addHandler(Class<T> response, IRequestResult<T> handler)
	{
		Collection<IRequestResult<?>> current = this.handlers.get(response);

		if (current == null)
		{
			current = new LinkedList<IRequestResult<?>>();
			this.handlers.put(response, current);
		}

		current.add(handler);
	}
}
