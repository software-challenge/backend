package sc.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.helpers.StringHelper;
import sc.logic.save.GUIConfiguration;
import sc.plugin.GUIPluginInstance;
import sc.shared.SlotDescriptor;

public class HelperMethods {

    private static final Logger logger = LoggerFactory
            .getLogger(HelperMethods.class);

    private static final DateFormat dateTimeFormat = new SimpleDateFormat(
            "yyyy.MM.dd HH_mm_ss");

    /**
     * Executes a file indicated by <code>filename</code> with the given
     * <code>parameters</code>.
     *
     * @param filename
     * @param parameters
     * @throws IOException
     * @throws UnsupportedFileExtensionException
     */
    public static void exec(final String filename, final String[] parameters)
            throws IOException, UnsupportedFileExtensionException {
        final File file = new File(filename);
        final String ext = getFileExtension(file);
        /*
         * have to parse on my own because exec() split after each white space.
         * you must not add additional white spaces in the command array. the
         * processbuilder will add it automatically.
         */
        List<String> commandFragments = new LinkedList<String>();

        if (ext.equals(".jar")) {
            commandFragments.add("java");
            commandFragments.add("-jar");
            commandFragments.add("-Xmx" + GUIConfiguration.maxHeapSize + "m");
        } else if (ext.equals(".exe")) {
            // do nothing
        }
        /*else {
			throw new UnsupportedFileExtensionException();// TODO remove?
		}*/

        commandFragments.add(filename);
        commandFragments.addAll(Arrays.asList(parameters));

        logger.debug("Executing {}", StringHelper.join(commandFragments, " "));
        ProcessBuilder builder = new ProcessBuilder(commandFragments);
        builder.directory(file.getParentFile());
        builder.redirectErrorStream();
        final Process proc = builder.start();

        final Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(proc.getInputStream()));
                String line = "";
                try {
                    while ((line = input.readLine()) != null) {
                        logger.debug(line);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to redirect STDOUT.");
                }

                try {
                    int waitFor = proc.waitFor();
                    logger.debug("Process exited with ExitCode={}", waitFor);
                } catch (InterruptedException e) {
                    logger.error("Waiting interrupted. No exit code available. Killing process...");
                    proc.destroy();
                    logger.debug("Process killed");
                    //e.printStackTrace();
                }
            }
        });

        clientThread.setName("Client runner thread " + filename + " thread id " + clientThread.getId());
        clientThread.start();
    }

    /**
     * Returns the file extension of the given <code>file</code>. Otherwise, it
     * returns the filename of the given <code>file</code>.
     *
     * @param file
     * @return
     */
    public static String getFileExtension(File file) {
        String filename = file.getName();

        int index = filename.lastIndexOf('.');
        if (-1 != index) {
            return filename.substring(index);
        }

        return filename;
    }

    /**
     * Returns the filename without the file extension if present, i.e. it
     * simply removes the file extension at the end.
     *
     * @param filename
     * @return
     */
    public static String getFilenameWithoutFileExt(String filename) {
        int dotindex = filename.lastIndexOf('.');
        if (dotindex != -1) {
            return filename.substring(0, dotindex);
        } else {
            return filename;
        }
    }

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
    public static String generateReplayFilename(GUIPluginInstance plugin, List<SlotDescriptor> descriptors) {
        StringBuilder replayFilename = new StringBuilder("./replays/replay");
        replayFilename.append("_" + plugin.getDescription().uuid());
        for (int i = 0; i < descriptors.size(); i++) {
            replayFilename.append("_" + descriptors.get(i).getDisplayName().replace(' ', '_'));
        }
        replayFilename.append("_" + HelperMethods.getCurrentDateTime().replace(' ', '_') + ".xml");

        return replayFilename.toString();
    }

    public static int roundInteger(int value, int numbers) {
        double dValue = value;

        for (int i = 0; i < numbers; i++) {
            dValue = Math.round(dValue * 0.1);
        }

        long iValue = Math.round(dValue);

        for (int i = 0; i < numbers; i++) {
            iValue *= 10;
        }

        // since we got an INT as input this will possibly be an INT again
        return (int) iValue;
    }

    public static String millisecondsToString(int value) {
        if (Math.abs(value) < 1000) {
            return value + "ms";
        } else {
            return (value / 1000) + "s";
        }
    }
}
