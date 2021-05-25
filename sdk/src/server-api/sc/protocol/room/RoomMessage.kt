package sc.protocol.room

/** Implemented by any message sent within a GameRoom. */
interface RoomMessage

/** Implemented by any message within a room
 * that does not concern the progress of the game. */
interface RoomOrchestrationMessage: RoomMessage
