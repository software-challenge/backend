package sc.logic;

import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.common.IConfiguration;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin.GUIPluginInstance;
import sc.plugin.GUIPluginManager;

public interface ILogicFacade {

	/**
	 * Replay file extension.
	 */
	public static final String APP_DIR = System.getProperty("user.dir");

	void loadLanguageData() throws CouldNotFindAnyLanguageFileException;

	Properties getLanguageData();

	IConfiguration getConfiguration();

	void loadPlugins() throws CouldNotFindAnyPluginException;
	void unloadPlugins();

	GUIPluginManager getPluginManager();

	void setObservation(IObservation observer);

	IObservation getObservation();

	void startServer(int port);
	void stopServer();
	
	List<GUIPluginInstance> getAvailablePluginsSorted();
	Vector<String> getPluginNames(List<GUIPluginInstance> plugins);
}
