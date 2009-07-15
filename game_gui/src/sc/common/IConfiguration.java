package sc.common;

import sc.logic.ELanguage;

public interface IConfiguration {

	ELanguage getLanguage();
	void setLanguage(ELanguage language);
	
	void setCreateGameDialogPath(String createGameDialogPath);
	String getCreateGameDialogPath();
	
	void setNumTest(int numTest);
	int getNumTest();
	
	String getTestDialogPath();
	void setTestDialogPath(String testDialogPath);
	
	String getLoadReplayPath();
	void setLoadReplayPath(String loadReplayPath);
}
