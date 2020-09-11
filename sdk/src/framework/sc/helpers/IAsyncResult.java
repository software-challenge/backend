package sc.helpers;

import sc.protocol.responses.ProtocolMessage;

public interface IAsyncResult {
  void operate(ProtocolMessage result);
}
