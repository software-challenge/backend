package sc.server.gaming;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.server.network.Client;

public final class ReservationManager
{

	private static Logger					logger			= LoggerFactory
																	.getLogger(ReservationManager.class);
	private static Map<String, PlayerSlot>	reservations	= new HashMap<String, PlayerSlot>();

	private ReservationManager()
	{
		// singleton
	}

	/**
	 * If reservationcode is valid register client to playerslot and start game if all clients connected
	 * @param client
	 * @param reservation
	 * @return newly filled PlayerSlot
	 * @throws RescueableClientException
	 */
	public static synchronized PlayerSlot redeemReservationCode(Client client,
			String reservation) throws RescueableClientException
	{
		PlayerSlot result = reservations.remove(reservation);

		if (result == null)
		{
			throw new UnknownReservationException();
		}
		else
		{
			logger.info("Reservation {} was redeemed.", reservation);
			result.getRoom().fillSlot(result, client);
			return result;
		}
	}

	public synchronized static String reserve(PlayerSlot playerSlot)
	{
		if (reservations.containsValue(playerSlot))
		{
			throw new RuntimeException("This slot is already reserved.");
		}
		
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

	public static synchronized void freeReservation(String reservation)
	{
		PlayerSlot slot = reservations.remove(reservation);
		slot.free();
	}
}
