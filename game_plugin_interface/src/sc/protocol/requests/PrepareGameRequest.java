package sc.protocol.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sc.protocol.IRequest;
import sc.protocol.responses.PrepareGameResponse;
import sc.shared.SlotDescriptor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("prepare")
public class PrepareGameRequest implements ILobbyRequest,
		IRequest<PrepareGameResponse>
{
	@XStreamAsAttribute
	private final String				gameType;

	@XStreamImplicit(itemFieldName = "slot")
	private final List<SlotDescriptor>	slotDescriptors;

	public PrepareGameRequest(String gameType, int playerCount)
	{

		this.gameType = gameType;
		this.slotDescriptors = new LinkedList<SlotDescriptor>();

		for (int i = 0; i < playerCount; i++)
		{
			this.slotDescriptors.add(new SlotDescriptor("Player "
					+ String.valueOf(i + 1)));
		}

		if (getPlayerCount() == 0)
		{
			throw new IllegalArgumentException("PlayerCount must be positive");
		}
	}

	public PrepareGameRequest(String gameType, SlotDescriptor... descriptors)
	{
		this.gameType = gameType;
		this.slotDescriptors = Arrays.asList(descriptors);

		if (getPlayerCount() == 0)
		{
			throw new IllegalArgumentException("PlayerCount must be positive");
		}
	}

	public String getGameType()
	{
		return this.gameType;
	}

	public int getPlayerCount()
	{
		return this.getSlotDescriptors().size();
	}

	public List<SlotDescriptor> getSlotDescriptors()
	{
		return this.slotDescriptors;
	}
}
