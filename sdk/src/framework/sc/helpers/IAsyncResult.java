package sc.helpers;

import sc.protocol.responses.ProtocolMessage;

import java.util.function.Consumer;

public interface IAsyncResult extends Consumer<ProtocolMessage> {}
