package sc.logic;

import java.util.Locale;
import java.util.ResourceBundle;

import sc.common.CouldNotFindAnyLanguageFile;
import sc.common.IConfiguration;
import sc.common.IConfiguration.ELanguage;

public class LogicFacade implements ILogicFacade {

	/**
	 * Folder of all language files
	 */
	private static final String BASENAME = "sc/resources";

	/**
	 * Singleton instance
	 */
	private static volatile LogicFacade instance;

	private LogicFacade() { // Singleton
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveConfiguration(IConfiguration config) {
		// TODO Auto-generated method stub

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

}
