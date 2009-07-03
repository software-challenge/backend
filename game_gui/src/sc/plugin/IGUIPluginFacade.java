package sc.plugin;

import java.awt.Image;
import javax.swing.JFrame;
import sc.api.plugins.IPlugin;

public interface IGUIPluginFacade {

	void setRenderContext(JFrame frame);
	Image getCurrentStateImage();
	String getPluginVersion(IPlugin plugin);
	//TODO IPlugin correct type for working with it?
	void startGame(IPlugin plugin, int playerCount);
	void stopGame();
	void pauseGame();
	void connectToServer(String ip, int port);
}
