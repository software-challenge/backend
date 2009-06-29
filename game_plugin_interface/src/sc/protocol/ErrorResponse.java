package sc.protocol;

public class ErrorResponse
{
	private Object	originalRequest;

	private String	message;

	public ErrorResponse(Object request, String message)
	{
		this.originalRequest = request;
		this.message = message;
	}

	public Object getOriginalRequest()
	{
		return this.originalRequest;
	}

	public String getMessage()
	{
		return this.message;
	}

}
