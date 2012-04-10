package sc.plugin2014.gui.renderer.game_configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RenderConfiguration {

    public static final int        ANTIALIASING = 0;
    public static final int        TRANSPARANCY = 1;
    public static final int        BACKGROUND   = 2;
    public static final int        MOVEMENT     = 3;
    public static final int        DEBUG_VIEW   = 4;

    public static final String[]   OPTION_NAMES = new String[] {
            "Kantenglättung", "Transparenz", "Hintergrundbild", "Animationen",
            "Debugansicht"                     };

    private static final boolean[] DEFAULTS     = new boolean[] { true, true,
            true, true, false, false           };

    public static final boolean[]  OPTIONS      = DEFAULTS.clone();

    public static void saveSettings() {

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (int i = 0; i < OPTIONS.length; i++) {
            map.put(OPTION_NAMES[i], new Boolean(OPTIONS[i]));
        }

        OutputStream fileStream = null;
        try {
            fileStream = new FileOutputStream("qwirkle_gui.conf");
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(map);
            objectStream.flush();
        }
        catch (IOException e) {}
        finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            }
            catch (Exception e) {}
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadSettings() {

        InputStream fileStream = null;

        try {
            fileStream = new FileInputStream("qwirkle_gui.conf");
            ObjectInputStream objectStream = new ObjectInputStream(fileStream);
            HashMap<String, Boolean> map = (HashMap<String, Boolean>) objectStream
                    .readObject();

            for (int i = 0; i < OPTIONS.length; i++) {
                Boolean option = map.get(OPTION_NAMES[i]);
                OPTIONS[i] = option == null ? DEFAULTS[i] : option;
            }

        }
        catch (IOException e) {}
        catch (ClassNotFoundException e) {}
        finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            }
            catch (Exception e) {}
        }
    }

}
