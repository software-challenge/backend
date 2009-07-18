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
import sc.protocol.responses.ErrorResponse;
import sc.protocol.responses.GamePausedEvent;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.MementoPacket;
import sc.protocol.responses.PrepareGameResponse;
import sc.protocol.responses.RoomPacket;
import sc.shared.GameResult;
import sc.shared.PlayerScore;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreCause;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import sc.shared.SlotDescriptor;

import com.thoughtworks.xstream.XStream;

public abstract class LobbyProtocol
{
	public static XStream registerMessages(XStream xStream)
	{
		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				ErrorResponse.class, GamePausedEvent.class,
				JoinGameResponse.class, LeftGameEvent.class,
				MementoPacket.class, PrepareGameResponse.class,
				RoomPacket.class }));

		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				AuthenticateRequest.class, CancelRequest.class,
				FreeReservationRequest.class, JoinPreparedRoomRequest.class,
				JoinRoomRequest.class, ObservationRequest.class,
				PauseGameRequest.class, PrepareGameRequest.class,
				StepRequest.class }));
		
		registerAdditionalMessages(xStream, Arrays.asList(new Class<?>[] {
				GameResult.class, PlayerScore.class, ScoreAggregation.class,
				ScoreCause.class, ScoreDefinition.class, ScoreFragment.class,
				SlotDescriptor.class }));

		return xStream;
	}

	public static XStream registerAdditionalMessages(XStream xstream,
			Collection<Class<?>> protocolClasses)
	{
		for (Class<?> clazz : protocolClasses)
		{
			xstream.processAnnotations(clazz);
		}

		return xstream;
	}
}
