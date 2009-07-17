package sc.api.plugins.host;

import sc.helpers.IAsyncResult;
import sc.protocol.responses.ErrorResponse;

public interface IRequestResult<T> extends IAsyncResult<T>
{
	public void handleError(ErrorResponse e);
}
