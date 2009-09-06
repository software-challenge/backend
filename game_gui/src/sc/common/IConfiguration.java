package sc.common;

import java.io.File;

import sc.logic.save.ConfigCreateGameDialog;
import sc.logic.save.GUIConfiguration.ELanguage;

public interface IConfiguration {

	ELanguage getLanguage();
	void setLanguage(ELanguage language);
	
	void setNumTest(int numTest);
	int getNumTest();
	
	File getCreateGameDialogPath();
	void setCreateGameDialogPath(String createGameDialogPath);
	
	File getTestDialogPath();
	void setTestDialogPath(String testDialogPath);
	
	File getLoadReplayPath();
	void setLoadReplayPath(String loadReplayPath);
	
	ConfigCreateGameDialog getConfigCreateGameDialog();
	
	public int getSpeedValue();
	public void setSpeedValue(int speedValue);
	
	boolean showWarnMsg();
	void setShowWarnMsg(boolean selected);
}
