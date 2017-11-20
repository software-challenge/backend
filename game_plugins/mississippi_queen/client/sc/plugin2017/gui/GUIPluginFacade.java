package sc.plugin2017.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.networking.clients.ObservingClient;
import sc.plugin2017.EPlayerId;
import sc.plugin2017.GamePlugin;
import sc.plugin2017.GuiClient;
import sc.plugin2017.gui.renderer.RenderFacade;
import sc.plugin2017.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreDefinition;
import sc.shared.SlotDescriptor;

/**
 * This is the GUIPlugin interface that is loaded by the server when it loads
 * the plugins
 *
 * @author sca
 *
 */
@PluginDescriptor(name = GamePlugin.PLUGIN_NAME, uuid = GamePlugin.PLUGIN_UUID, author = GamePlugin.PLUGIN_AUTHOR)
public class GUIPluginFacade implements IGuiPlugin {

  private static final Logger logger = LoggerFactory
      .getLogger(GUIPluginFacade.class);

	public GUIPluginFacade() {
	}

	@Override
	public void setRenderContext(Panel panel) {
		RenderFacade.getInstance().setRenderContext(panel);
	}

	@Override
	public Image getCurrentStateImage() {
		return RenderFacade.getInstance().getImage();

	}

	@Override
	public String getPluginInfoText() {
		return GamePlugin.PLUGIN_NAME;
	}

	@Override
	public Image getPluginIcon() {
		return loadImage("resource/game/pluginIcon.png");
	}

	@Override
	public Image getPluginImage() {
		return loadImage("resource/game/pluginImage.png");
	}

	private Image loadImage(String filename) {
	  logger.debug("loading image {}", filename);
	  ImageIcon icon = new ImageIcon(filename);
	  return icon.getImage();
	}

	/**
	 * Server wants us to prepare a game Then create a GuiClient that opens a
	 * new room and create the GUI
	 */
	@Override
	public IGamePreparation prepareBackgroundGame(final String ip, final int port,
			SlotDescriptor... descriptors) throws IOException {
		GuiClient client = new GuiClient(ip, port, EPlayerId.OBSERVER);
		AdministrativeGameHandler handler = new AdministrativeGameHandler();
		client.setHandler(handler);
		RenderFacade.getInstance().setHandler(handler, EPlayerId.OBSERVER);
		GamePreparation result = new GamePreparation(client, descriptors);
		RenderFacade.getInstance().setDisabled(true);
		return result;
	}


	/**
	 * Server wants us to prepare a game Then create a GuiClient that opens a
	 * new room and create the GUI
	 */
	@Override
	public IGamePreparation prepareGame(final String ip, final int port,
			SlotDescriptor... descriptors) throws IOException {
		GuiClient client = new GuiClient(ip, port, EPlayerId.OBSERVER);
		AdministrativeGameHandler handler = new AdministrativeGameHandler();
		client.setHandler(handler);
		RenderFacade.getInstance().setHandler(handler, EPlayerId.OBSERVER);
		GamePreparation result = new GamePreparation(client, descriptors);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		return result;
	}

	/**
	 * Server wants us to load a replay.
	 */
	@Override
	public IObservation loadReplay(String filename) throws IOException {
		ObservingClient rep = new ObservingClient(Configuration.getXStream(),
				ReplayBuilder.loadReplay(filename));
		ObserverGameHandler handler = new ObserverGameHandler();
		RenderFacade.getInstance().setHandler(null, EPlayerId.OBSERVER);
		IObservation obs = new Observation(rep, handler);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		return obs;
	}

	@Override
	public int getMinimalPlayerCount() {
		return 2;
	}

	@Override
	public int getMaximalPlayerCount() {
		return 2;
	}

	@Override
	public int getPluginYear() {
		return GamePlugin.PLUGIN_YEAR;
	}

	@Override
	public void initialize(IGuiPluginHost host) {
		// not needed
	}

	@Override
	public void unload() {
		// not needed
	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		return GamePlugin.SCORE_DEFINITION;
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		if (screen.width > 1024) {
			screen.width = 1024;
		}

		if (screen.height > 768) {
			screen.height = 768;
		}

		return screen;
	}

}
