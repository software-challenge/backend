package sc.logic;

import java.util.ResourceBundle;

import sc.common.CouldNotFindAnyLanguageFileException;
import sc.common.CouldNotFindAnyPluginException;
import sc.common.IConfiguration;
import sc.plugin.GUIPluginManager;

public interface ILogicFacade {

	void loadLanguageData() throws CouldNotFindAnyLanguageFileException;
	ResourceBundle getLanguageData();

	IConfiguration getConfiguration();
	void saveConfiguration(IConfiguration config);

	void loadPlugins() throws CouldNotFindAnyPluginException;
	GUIPluginManager getPluginManager();
}
