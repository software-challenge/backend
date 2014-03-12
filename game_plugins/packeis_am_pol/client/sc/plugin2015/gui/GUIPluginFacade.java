package client.sc.plugin2015.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.networking.clients.ObservingClient;
import client.sc.plugin2015.EPlayerId;
import server.sc.plugin2015.GamePlugin;
import client.sc.plugin2015.GuiClient;
import client.sc.plugin2015.gui.renderer.FrameRenderer;
import client.sc.plugin2015.gui.renderer.RenderFacade;
import shared.sc.plugin2015.util.Configuration;
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

	public GUIPluginFacade() {
	}

	@Override
	public void setRenderContext(JPanel panel, boolean threeDimensional) {
		RenderFacade.getInstance().setRenderContext(panel, threeDimensional);
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
		return loadImage("resource/game/sheep.png");
	}

	@Override
	public Image getPluginImage() {
		return loadImage("resource/game/manhattan.png");
	}

	private static Image loadImage(String filename) {
		URL url = FrameRenderer.class.getClassLoader().getResource(filename);

		if (url == null) {
			return new ImageIcon().getImage();
		}
		return (new ImageIcon(url)).getImage();
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
		//RenderFacade.getInstance().setHandler(handler, EPlayerId.OBSERVER);
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
		//RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
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
