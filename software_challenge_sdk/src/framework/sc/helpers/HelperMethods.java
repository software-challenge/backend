package sc.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.SlotDescriptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HelperMethods {

  private static final Logger logger = LoggerFactory
          .getLogger(HelperMethods.class);

  private static final DateFormat dateTimeFormat = new SimpleDateFormat(
          "yyyy.MM.dd HH_mm_ss");

  /**
   * Returns the current date and time as string formatted as yyyy.MM.dd
   * HH_mm_ss.
   *
   * @return current date and time
   */
  public static String getCurrentDateTime() {
    return dateTimeFormat.format(new Date());
  }

  /**
   * Returns a new generated filename for a replay file.
   *
   * @param descriptors
   * @return
   */
  public static String generateReplayFilename(String pluginUuid, List<SlotDescriptor> descriptors) {
    StringBuilder replayFilename = new StringBuilder("./replays/replay");
    replayFilename.append("_" + pluginUuid); // something like hui_2018
    for (int i = 0; i < descriptors.size(); i++) {
      replayFilename.append("_" + descriptors.get(i).getDisplayName().replace(' ', '_'));
    }
    replayFilename.append("_" + HelperMethods.getCurrentDateTime().replace(' ', '_') + ".xml");
    logger.debug("Generated file name {}", replayFilename.toString());
    return replayFilename.toString();
  }
}
