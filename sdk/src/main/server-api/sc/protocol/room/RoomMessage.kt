package sc.protocol.room

/** For all communication within a GameRoom. */
interface RoomMessage

/** A [RoomMessage] that does not concern the progress of the game. */
interface RoomOrchestrationMessage: RoomMessage

/** A [RoomMessage] that can be received by observers. */
interface ObservableRoomMessage: RoomMessage
