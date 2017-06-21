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

	/* trying to fix "no-args constructor" problem of XStream */	
	public PrepareGameRequest(){
		this.gameType = null;
		this.slotDescriptors = null;
	}

	/**
	 * Create a Prepared Game
	 * @param gameType
	 */
	public PrepareGameRequest(String gameType)
	{
		this.gameType = gameType;
		this.slotDescriptors = new LinkedList<SlotDescriptor>();

		// Add two players, named Player1 and Player2
		this.slotDescriptors.add(new SlotDescriptor("Player "
					+ String.valueOf(1)));
		this.slotDescriptors.add(new SlotDescriptor("Player "
						+ String.valueOf(2)));

		if (this.getSlotDescriptors().size() == 0)
		{
			throw new IllegalArgumentException("PlayerCount must be positive");
		}
	}

  /**
   *
   * @param gameType
   * @param descriptors
   */
	public PrepareGameRequest(String gameType, SlotDescriptor... descriptors)
	{
		this.gameType = gameType;
		this.slotDescriptors = Arrays.asList(descriptors);

		if (this.getSlotDescriptors().size() == 0)
		{
			throw new IllegalArgumentException("PlayerCount must be positive");
		}
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
