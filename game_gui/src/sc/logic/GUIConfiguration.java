package sc.logic;

import sc.common.IConfiguration;

public class GUIConfiguration implements IConfiguration {

	private ELanguage lang = ELanguage.DE;
	
	@Override
	public ELanguage getLanguage() {
		return lang;
	}

	@Override
	public void setLanguage(ELanguage language) {
		this.lang = language;
	}

}
