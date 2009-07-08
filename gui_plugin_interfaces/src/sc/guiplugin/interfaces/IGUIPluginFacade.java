package sc.guiplugin.interfaces;

import java.awt.Image;
import java.io.IOException;

import javax.swing.JPanel;

public interface IGUIPluginFacade {

	/**
	 * sets the rendercontext. So that the game can be displayed on
	 * <code>panel</code>.
	 * 
	 * @param panel
	 *            JPanel instance on which the game should display
	 */
	void setRenderContext(JPanel panel, boolean threeDimensional);

	/**
	 * gets an Image of the current game state. External viewers that can not
	 * display jpanels need this.
	 * 
	 * @return Image of the current game state.
	 */
	Image getCurrentStateImage();

	String getPluginVersion();

	IGamePreparation prepareGame(String ip, int port) throws IOException;

}
