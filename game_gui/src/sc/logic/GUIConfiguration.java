package sc.logic;

import java.io.Serializable;

import sc.common.IConfiguration;

@SuppressWarnings("serial")
public class GUIConfiguration implements IConfiguration, Serializable {

	private static String pluginFolder = null;
	private ELanguage lang;

	public GUIConfiguration() {
		this.lang = ELanguage.DE;
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

}
