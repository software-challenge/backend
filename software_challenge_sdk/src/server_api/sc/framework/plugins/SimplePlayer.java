package sc.framework.plugins;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.protocol.MoveRequest;
import sc.protocol.responses.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class SimplePlayer {
  public static final Logger logger = LoggerFactory.getLogger(SimplePlayer.class);
  
  @XStreamOmitField
  protected List<IPlayerListener> listeners;
  
  @XStreamOmitField
  private boolean canTimeout;
  
  @XStreamOmitField
  private boolean shouldBePaused;
  
  @XStreamAsAttribute
  private String displayName;
  
  @XStreamOmitField
  protected boolean violated = false;
  
  @XStreamOmitField
  protected boolean left = false;
  
  @XStreamOmitField
  protected boolean softTimeout = false;
  
  @XStreamOmitField
  protected boolean hardTimeout = false;
  
  @XStreamOmitField
  protected String violationReason = null;
  
  public SimplePlayer() {
    initListeners();
  }
  
  /** @return Reason for violation */
  public String getViolationReason() {
    return this.violationReason;
  }
  
  public void setViolationReason(String violationReason) {
    this.violationReason = violationReason;
  }
  
  public void addPlayerListener(IPlayerListener listener) {
    this.listeners.add(listener);
  }
  
  public void removePlayerListener(IPlayerListener listener) {
    this.listeners.remove(listener);
  }
  
  public void notifyListeners(ProtocolMessage o) {
    for (IPlayerListener listener : this.listeners) {
      listener.onPlayerEvent(o);
    }
  }
  
  public void requestMove() {
    MoveRequest request = new MoveRequest();
    
    for (IPlayerListener listener : this.listeners) {
      listener.onPlayerEvent(request);
    }
    
    logger.debug("Move requested.");
  }
  
  public final void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public final String getDisplayName() {
    return this.displayName;
  }
  
  public final void setCanTimeout(boolean canTimeout) {
    this.canTimeout = canTimeout;
  }
  
  public final void setShouldBePaused(boolean shouldBePaused) {
    this.shouldBePaused = shouldBePaused;
  }
  
  public boolean isCanTimeout() {
    return this.canTimeout;
  }
  
  public boolean isShouldBePaused() {
    return this.shouldBePaused;
  }
  
  public void setViolated(boolean violated) {
    this.violated = violated;
  }
  
  public boolean hasViolated() {
    return this.violated;
  }
  
  public void setLeft(boolean left) {
    this.left = left;
  }
  
  public boolean hasLeft() {
    return this.left;
  }
  
  public void setSoftTimeout(boolean timeout) {
    this.softTimeout = timeout;
  }
  
  public boolean hasSoftTimeout() {
    return this.softTimeout;
  }
  
  public void setHardTimeout(boolean timeout) {
    this.hardTimeout = timeout;
  }
  
  public boolean hasHardTimeout() {
    return this.hardTimeout;
  }
  
  /**
   * Initializes listeners, when they don't already exist. Only used for
   * playing on an imported state
   */
  public void initListeners() {
    if (this.listeners == null) {
      this.listeners = new ArrayList<>();
    }
  }
  
}
