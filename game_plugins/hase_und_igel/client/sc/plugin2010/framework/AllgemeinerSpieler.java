/**
 * 
 */
package sc.plugin2010.framework;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2010.Player;
import sc.plugin2010.Player.Action;

/**
 * @author ffi
 * 
 */
public abstract class AllgemeinerSpieler
{
	private Player	player;

	/**
	 * Holt die Karotten, die der Spieler besitzt
	 * 
	 * @return Karottenvorrat des Spielers
	 */
	public int holeKarottenAnzahl()
	{
		return player.getCarrotsAvailable();
	}

	/**
	 * Holt die Salate, welche vom dem Spieler noch gefressen werden müssen,
	 * damit dieser ins Ziel darf.
	 * 
	 * @return Salatanzahl, welche noch zu fressen ist
	 */
	public int holeSalatAnzahl()
	{
		return player.getSaladsToEat();
	}

	/**
	 * Holt die aktuelle Spielerfarbe des Spielers.
	 * 
	 * @return
	 */
	public Spielerfarbe holeSpielerFarbe()
	{
		if (player.getColor() == Player.FigureColor.BLUE)
		{
			return Spielerfarbe.BLAU;
		}
		else
		{
			return Spielerfarbe.ROT;
		}
	}

	/**
	 * Holt das aktuelle Feld auf welchem der Spieler steht.
	 * 
	 * @return Feldnummer zwischen 0 und 65
	 */
	public int holeFeldnummer()
	{
		return player.getPosition();
	}

	public Zug holeLetztenZug()
	{
		player.getLastMove(); // TODO
		return null;
	}

	/**
	 * Holt die Hasenjoker, welche dieser Spieler noch besitzt.
	 */
	public List<Hasenjoker> holeHasenjoker()
	{
		List<Action> actions = player.getActions();
		List<Hasenjoker> result = new LinkedList<Hasenjoker>();

		for (Action action : actions)
		{
			switch (action)
			{
				case EAT_SALAD:
					result.add(Hasenjoker.FRISS_SALAT);
					break;
				case TAKE_OR_DROP_CARROTS:
					result.add(Hasenjoker.NIMM_ODER_GIB_20_KAROTTEN);
					break;
				case HURRY_AHEAD:
					result.add(Hasenjoker.RUECKE_VOR);
					break;
				case FALL_BACK:
					result.add(Hasenjoker.FALLE_ZURUECK);
					break;
				default:
					break;
			}
		}

		return result;
	}

	/**
	 * @param pu
	 */
	protected void update(Player player)
	{
		this.player = player;
	}

	protected Player getPlayer()
	{
		return player;
	}
}
