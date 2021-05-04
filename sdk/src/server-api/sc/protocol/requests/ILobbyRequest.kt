package sc.protocol.requests

import sc.protocol.ProtocolPacket

/** Parent for all messages the Lobby can handle.  */
interface ILobbyRequest: ProtocolPacket

/** Marks Requests that can only be made by an administrative client. */
interface AdminLobbyRequest: ILobbyRequest