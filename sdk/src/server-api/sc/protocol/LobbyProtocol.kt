package sc.protocol

import com.thoughtworks.xstream.XStream
import sc.api.plugins.ITeam
import sc.protocol.requests.*
import sc.protocol.responses.*
import sc.protocol.room.ErrorMessage
import sc.protocol.room.GamePaused
import sc.protocol.room.MementoMessage
import sc.protocol.room.RoomPacket
import sc.shared.*

object LobbyProtocol {
    
    @JvmStatic
    fun registerMessages(xStream: XStream): XStream {
        // Messages
        registerAdditionalMessages(xStream, listOf(
                ErrorPacket::class.java,
                ErrorMessage::class.java,
                GamePaused::class.java,
                JoinedRoomResponse::class.java,
                RemovedFromGame::class.java,
                MementoMessage::class.java,
                GamePreparedResponse::class.java,
                ObservationResponse::class.java,
                RoomPacket::class.java,
                WelcomeMessage::class.java,
                PlayerScoreResponse::class.java,
                TestModeResponse::class.java,
                RoomWasJoinedEvent::class.java,
        ))
        
        // Requests
        registerAdditionalMessages(xStream, listOf(
                AuthenticateRequest::class.java,
                CancelRequest::class.java,
                JoinPreparedRoomRequest::class.java,
                JoinGameRequest::class.java,
                JoinRoomRequest::class.java,
                ObservationRequest::class.java,
                PauseGameRequest::class.java,
                PrepareGameRequest::class.java,
                StepRequest::class.java,
                PlayerScoreRequest::class.java,
                TestModeRequest::class.java,
        ))
        
        // Data
        registerAdditionalMessages(xStream, listOf(
                GameResult::class.java,
                PlayerScore::class.java,
                ScoreAggregation::class.java,
                ITeam::class.java,
                ScoreCause::class.java,
                ScoreDefinition::class.java,
                ScoreFragment::class.java,
                WinCondition::class.java,
                SlotDescriptor::class.java,
                Score::class.java,
                ScoreValue::class.java,
        ))
        
        return xStream
    }
    
    @JvmStatic
    fun registerAdditionalMessages(xStream: XStream, protocolClasses: Collection<Class<*>>?): XStream {
        if (protocolClasses != null) {
            for (clazz in protocolClasses) {
                xStream.processAnnotations(clazz)
            }
        }
        return xStream
    }
    
}
