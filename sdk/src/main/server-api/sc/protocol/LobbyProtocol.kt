package sc.protocol

import com.thoughtworks.xstream.XStream
import sc.api.plugins.Team
import sc.protocol.requests.*
import sc.protocol.responses.*
import sc.protocol.room.*
import sc.shared.*

object LobbyProtocol {
    
    @JvmStatic
    fun registerMessages(xStream: XStream): XStream {
        // Requests
        registerAdditionalMessages(xStream, listOf(
                AuthenticateRequest::class.java,
                CancelRequest::class.java,
                JoinGameRequest::class.java,
                JoinPreparedRoomRequest::class.java,
                JoinRoomRequest::class.java,
                ObservationRequest::class.java,
                PauseGameRequest::class.java,
                PlayerScoreRequest::class.java,
                PrepareGameRequest::class.java,
                StepRequest::class.java,
                TestModeRequest::class.java,
        ))
    
        // Responses
        registerAdditionalMessages(xStream, listOf(
                ErrorPacket::class.java,
                GamePreparedResponse::class.java,
                JoinedRoomResponse::class.java,
                ObservationResponse::class.java,
                PlayerScoreResponse::class.java,
                RoomWasJoinedEvent::class.java,
                TestModeResponse::class.java,
        ))
        
        // Messages
        registerAdditionalMessages(xStream, listOf(
                RemovedFromGame::class.java,
                RoomPacket::class.java,
                ErrorMessage::class.java,
                GamePaused::class.java,
                MementoMessage::class.java,
                MoveRequest::class.java,
                WelcomeMessage::class.java,
        ))
        
        // Data
        registerAdditionalMessages(xStream, listOf(
                GameResult::class.java,
                PlayerScore::class.java,
                ScoreAggregation::class.java,
                Team::class.java,
                ScoreCause::class.java,
                ScoreDefinition::class.java,
                ScoreFragment::class.java,
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
