package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "slotDescriptor")
public class SlotDescriptor {

  public static final SlotDescriptor DEFAULT_DESCRIPTOR = new SlotDescriptor("Unknown");

  @XStreamAsAttribute
  private final String displayName;

  @XStreamAsAttribute
  private final boolean canTimeout;

  @XStreamAsAttribute
  private final boolean shouldBePaused;

  /** might be needed by XStream */
  public SlotDescriptor() {
    this.displayName = null;
    this.canTimeout = false;
    this.shouldBePaused = false;
  }

  public SlotDescriptor(String displayName) {
    this(displayName, true, true);
  }

  public SlotDescriptor(String displayName, boolean canTimeout,
                        boolean shouldBePaused) {
    this.displayName = displayName;
    this.canTimeout = canTimeout;
    this.shouldBePaused = shouldBePaused;
  }

  public SlotDescriptor(String displayName, boolean canTimeout) {
    this(displayName, canTimeout, true);
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public boolean isCanTimeout() {
    return this.canTimeout;
  }

  public boolean isShouldBePaused() {
    return this.shouldBePaused;
  }

  public String toString() {
    return String.format("SlotDescriptor{displayName=%s, canTimeout=%s, shouldBePaused=%s}", displayName, canTimeout, shouldBePaused);
  }

}
