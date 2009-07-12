package sc.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.common.IConfiguration;
import sc.gui.stuff.YearComparator;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin.GUIPluginInstance;
import sc.plugin.GUIPluginManager;
import sc.server.Application;
import sc.server.Lobby;

public class LogicFacade implements ILogicFacade {

	/**
	 * Folder of all language files
	 */
	private static final String BASENAME = "sc/resource/game_gui";
	/**
	 * Configuration file name
	 */
	private static final String CONFIG_FILENAME = "game_gui.conf";
	/**
	 * Holds all vailable plugins
	 */
	private final GUIPluginManager pluginMan;
	private IConfiguration config;
	/**
	 * For multi-language support
	 */
	private ResourceBundle languageData;
	private IObservation observation;
	private Lobby server;

	/**
	 * Singleton instance
	 */
	private static volatile LogicFacade instance;

	private LogicFacade() { // Singleton
		this.pluginMan = GUIPluginManager.getInstance();
		this.config = this.loadConfiguration();
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

	private IConfiguration loadConfiguration() {
		IConfiguration result;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(CONFIG_FILENAME));
			result = (IConfiguration) in.readObject();
		} catch (FileNotFoundException e) {
			result = new GUIConfiguration();
		} catch (IOException e) {
			result = new GUIConfiguration();
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			result = new GUIConfiguration();
			// e.printStackTrace();
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
	public void saveConfiguration(GUIConfiguration config) {
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
	public void loadLanguageData() throws CouldNotFindAnyLanguageFileException {
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
			throw new CouldNotFindAnyLanguageFileException();
		}

		this.languageData = ResourceBundle.getBundle(BASENAME, locale);
	}

	@Override
	public GUIPluginManager getPluginManager() {
		return pluginMan;
	}

	@Override
	public void loadPlugins() throws CouldNotFindAnyPluginException {
		this.pluginMan.reload();
		if (this.pluginMan.getAvailablePlugins().size() == 0) {
			throw new CouldNotFindAnyPluginException();
		}
		this.pluginMan.activateAllPlugins(new IGuiPluginHost() {
			
		});
	}

	@Override
	public ResourceBundle getLanguageData() {
		return languageData;
	}

	@Override
	public IConfiguration getConfiguration() {
		return config;
	}

	@Override
	public IObservation getObservation() {
		return observation;
	}

	@Override
	public void setObservation(IObservation observer) {
		this.observation = observer;
	}

	@Override
	public void startServer(Integer port) {
		server = Application.startServer(port, false);
	}

	@Override
	public void stopServer() {
		if (server != null)
			server.close();
	}

	@Override
	public void unloadPlugins() {
		pluginMan.reload();
	}

	/**
	 * Returns available plugins in sorted order.
	 * 
	 * @return plugins
	 */
	public List<GUIPluginInstance> getAvailablePluginsSorted() {
		Collection<GUIPluginInstance> plugins = pluginMan.getAvailablePlugins();
		// sort by plugin's year
		List<GUIPluginInstance> sortedPlugins = new LinkedList<GUIPluginInstance>(plugins);
		Collections.sort(sortedPlugins, new YearComparator());
		return sortedPlugins;
	}

	/**
	 * Returns all available plugin names in a sorted order.
	 * 
	 * @param plugins
	 * @return
	 */
	public Vector<String> getPluginNames(List<GUIPluginInstance> plugins) {
		Vector<String> result = new Vector<String>();
		int last = 0;
		for (int i = 0; i < plugins.size(); i++) {
			GUIPluginInstance pluginInstance = plugins.get(i);
			if (pluginInstance.getPlugin().getPluginYear() > last) {
				result.add(pluginInstance.getDescription().name());
			}
		}

		return result;
	}

}
