package sc.plugin2011.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JPanel;

import edu.cau.plugins.PluginDescriptor;

import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IGuiPluginHost;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin2011.GuiClient;
import sc.shared.ScoreDefinition;
import sc.shared.SlotDescriptor;

@PluginDescriptor(author = "Sven Casimir", uuid = "fang_den_hut", name = "Fang den Hut")
public class GUIPluginFacade implements IGuiPlugin {

	@Override
	public Image getCurrentStateImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaximalPlayerCount() {
		return 2;
	}

	@Override
	public int getMinimalPlayerCount() {
		return 2;
	}

	@Override
	public Dimension getMinimumSize() {
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
	public Image getPluginIcon() {
		return null;
	}

	@Override
	public Image getPluginImage() {
		return null;
	}

	@Override
	public String getPluginInfoText() {
		return "Fang den Hut! Los. FANG IHN DOCH!";
	}

	@Override
	public int getPluginYear() {
		return 2011;
	}

	@Override
	public ScoreDefinition getScoreDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IObservation loadReplay(String filename) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Prepare a new game and send back the GamePreparation.
	 * The game preparation contains the observer which holds the events called by the server if something happens we might
	 * have to react to
	 */
	@Override
	public IGamePreparation prepareGame(String ip, int port,
			SlotDescriptor... descriptors) throws IOException {
		GuiClient client = new GuiClient();
		GamePreparation preparation = new GamePreparation(client, descriptors);
		return preparation;
	}

	@Override
	public void setRenderContext(JPanel panel, boolean threeDimensional) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(IGuiPluginHost host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

}
