package sc.helpers;

import sc.protocol.ErrorResponse;

public interface IRequestResult<T> extends IAsyncResult<T>
{
	public void handleError(ErrorResponse e);
}
