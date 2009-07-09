package sc.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import sc.common.CouldNotFindAnyLanguageFile;
import sc.common.IConfiguration;
import sc.common.IConfiguration.ELanguage;
import sc.plugin.GUIPluginManager;

public class LogicFacade implements ILogicFacade {

	/**
	 * Folder of all language files
	 */
	private static final String BASENAME = "sc/resources/game_gui";
	/**
	 * Configuration file name
	 */
	private static final String CONFIG_FILENAME = "game_gui.conf";
	/**
	 * Holds all vailable plugins
	 */
	private final GUIPluginManager pluginMan;
	/**
	 * Singleton instance
	 */
	private static volatile LogicFacade instance;

	private LogicFacade() { // Singleton
		this.pluginMan = new GUIPluginManager();
	}

	public static LogicFacade getInstance() {
		if (null == instance) {
			synchronized (LogicFacade.class) {
				if (null == instance) {
					instance = new LogicFacade();
				}
			}
		}
		return instance;
	}

	@Override
	public IConfiguration loadConfiguration() {
		IConfiguration result;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(CONFIG_FILENAME));
			result = (IConfiguration) in.readObject();
		} catch (FileNotFoundException e) {
			result = new GUIConfiguration();
		} catch (IOException e) {
			result = new GUIConfiguration();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			result = new GUIConfiguration();
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return result;
	}

	@Override
	public void saveConfiguration(IConfiguration config) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(CONFIG_FILENAME));
			out.writeObject(config);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	@Override
	public ResourceBundle loadLanguageData(IConfiguration config)
			throws CouldNotFindAnyLanguageFile {
		ELanguage language = config.getLanguage();
		Locale locale;
		switch (language) {
		case DE:
			locale = new Locale("de", "DE");
			break;
		case EN:
			locale = new Locale("en", "EN");
			break;
		default:
			throw new CouldNotFindAnyLanguageFile();
		}

		return ResourceBundle.getBundle(BASENAME, locale);
	}

	public GUIPluginManager getPluginMan() {
		return pluginMan;
	}

}
