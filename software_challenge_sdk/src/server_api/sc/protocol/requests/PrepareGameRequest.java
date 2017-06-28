package sc.protocol.requests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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


	private Object loadGameInfo = null;

	/**
	 * Create a Prepared Game
   * @param gameType name of the game as String
	 */
	public PrepareGameRequest(String gameType)
	{
		this.gameType = gameType;
		this.slotDescriptors = new LinkedList<>();

		// Add two players, named Player1 and Player2
		this.slotDescriptors.add(new SlotDescriptor("Player1"));
		this.slotDescriptors.add(new SlotDescriptor("Player2"));

		if (this.getSlotDescriptors().size() == 0)
		{
			throw new IllegalArgumentException("PlayerCount must be positive");
		}
	}

	/**
	 * Create PrePareGameRequest with name and Descriptors for each player
	 * @param gameType name of the Game as String
	 * @param descriptor1 descriptor for Player 1
	 * @param descriptor2 descriptor for Player 2
	 */
	public PrepareGameRequest(String gameType, SlotDescriptor descriptor1, SlotDescriptor descriptor2)
	{
		this.gameType = gameType;
		this.slotDescriptors = new LinkedList<>();

		// Add two players, named Player1 and Player2
		this.slotDescriptors.add(descriptor1);
		this.slotDescriptors.add(descriptor2);
	}

	public void setLoadGameInfo(Object info) {
		this.loadGameInfo = info;
	}

	public Object getLoadGameInfo() {
		return this.loadGameInfo;
	}

	public String getGameType()
	{
		return this.gameType;
	}

	public List<SlotDescriptor> getSlotDescriptors()
	{
		return this.slotDescriptors;
	}
}
