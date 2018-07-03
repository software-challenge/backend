package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.SlotDescriptor;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("prepare")
public class PrepareGameRequest implements ILobbyRequest, IRequest<PrepareGameProtocolMessage> {
  
  @XStreamAsAttribute
  private final String gameType;
  
  @XStreamImplicit(itemFieldName = "slot")
  private final List<SlotDescriptor> slotDescriptors = new ArrayList<>();
  
  // i.e. GameState
  private Object loadGameInfo = null;
  
  /**
   * Create a Prepared Game <p>
   * Adds two players called Player1 and Player2
   *
   * @param gameType name of the game as String
   */
  public PrepareGameRequest(String gameType) {
    this(gameType, new SlotDescriptor("Player1"), new SlotDescriptor("Player2"));
  }
  
  /**
   * Create PrePareGameRequest with name and Descriptors for each player
   *
   * @param gameType    name of the Game as String
   * @param descriptor1 descriptor for Player 1
   * @param descriptor2 descriptor for Player 2
   */
  public PrepareGameRequest(String gameType, SlotDescriptor descriptor1, SlotDescriptor descriptor2) {
    this.gameType = gameType;
    this.slotDescriptors.add(descriptor1);
    this.slotDescriptors.add(descriptor2);
  }
  
  public Object getLoadGameInfo() {
    return this.loadGameInfo;
  }
  
  public String getGameType() {
    return this.gameType;
  }
  
  public List<SlotDescriptor> getSlotDescriptors() {
    return this.slotDescriptors;
  }
  
}
