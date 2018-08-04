package sc.helpers;

import sc.protocol.responses.ProtocolMessage;

public interface IAsyncResult
{
	public void operate(ProtocolMessage result);
}
