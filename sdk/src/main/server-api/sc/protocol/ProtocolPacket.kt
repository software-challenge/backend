package sc.protocol

/** Common interface for all packets sent via the XML Protocol. */
interface ProtocolPacket

/** Interface for all packets sent by the server to clients. */
interface ResponsePacket : ProtocolPacket
