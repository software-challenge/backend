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
		this.typ = typ;
		this.joker = joker;
		this.n = n;
	}
}
