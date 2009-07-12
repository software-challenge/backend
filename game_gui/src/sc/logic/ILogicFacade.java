package sc.logic;

import java.util.ResourceBundle;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.common.IConfiguration;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin.GUIPluginManager;

public interface ILogicFacade {

	/**
	 * Replay file extension.
	 */
	static final String EXT_REPLAY = ".rpl";

	void loadLanguageData() throws CouldNotFindAnyLanguageFileException;

	ResourceBundle getLanguageData();

	IConfiguration getConfiguration();

	void saveConfiguration(GUIConfiguration config);

	void loadPlugins() throws CouldNotFindAnyPluginException;
	void unloadPlugins();

	GUIPluginManager getPluginManager();

	void setObservation(IObservation observer);

	IObservation getObservation();

	void startServer(Integer port);
	void stopServer();
}
