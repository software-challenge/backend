package sc.protocol.room

/** A sent within a GameRoom. */
interface RoomMessage

/** Marks a [RoomMessage] that does not concern the progress of the game. */
interface RoomOrchestrationMessage: RoomMessage

/** Marks a [RoomMessage] that can be received by observers. */
interface ObservableRoomMessage: RoomMessage
