package sc.protocol;

import java.util.Arrays;
import java.util.Collection;

import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.CancelRequest;
import sc.protocol.requests.FreeReservationRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.requests.StepRequest;
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
				PauseGameRequest.class, PrepareGameRequest.class,
				StepRequest.class }));
		
		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				GameResult.class, PlayerScore.class, ScoreAggregation.class, PlayerColor.class,
				ScoreCause.class, ScoreDefinition.class, ScoreFragment.class, WinCondition.class,
				SlotDescriptor.class }));

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
