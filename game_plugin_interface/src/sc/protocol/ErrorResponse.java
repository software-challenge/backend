package sc.protocol;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("error")
public class ErrorResponse
{
	private Object	originalRequest;

	@XStreamAsAttribute
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
