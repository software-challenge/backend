package sc.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sc.common.IConfiguration;

public class GUIConfiguration implements IConfiguration, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4635852675167221186L;

	private static final String CONFIG_FILENAME = "game_gui.conf";

	private static String pluginFolder = null;

	private ELanguage lang;

	private String createGameDialogPath;

	private int numTest;

	private String testDialogPath;

	private static GUIConfiguration instance;

	static {
		instance = load();
	}

	/**
	 * Constructs a configuration with default values.
	 */
	private GUIConfiguration() {
		this.lang = ELanguage.DE;
		this.createGameDialogPath = ".";
		this.numTest = 100;
		this.testDialogPath = ".";
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

	@Override
	public void setCreateGameDialogPath(String createGameDialogPath) {
		this.createGameDialogPath = createGameDialogPath;
	}

	@Override
	public String getCreateGameDialogPath() {
		return createGameDialogPath;
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

	@Override
	public String getTestDialogPath() {
		return this.testDialogPath;
	}

	@Override
	public void setTestDialogPath(String testDialogPath) {
		this.testDialogPath = testDialogPath;
	}
}
