package sc.common;

import java.io.File;

import sc.logic.ConfigCreateGameDialog;
import sc.logic.GUIConfiguration.ELanguage;

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
}
