package sc.plugin2010.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin2010.Client;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.renderer.RenderFacade;
import sc.protocol.ReplayClient;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author ffi
 * 
 */
public class GUIPluginFacade implements IGuiPlugin
{
	/**
	 * Singleton instance
	 */
	private static volatile GUIPluginFacade	instance;

	private GUIPluginFacade()
	{ // Singleton
	}

	public static GUIPluginFacade getInstance()
	{
		if (null == instance)
		{
			synchronized (GUIPluginFacade.class)
			{
				if (null == instance)
				{
					instance = new GUIPluginFacade();
				}
			}
		}
		return instance;
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
	public String getPluginVersion()
	{
		return "1.0"; // TODO
	}

	@Override
	public String getPluginInfoText()
	{
		return "Die Nutzung des Spielkonzeptes \"Hase und Igel\" (Name, Spielregeln und Grafik) erfolgt mit freundlicher Genehmigung der Ravensburger Spieleverlag GmbH.";
	}

	@Override
	public IGamePreparation prepareGame(final String ip, final int port,
			int playerCount, String filename) throws IOException
	{
		Client client = new Client(ip, port, EPlayerId.OBSERVER);
		client.setHandler(new GUIGameHandler(client));
		return new GamePreparation(client, playerCount);
	}

	@Override
	public IObservation loadReplay(String filename) throws IOException
	{
		ReplayClient rep = new ReplayClient(new XStream(), new File(filename));
		IObservation obs = new ReplayObservation(rep);
		return obs;
	}

	@Override
	public String[] getStatisticsInfo()
	{
		// TODO Auto-generated method stub
		return null;
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
}
