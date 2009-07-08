package sc;

import java.awt.Image;

import javax.swing.JPanel;

public interface IGUIPluginFacade {

	void setRenderContext(JPanel panel, boolean threeDimensional);

	Image getCurrentStateImage();

	String getPluginVersion();

	IGamePreparation prepareGame(String ip, int port);

}
