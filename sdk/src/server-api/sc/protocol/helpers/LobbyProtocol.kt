package sc.protocol.helpers

import com.thoughtworks.xstream.XStream
import sc.api.plugins.ITeam
import sc.protocol.requests.*
import sc.protocol.responses.*
import sc.shared.*

object LobbyProtocol {

    @JvmStatic
    fun registerMessages(xStream: XStream): XStream {
        registerAdditionalMessages(xStream, listOf(ProtocolErrorMessage::class.java, GamePausedEvent::class.java, JoinGameProtocolMessage::class.java, LeftGameEvent::class.java, MementoPacket::class.java, PrepareGameProtocolMessage::class.java, ObservationProtocolMessage::class.java, RoomPacket::class.java, WelcomeMessage::class.java))
    
        registerAdditionalMessages(xStream, listOf(AuthenticateRequest::class.java, CancelRequest::class.java, FreeReservationRequest::class.java, JoinPreparedRoomRequest::class.java, JoinRoomRequest::class.java, ObservationRequest::class.java, PauseGameRequest::class.java, ControlTimeoutRequest::class.java, PrepareGameRequest::class.java, StepRequest::class.java, GetScoreForPlayerRequest::class.java, TestModeRequest::class.java, PlayerScorePacket::class.java, TestModeMessage::class.java, GameRoomMessage::class.java))
    
        registerAdditionalMessages(xStream, listOf(GameResult::class.java, PlayerScore::class.java, ScoreAggregation::class.java, ITeam::class.java,
                ScoreCause::class.java, ScoreDefinition::class.java, ScoreFragment::class.java, WinCondition::class.java,
                SlotDescriptor::class.java, Score::class.java, ScoreValue::class.java))

        return xStream
    }

    @JvmStatic
    fun registerAdditionalMessages(xstream: XStream, protocolClasses: Collection<Class<*>>?): XStream {
        if(protocolClasses != null) {
            for(clazz in protocolClasses) {
                xstream.processAnnotations(clazz)
            }
        }
        return xstream
    }

}
