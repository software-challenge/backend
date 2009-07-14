package sc.common;

import sc.logic.ELanguage;

public interface IConfiguration {

	ELanguage getLanguage();
	void setLanguage(ELanguage language);
	
	void setCreateGameDialogPath(String createGameDialogPath);
	String getCreateGameDialogPath();
}
