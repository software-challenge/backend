package sc.logic;

import java.util.ResourceBundle;

import sc.common.CouldNotFindAnyLanguageFile;
import sc.common.IConfiguration;

public interface ILogicFacade {

	ResourceBundle loadLanguageData(IConfiguration config) throws CouldNotFindAnyLanguageFile;
	
	IConfiguration loadConfiguration();
	void saveConfiguration(IConfiguration config);
}
