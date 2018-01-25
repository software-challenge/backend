package sc.protocol.helpers;

import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;

public final class RequestResult<T extends ProtocolMessage>
{
	T	result	= null;
	ProtocolErrorMessage error	= null;

	/**
	 * Checks wether this object was set-up correctly.
	 * 
	 * @return true, if either result or error is set.
	 */
	public boolean hasValidContents()
	{
		return (this.result == null ^ this.error == null);
	}

	/**
	 * True, if error is not set.
	 * 
	 * @return True, if error is not set
	 */
	public boolean isSuccessful()
	{
		return this.error == null;
	}

	public void setError(ProtocolErrorMessage error)
	{
		this.error = error;
	}

	/**
	 * Get's the error (if the request failed);
	 * @return ProtocolErrorMessage
	 */
	public ProtocolErrorMessage getError()
	{
		return this.error;
	}

	public void setResult(T result)
	{
		this.result = result;
	}

	public T getResult()
	{
		return this.result;
	}
}
