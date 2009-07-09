package sc.logic;

import sc.common.IConfiguration;

public class GUIConfiguration implements IConfiguration {

	private static String pluginFolder = null;
	private ELanguage lang = ELanguage.DE;
	
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
		return pluginFolder ;
	}

}
