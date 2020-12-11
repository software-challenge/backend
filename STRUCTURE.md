# Overview

TODO

## [sdk/server-api/sc.protocol](sdk/src/server-api/sc/protocol)

*Request: Ask for an action or information  
extends [AdminLobbyRequest](sdk/src/server-api/sc/protocol/requests/ILobbyRequest.kt): Requires authentication

### [Responses](sdk/src/server-api/sc/protocol/responses)

If it extends `ProtocolMessage` directly, it is wrapped in a [RoomPacket](sdk/src/server-api/sc/protocol/responses/RoomPacket.kt)
and sent to a specific room, otherwise it has to extend `ILobbyRequest` and is sent to LobbyListeners.

*Response: Response to a request  
*Event: Update to all observers
