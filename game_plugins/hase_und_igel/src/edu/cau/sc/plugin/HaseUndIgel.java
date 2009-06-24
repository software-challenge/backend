package edu.cau.sc.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.IGamePluginHost;
import sc.api.plugins.IGame;
import sc.api.plugins.IGamePlugin;
import sc.api.plugins.PluginDescriptor;
import sc.api.plugins.protocol.IPacketFactory;

import edu.cau.sc.plugin.protocol.PacketFactory;

/**
 * The following description is taken from the german manual:
 * 
 * Hase Und Igel
 * Ein fesselendes Wettrennen für 2 bis 6  Spieler 
 * Inhalt: 1 Spielplan, 120 Spielkarten (90 Karotten-Karten, 18 Salat-Karten,
 * 12 Hasen-Karten), 6 Rennkarten, 6 Spielfiguren.
 * 
 * @author rra
 */
@PluginDescriptor(name = "Hase und Igel", uuid = "66c54fa5-8db6-4a72-91c2-cfd3a4e00f3a")
public class HaseUndIgel implements IGamePlugin
{
	protected static final Logger	logger	= LoggerFactory
													.getLogger(HaseUndIgel.class);

	@Override
	public void initialize(IGamePluginHost gameServer)
	{
		logger.info("Ich bin der Hase, direkt aus dem Plugin!");
	}

	@Override
	public IGame createGame()
	{
		return new GameInstance(3);
	}

	@Override
	public IPacketFactory getPacketFactory()
	{
		return PacketFactory.getInstance();
	}

	@Override
	public void unload()
	{
		// nothing to do
	}
}
