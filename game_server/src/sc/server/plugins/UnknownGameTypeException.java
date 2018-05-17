package sc.server.plugins;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.helpers.StringHelper;

public class UnknownGameTypeException extends RescuableClientException
{

	/**  */
	private static final long	serialVersionUID = -6520842646313711672L;
	private Iterable<String>	availableUUIDs;

	public UnknownGameTypeException(String string, Iterable<String> iterable)
	{
		super(string);
		this.availableUUIDs = iterable;
	}

	@Override
	public String getMessage()
	{
		return "Unknown GameType UUID: " + super.getMessage() + " (available: "
				+ StringHelper.join(this.availableUUIDs, ",") + ")";
	}

}
