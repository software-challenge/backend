package sc.protocol.requests

import sc.protocol.ProtocolPacket

/** Parent for all packets handled by the Lobby.  */
interface ILobbyRequest: ProtocolPacket

/** Marks requests only allowed as an administrative client. */
interface AdminLobbyRequest: ILobbyRequest