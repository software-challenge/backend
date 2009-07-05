package sc.plugin2010.framework;

/**
 * @author ffi
 * 
 */
public class Spielbrett
{
	public enum ESpielfeldTyp
	{
		SALAT, KAROTTEN, HASE, IGEL, POSITION_1, POSITION_2
	}

	public Spielbrett()
	{

	}

	public ESpielfeldTyp holeSpielfeldType(int feldNummer)
	{
		return ESpielfeldTyp.SALAT; // TODO
	}

	public boolean istSpielfeldBesetzt(int feldNummer)
	{
		return false; // TODO
	}

	public int werStehtAufFeld(int feldNummer)
	{
		return 0;
	}
}
