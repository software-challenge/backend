/**
 * 
 */
package sc.plugin2010.framework;

/**
 * @author ffi
 * 
 */
public class Zug
{
	private Zugtyp		typ;
	private Hasenjoker	joker;
	private int			n;

	public Zug(Zugtyp typ)
	{
		initialize(typ, null, 0);
	}

	public Zug(Zugtyp typ, int n)
	{
		initialize(typ, null, n);
	}

	public Zug(Zugtyp typ, Hasenjoker joker, int n)
	{
		initialize(typ, joker, n);
	}

	private void initialize(Zugtyp typ, Hasenjoker joker, int n)
	{
		setzeZugTyp(typ);
		setzeJoker(joker);
		setzeN(n);
	}

	public void setzeZugTyp(Zugtyp typ)
	{
		this.typ = typ;
	}

	public Zugtyp holeZugTyp()
	{
		return typ;
	}

	public void setzeJoker(Hasenjoker joker)
	{
		this.joker = joker;
	}

	public Hasenjoker holeJoker()
	{
		return joker;
	}

	public void setzeN(int n)
	{
		this.n = n;
	}

	public int holeN()
	{
		return n;
	}
}
