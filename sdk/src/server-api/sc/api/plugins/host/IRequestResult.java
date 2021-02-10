package sc.api.plugins.host;

import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;

import java.util.function.Consumer;

public interface IRequestResult extends Consumer<ProtocolMessage> {
	void handleError(ProtocolErrorMessage e);
}
