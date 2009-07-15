package sc.protocol;

public class RequestResult<T>
{
	T				result	= null;
	ErrorResponse	error	= null;

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
	 * @return
	 */
	public boolean isSuccessful()
	{
		return this.error == null;
	}

	public void setError(ErrorResponse error)
	{
		this.error = error;
	}

	/**
	 * Get's the error (if the request failed);
	 * @return
	 */
	public ErrorResponse getError()
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
