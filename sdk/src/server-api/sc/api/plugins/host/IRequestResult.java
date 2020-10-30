package sc.api.plugins.host;

import sc.helpers.IAsyncResult;
import sc.protocol.responses.ProtocolErrorMessage;

public interface IRequestResult extends IAsyncResult {
	void handleError(ProtocolErrorMessage e);
}
