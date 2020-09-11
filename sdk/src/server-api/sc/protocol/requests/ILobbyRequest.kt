package sc.protocol.requests

import sc.protocol.responses.ProtocolMessage

/** Parent for all messages the Lobby can handle.  */
interface ILobbyRequest: ProtocolMessage

/** Marks Requests that can only be made by an administrative client. */
interface AdminLobbyRequest: ILobbyRequest