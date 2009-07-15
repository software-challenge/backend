package sc.guiplugin.interfaces;

import java.awt.Image;
import java.io.IOException;

import javax.swing.JPanel;

import sc.shared.ScoreDefinition;
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

	/**
	 * Returns the statistics information descriptions, e.g. "Eisschollen" or
	 * "Brücken"
	 * 
	 * @return the statistics information descriptions
	 */
	ScoreDefinition getScoreDefinition();

	/**
	 * 
	 * @param ip
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public IGamePreparation prepareGame(final String ip, final int port,
			int playerCount, String... displayNames) throws IOException;

	/**
	 * Loads the replay given by <code>filename</code>.
	 * 
	 * @param filename
	 * @return an IGamePreparation instance with an empty list of slots.
	 */
	IObservation loadReplay(final String filename) throws IOException;

	/**
	 * displays info text of the plugin. like "created by..."
	 * 
	 * @return
	 */
	String getPluginInfoText();

	/**
	 * Returns the minimal necessary number of players to play the game.
	 * 
	 * @return
	 */
	int getMinimalPlayerCount();

	/**
	 * Returns the year when the plugin has been played. SC2010 without SC for
	 * example
	 * 
	 * @return
	 */
	int getPluginYear();

	/**
	 * Returns the maximal number of players to play the game.
	 * 
	 * @return
	 */
	int getMaximalPlayerCount();
}
