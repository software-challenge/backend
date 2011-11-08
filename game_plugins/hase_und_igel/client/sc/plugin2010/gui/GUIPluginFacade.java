package sc.plugin2010.gui;

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
import sc.plugin2010.EPlayerId;
import sc.plugin2010.GamePlugin;
import sc.plugin2010.GuiClient;
import sc.plugin2010.renderer.RenderFacade;
import sc.plugin2010.renderer.RendererUtil;
import sc.plugin2010.util.Configuration;
import sc.plugins.PluginDescriptor;
import sc.shared.ScoreDefinition;
import sc.shared.SlotDescriptor;

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
		return RendererUtil.getImage("resource/game/hase_und_igel_icon.png");
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
	public IGamePreparation prepareBackgroundGame(final String ip, final int port,
			SlotDescriptor... descriptors) throws IOException
	{
		return prepareGame(ip, port, descriptors);
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
		return RendererUtil
				.getImage("resource/game/hase_alternatives_icon.png");
	}
}
