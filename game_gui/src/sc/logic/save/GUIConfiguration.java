package sc.logic.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GUIConfiguration implements Serializable {

	private static final long serialVersionUID = -4635852675167221186L;

	private static final String CONFIG_FILENAME = "game_gui.conf";
	private static final String PATH_STD = ".";
	private static String pluginFolder = null;
	
	public static String replayFileToLoad = null;

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

	private boolean suppressWarnMsg;

	private boolean showLog;

	private boolean saveErrorGames;
	private boolean saveLostGames;
	private boolean saveWonGames;

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
		this.suppressWarnMsg = false;
		this.showLog = true;
		this.saveErrorGames = true;
		this.saveLostGames = false;
		this.saveWonGames = false;
	}

	public static GUIConfiguration instance() {
		return instance;
	}

	public ELanguage getLanguage() {
		return lang;
	}

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
			e.printStackTrace();
			System.out.println("Could not save configuration");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not save configuration");
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

	public void setNumTest(int numTest) {
		this.numTest = numTest;
	}

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

	public File getCreateGameDialogPath() {
		return checkPath(createGameDialogPath);
	}

	public void setCreateGameDialogPath(String createGameDialogPath) {
		this.createGameDialogPath = createGameDialogPath;
	}

	public File getTestDialogPath() {
		return checkPath(testDialogPath);
	}

	public void setTestDialogPath(String testDialogPath) {
		this.testDialogPath = testDialogPath;
	}

	public File getLoadReplayPath() {
		return checkPath(loadReplayPath);
	}

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

	public boolean suppressWarnMsg() {
		return suppressWarnMsg;
	}

	public void setSuppressWarnMsg(boolean selected) {
		this.suppressWarnMsg = selected;
	}

	public void setShowTestLog(boolean showLog) {
		this.showLog = showLog;
	}

	public boolean showTestLog() {
		return showLog;
	}

	public void setSaveErrorGames(boolean selected) {
		this.saveErrorGames = selected;
	}
	
	public boolean saveErrorGames() {
		return saveErrorGames;
	}

	public void setSaveLostGames(boolean selected) {
		this.saveLostGames = selected;
	}
	
	public boolean saveLostGames() {
		return saveLostGames;
	}

	public void setSaveWonGames(boolean selected) {
		this.saveWonGames = selected;
	}
	
	public boolean saveWonGames() {
		return saveWonGames;
	}

}
