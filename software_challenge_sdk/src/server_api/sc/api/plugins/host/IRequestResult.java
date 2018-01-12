package sc.api.plugins.host;

import sc.helpers.IAsyncResult;
import sc.protocol.responses.ProtocolErrorMessage;

public interface IRequestResult extends IAsyncResult
{
	public void handleError(ProtocolErrorMessage e);
}
