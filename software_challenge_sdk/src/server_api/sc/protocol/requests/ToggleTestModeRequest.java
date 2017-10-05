package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("testMode")
public class ToggleTestModeRequest extends ProtocolMessage  implements ILobbyRequest {
}
