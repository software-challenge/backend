package sc.plugin2010.framework;

/**
 * Eine Zug-Klasse, welche einen Zug in Hase und Igel repräsentiert. Dieser Zug
 * kann, zum Beispiel, SpieleHasenJoker oder Ziehe Felder vor sein.
 * 
 * Die Klasse ist eine Alternative zum Senden der Züge ohne Kara-Methode.
 * (Eignet sich vor allem für das fortgeschrittene Programmieren, wenn das
 * Programm größer wird.)
 * 
 * @author ffi
 * 
 */
public class Zug
{
	private Zugtyp		typ;
	private Hasenjoker	joker;
	private int			n;

	/**
	 * Erzeugt einen neuen Zug mit dem Zugyp <code>typ</code>
	 * 
	 * @param typ
	 *            Typ des Zuges aus <code>Zugtyp</code>
	 */
	public Zug(Zugtyp typ)
	{
		initialize(typ, null, 0);
	}

	/**
	 * Erzeugt einen neuen Zug mit dem Zugyp <code>typ</code> und einer
	 * gewünschten Anzahl <code>n</code>. Zum Beispiel repräsentiert
	 * <code>n</code> bei Figur ziehen, die Felderanzahl, welche gezogen werden
	 * soll.
	 * 
	 * @param typ
	 *            Typ des Zuges aus <code>Zugtyp</code>
	 * @param n
	 *            <code>n</code> bei Figur ziehen, die Felderanzahl, welche
	 *            gezogen werden soll. ACHTUNG: Bei Felderziehen gilt: relative
	 *            Felderanzahl und nicht absolute Felderanzahl wie bei
	 *            <code>spieler.setzeFigur()</code>.
	 */
	public Zug(Zugtyp typ, int n)
	{
		initialize(typ, null, n);
	}

	/**
	 * Erzeugt einen neuen Zug mit dem Zugyp <code>typ</code> und einer
	 * gewünschten Anzahl <code>n</code> und dem Hasenjoker <code>joker</code>.
	 * Zum Beispiel repräsentiert <code>n</code> bei Figur ziehen, die
	 * Felderanzahl, welche gezogen werden soll. Diese Methode ist vor allem für
	 * SPIELE_HASENJOKER da.
	 * 
	 * @param typ
	 *            Typ des Zuges aus <code>Zugtyp</code>
	 * @param joker
	 *            Welcher Hasenjoker gespielt werden soll. Nur bei
	 *            SPIELE_HASENJOKER wichtig.
	 */
	public Zug(Zugtyp typ, Hasenjoker joker)
	{
		initialize(typ, joker, 0);
	}

	/**
	 * Erzeugt einen neuen Zug mit dem Zugyp <code>typ</code> und einer
	 * gewünschten Anzahl <code>n</code> und dem Hasenjoker <code>joker</code>.
	 * Zum Beispiel repräsentiert <code>n</code> bei Figur ziehen, die
	 * Felderanzahl, welche gezogen werden soll. Diese Methode ist vor allem für
	 * SPIELE_HASENJOKER da. Bei dem Joker NIMM_ODER_GIB_20_KAROTTEN steht
	 * <code>n</code> für zum Beispiel 20 oder -20 oder 0. (Jeweils nehmen,
	 * abgeben, nichts tun)
	 * 
	 * @param typ
	 *            Typ des Zuges aus <code>Zugtyp</code>
	 * @param joker
	 *            Welcher Hasenjoker gespielt werden soll. Nur bei
	 *            SPIELE_HASENJOKER wichtig.
	 * @param n
	 *            <code>n</code> bei Figur ziehen, die Felderanzahl, welche
	 *            gezogen werden soll. ACHTUNG: Bei Felderziehen gilt: relative
	 *            Felderanzahl und nicht absolute Felderanzahl wie bei
	 *            <code>spieler.setzeFigur()</code>.
	 */
	public Zug(Zugtyp typ, Hasenjoker joker, int n)
	{
		initialize(typ, joker, n);
	}

	/**
	 * initialisieren der Zug-Klasse
	 * 
	 * @param typ
	 *            Typ des Zuges aus <code>Zugtyp</code>
	 * @param joker
	 *            Welcher Hasenjoker gespielt werden soll. Nur bei
	 *            SPIELE_HASENJOKER wichtig.
	 * @param n
	 *            <code>n</code> bei Feldern ziehen, die Felderanzahl, welche
	 *            gezogen werden soll. ACHTUNG: Bei Felderziehen gilt: relative
	 *            Felderanzahl und nicht absolute Felderanzahl wie bei
	 *            <code>spieler.setzeFigur()</code>.
	 */
	private void initialize(Zugtyp typ, Hasenjoker joker, int n)
	{
		setzeZugTyp(typ);
		setzeJoker(joker);
		setzeN(n);
	}

	/**
	 * setzt den Zugtyp des Zuges
	 * 
	 * @param typ
	 */
	public void setzeZugTyp(Zugtyp typ)
	{
		this.typ = typ;
	}

	/**
	 * Holt den Zugtyp des Zuges
	 * 
	 * @return
	 */
	public Zugtyp holeZugTyp()
	{
		return typ;
	}

	/**
	 * setzt den Hasenjoker des Zuges
	 * 
	 * @param joker
	 */
	public void setzeJoker(Hasenjoker joker)
	{
		this.joker = joker;
	}

	/**
	 * holt den Hasenjoker des Zuges
	 * 
	 * @return
	 */
	public Hasenjoker holeJoker()
	{
		return joker;
	}

	/**
	 * setzt die Anzahl n
	 * 
	 * @param n
	 */
	public void setzeN(int n)
	{
		this.n = n;
	}

	/**
	 * holt die Anzahl n
	 * 
	 * @return n
	 */
	public int holeN()
	{
		return n;
	}
}
