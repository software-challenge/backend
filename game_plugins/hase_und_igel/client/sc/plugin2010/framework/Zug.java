/**
 * 
 */
package sc.plugin2010.framework;

import sc.plugin2010.Move;

/**
 * @author ffi
 * 
 */
public class Zug
{
	private Move	move;

	public Zug(Zugtyp typ)
	{
		initialize(typ, 0);
	}

	public Zug(Zugtyp typ, int felderVorwaerts)
	{
		initialize(typ, felderVorwaerts);
	}

	private void initialize(Zugtyp typ, int felderVorwaerts)
	{
		Move.MoveTyp movetyp;

		switch (typ)
		{
			case FRISS_SALAT:
				movetyp = Move.MoveTyp.EAT;
				break;
			case ZIEHE_VORWAERTS:
				movetyp = Move.MoveTyp.MOVE;
				break;
			default:
				break;
		}
	}
}
