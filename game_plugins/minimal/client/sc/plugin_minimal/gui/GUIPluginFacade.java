package sc.plugin_minimal.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JPanel;

import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.networking.clients.ObservingClient;
import sc.plugin_minimal.EPlayerId;
import sc.plugin_minimal.GamePlugin;
import sc.plugin_minimal.GuiClient;
import sc.plugin_minimal.renderer.RenderFacade;
import sc.plugin_minimal.util.Configuration;
import sc.shared.ScoreDefinition;
import sc.shared.SlotDescriptor;
import edu.cau.plugins.PluginDescriptor;

/**
 * 
 * @author ffi
 * 
 */
@PluginDescriptor(author = "Florian Fittkau", uuid = "hui", name = "Hase und Igel")
public class GUIPluginFacade implements IGuiPlugin
{
	public GUIPluginFacade()
	{

	}

	@Override
	public void setRenderContext(JPanel panel, boolean threeDimensional)
	{
		RenderFacade.getInstance().setRenderContext(panel, threeDimensional);
	}

	@Override
	public Image getCurrentStateImage()
	{
		return RenderFacade.getInstance().getImage();

	}

	@Override
	public String getPluginInfoText()
	{
		return "<html>Die Nutzung des Spielkonzeptes \"Hase und Igel\" (Name, Spielregeln und Grafik) "
				+ "<BR> erfolgt mit freundlicher Genehmigung der Ravensburger Spieleverlag GmbH.</html>";
	}

	@Override
	public Image getPluginIcon()
	{
		return null;
	}

	@Override
	public IGamePreparation prepareGame(final String ip, final int port,
			SlotDescriptor... descriptors) throws IOException
	{
		GuiClient client = new GuiClient(ip, port, EPlayerId.OBSERVER);
		AdministrativeGameHandler handler = new AdministrativeGameHandler();
		client.setHandler(handler);
		RenderFacade.getInstance().createPanel(handler, EPlayerId.OBSERVER);
		GamePreparation result = new GamePreparation(client, descriptors);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		return result;
	}

	@Override
	public IObservation loadReplay(String filename) throws IOException
	{
		ObservingClient rep = new ObservingClient(Configuration.getXStream(),
				ReplayBuilder.loadReplay(filename));
		ObserverGameHandler handler = new ObserverGameHandler();
		RenderFacade.getInstance().createPanel(null, EPlayerId.OBSERVER);
		IObservation obs = new Observation(rep, handler);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		return obs;
	}

	@Override
	public int getMinimalPlayerCount()
	{
		return 2;
	}

	@Override
	public int getMaximalPlayerCount()
	{
		return 2;
	}

	@Override
	public int getPluginYear()
	{
		return 2010;
	}

	@Override
	public void initialize(IGuiPluginHost host)
	{
		// not needed
	}

	@Override
	public void unload()
	{
		// not needed
	}

	@Override
	public ScoreDefinition getScoreDefinition()
	{
		return GamePlugin.SCORE_DEFINITION;
	}

	@Override
	public Dimension getMinimumSize()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		if (screen.width > 1024)
		{
			screen.width = 1024;
		}

		if (screen.height > 768)
		{
			screen.height = 768;
		}

		return screen;
	}

	@Override
	public Image getPluginImage()
	{
		return null;
	}
}
