package sc.protocol;

import java.util.Arrays;
import java.util.Collection;

import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.shared.*;

import com.thoughtworks.xstream.XStream;

public abstract class LobbyProtocol
{
	public static XStream registerMessages(XStream xStream)
	{
		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				ProtocolErrorMessage.class, GamePausedEvent.class,
				JoinGameProtocolMessage.class, LeftGameEvent.class,
				MementoPacket.class, PrepareGameProtocolMessage.class,
				ObservationProtocolMessage.class, RoomPacket.class }));

		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				AuthenticateRequest.class, CancelRequest.class,
				FreeReservationRequest.class, JoinPreparedRoomRequest.class,
				JoinRoomRequest.class, ObservationRequest.class,
				PauseGameRequest.class, ControlTimeoutRequest.class, PrepareGameRequest.class,
				StepRequest.class, GetScoreForPlayerRequest.class, TestModeRequest.class,
				PlayerScorePacket.class, TestModeMessage.class, GameRoomMessage.class
		}));
		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				GameResult.class, PlayerScore.class, ScoreAggregation.class, PlayerColor.class,
				ScoreCause.class, ScoreDefinition.class, ScoreFragment.class, WinCondition.class,
				SlotDescriptor.class, Score.class, ScoreValue.class}));

		return xStream;
	}

	public static XStream registerAdditionalMessages(XStream xstream,
			Collection<Class<?>> protocolClasses)
	{
		if(protocolClasses != null)
		{
			for (Class<?> clazz : protocolClasses)
			{
				xstream.processAnnotations(clazz);
			}
		}

		return xstream;
	}
}
