package sc.plugin2010.framework;

import sc.plugin2010.Player;

/**
 * Diese Klassen repräsentiert den gegnerischen Spieler. Über sie können
 * Abfragen, wie die Position des Gegners, gemacht werden.
 * 
 * @author ffi
 * 
 */
public class Gegner
{
	private Player	player;

	public Gegner()
	{

	}

	public int holeKarottenAnzahl()
	{
		return player.getCarrotsAvailable();
	}

	public int holeSalatAnzahl()
	{
		return player.getSaladsToEat(); // TODO give salatsToEat
	}

	public int holeSpielerFarbe()
	{
		/*
		 * public enum FigureColor { RED, BLUE, YELLOW, WHITE, GREEN, ORANGE, }
		 * 
		 * return player.getColor();
		 */// TODO
		return 0;
	}

	public int holeAktuellesFeld()
	{
		return player.getPosition(); // TODO
	}

	public int holeLetztenZug()
	{
		return 0; // TODO
	}
}
