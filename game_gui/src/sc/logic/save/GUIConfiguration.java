package sc.logic.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sc.common.IConfiguration;

public class GUIConfiguration implements IConfiguration, Serializable {

	private static final long serialVersionUID = -4635852675167221186L;
	
	private static final String CONFIG_FILENAME = "game_gui.conf";
	private static final String PATH_STD = ".";
	private static String pluginFolder = null;

	public enum ELanguage {
		DE, EN
	}
	
	private ELanguage lang;

	private String createGameDialogPath;

	private String testDialogPath;

	private String loadReplayPath;

	private int numTest;
	
	private final ConfigCreateGameDialog configCreateGameDialog = new ConfigCreateGameDialog();
	/**
	 * cursor of the speed bar
	 */
	private int speedValue;

	private final static GUIConfiguration instance;

	static {
		instance = load();
	}

	/**
	 * Constructs a configuration with default values.
	 */
	private GUIConfiguration() {
		this.lang = ELanguage.DE;
		this.createGameDialogPath = PATH_STD;
		this.testDialogPath = PATH_STD;
		this.loadReplayPath = PATH_STD;
		this.numTest = 100;
		this.speedValue = 100;
	}

	public static GUIConfiguration instance() {
		return instance;
	}

	@Override
	public ELanguage getLanguage() {
		return lang;
	}

	@Override
	public void setLanguage(ELanguage language) {
		this.lang = language;
	}

	public static void setPluginFolder(String string) {
		pluginFolder = string;
	}

	public static String getPluginFolder() {
		return pluginFolder;
	}

	private static GUIConfiguration load() {
		GUIConfiguration result = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(CONFIG_FILENAME));
			result = (GUIConfiguration) in.readObject();
			System.out.println("Loaded GUI Configuration");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		if (result == null) {
			System.err.println("Couldn't load GUI Configuration");
			return new GUIConfiguration();
		}

		return result;
	}

	/**
	 * Saves the current configuration
	 */
	public void save() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(CONFIG_FILENAME));
			out.writeObject(this);
			System.out.println("Saved Configuration");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public void setNumberOfTests(int numTest) {
		this.setNumTest(numTest);
	}

	@Override
	public void setNumTest(int numTest) {
		this.numTest = numTest;
	}

	@Override
	public int getNumTest() {
		return numTest;
	}

	/**
	 * Checks if the given <code>path</code> (still) exists. If so, it returns
	 * the path. Otherwise, it returns the execution path.
	 * 
	 * @param path
	 * @return
	 */
	private File checkPath(final String path) {
		File f = new File(path);
		if (!f.exists()) {
			return new File(PATH_STD);
		}
		return f;
	}

	@Override
	public File getCreateGameDialogPath() {
		return checkPath(createGameDialogPath);
	}

	@Override
	public void setCreateGameDialogPath(String createGameDialogPath) {
		this.createGameDialogPath = createGameDialogPath;
	}

	@Override
	public File getTestDialogPath() {
		return checkPath(testDialogPath);
	}

	@Override
	public void setTestDialogPath(String testDialogPath) {
		this.testDialogPath = testDialogPath;
	}

	@Override
	public File getLoadReplayPath() {
		return checkPath(loadReplayPath);
	}

	@Override
	public void setLoadReplayPath(String loadReplayPath) {
		this.loadReplayPath = loadReplayPath;
	}

	public ConfigCreateGameDialog getConfigCreateGameDialog() {
		return configCreateGameDialog;
	}

	public int getSpeedValue() {
		return this.speedValue;
	}

	public void setSpeedValue(int speedValue) {
		this.speedValue = speedValue;
	}

}
