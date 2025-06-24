package sc.plugin2017.gui.renderer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author felix
 *
 */
public class RenderConfiguration {

	private static final Logger logger = LoggerFactory
			.getLogger(RenderConfiguration.class);
	
	/**
	 * The path where the Config is saved
	 */
	public static final String savePath = "hdfdf_gui.conf";

	/**
	 * The Strings of Renderers Processing can use
	 */
	public static final String[] RendererStrings = { "JAVA2D"/*, "P2D", "P3D" */};
	public static final Integer[] AntialiasingModes = { 0, 2, 4, 8 };

	public static final int RENDERER = 0;
	public static final int ANTIALIASING = 1;
	public static final int ANIMATION = 2;
	public static final int DEBUG_VIEW = 3;

	public static String optionRenderer = RendererStrings[0];
	public static int optionAntiAliasing = AntialiasingModes[2];
	public static boolean optionAnimation = true;
	public static boolean optionDebug = false;

	public static final String[] OPTION_NAMES = new String[] { "Renderer",
			"Kantenglättung", "Debugansicht" };

	public static void saveSettings() {
		// collect all set options and save them to File
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(OPTION_NAMES[0], optionRenderer);
		map.put(OPTION_NAMES[1], optionAntiAliasing);
		map.put(OPTION_NAMES[2], optionDebug);

		OutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(savePath);
			ObjectOutputStream outStream = new ObjectOutputStream(fileStream);
			outStream.writeObject(map);
			fileStream.flush();
			outStream.close();
		} catch (IOException e) {
			logger.warn("RenderConfiguration.saveSettings() - IOException");
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (Exception e) {

			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void loadSettings() {

		InputStream fileStream = null;

		try {
			fileStream = new FileInputStream(savePath);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			HashMap<String, Object> map = (HashMap<String, Object>) objectStream.readObject();
			optionRenderer = (String) map.get(OPTION_NAMES[0]);
			optionAntiAliasing = ((Number) map.get(OPTION_NAMES[1])).intValue();
			optionDebug = (Boolean) map.get(OPTION_NAMES[2]);

			objectStream.close();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
