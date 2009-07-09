package sc.guiplugin.interfaces;

import java.awt.Image;
import java.io.IOException;

import javax.swing.JPanel;

import edu.cau.plugins.IPlugin;

public interface IGuiPlugin extends IPlugin<IGuiPluginHost> {

	/**
	 * 
	 * sets the rendercontext. So that the game can be displayed on
	 * <code>panel</code>.
	 * 
	 * @param panel
	 *            JPanel instance on which the game should display
	 * @param threeDimensional
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

	/**
	 * 
	 * @param ip
	 * @param port
	 * @param replayFilename
	 * @return
	 * @throws IOException
	 */
	IGamePreparation prepareGame(final String ip, int port) throws IOException;

	/**
	 * Loads the replay given by <code>filename</code>.
	 * 
	 * @param filename
	 * @return an IGamePreparation instance with an empty list of slots.
	 */
	IObservation loadReplay(final String filename);

	/**
	 * displays info text of the plugin. like "created by..."
	 * 
	 * @return
	 */
	String getPluginInfoText();
}
