package sc.server.gaming;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import sc.server.network.Client;

public abstract class ReservationManager
{
	private static Map<String, PlayerSlot>	reservations	= new HashMap<String, PlayerSlot>();

	public static PlayerSlot claimReservation(Client client, String reservation)
			throws UnknownReservationException
	{
		PlayerSlot result = reservations.get(reservation);

		if (result == null)
		{
			throw new UnknownReservationException();
		}
		else
		{
			result.setClient(client);
			return result;
		}
	}

	public synchronized static String reserve(PlayerSlot playerSlot)
	{
		String key = generateUniqueId();
		reservations.put(key, playerSlot);
		return key;
	}

	private synchronized static String generateUniqueId()
	{
		String key = UUID.randomUUID().toString();

		while (reservations.containsKey(key))
		{
			key = UUID.randomUUID().toString();
		}

		return key;
	}
}
