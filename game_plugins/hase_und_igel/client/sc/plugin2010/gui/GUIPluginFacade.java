package sc.plugin2010.gui;

import java.awt.Image;
import java.io.IOException;

import javax.swing.JPanel;

import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin2010.Client;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.GamePlugin;
import sc.plugin2010.GamePreparation;
import sc.plugin2010.Observation;
import sc.plugin2010.renderer.RenderFacade;
import sc.plugin2010.renderer.RendererUtil;
import sc.plugin2010.util.Configuration;
import sc.protocol.clients.ObservingClient;
import sc.shared.ScoreDefinition;
import edu.cau.plugins.PluginDescriptor;

/**
 * 
 * @author ffi
 * 
 */
@PluginDescriptor(author = "Florian Fittkau", uuid = "hui", version = "1.0", name = "Hase und Igel")
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

	public Image getPluginIcon()
	{
		return RendererUtil.getImage("resource/hase_und_igel_icon.png");
	}

	@Override
	public IGamePreparation prepareGame(final String ip, final int port,
			int playerCount, String filename) throws IOException
	{
		Client client = new Client(ip, port, EPlayerId.OBSERVER);
		GUIGameHandler handler = new GUIGameHandler(client);
		client.setHandler(handler);
		RenderFacade.getInstance().createPanel(handler, EPlayerId.OBSERVER);
		return new GamePreparation(client, playerCount);
	}

	@Override
	public IObservation loadReplay(String filename) throws IOException
	{
		ObservingClient rep = new ObservingClient(Configuration.getXStream(),
				ReplayBuilder.loadReplay(filename));
		ObserverGameHandler handler = new ObserverGameHandler();
		RenderFacade.getInstance().createPanel(null, EPlayerId.OBSERVER);
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		IObservation obs = new Observation(rep, handler);
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
}
