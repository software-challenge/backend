/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Vector;

/**
 * @author ffi
 * 
 */
public class HaseUndIgelLayout implements LayoutManager
{
	private final Vector<Integer>	levels;
	private final Vector<Component>	comps;
	private final int				BORDER	= 5;
	private final int				SIZEX	= 11;
	private final int				SIZEY	= 11;

	public HaseUndIgelLayout()
	{
		levels = new Vector<Integer>();
		comps = new Vector<Component>();
	}

	@Override
	public void addLayoutComponent(final String level, final Component comp)
	{
		Integer i;
		try
		{
			i = new Integer(level);
		}
		catch (final NumberFormatException e)
		{
			throw new IllegalArgumentException("Illegal level");
		}
		levels.addElement(i);
		comps.addElement(comp);
	}

	@Override
	public void removeLayoutComponent(final Component comp)
	{
		final int i = comps.indexOf(comp);
		if (i != -1)
		{
			levels.removeElementAt(i);
			comps.removeElementAt(i);
		}
	}

	@Override
	public Dimension preferredLayoutSize(final Container parent)
	{
		return minimumLayoutSize(parent);
	}

	@Override
	public Dimension minimumLayoutSize(final Container parent)
	{
		final Dimension d = new Dimension();

		d.width = 100;
		d.height = 100;

		return d;
	}

	private boolean between(final int i, final int x, final int y)
	{
		return (x <= i && y > i);
	}

	@Override
	public void layoutContainer(final Container parent)
	{

		Component c;

		int height = Math.min(parent.getWidth(), parent.getHeight());
		int width = height;

		int compWidth = (int) (Math.round(width - BORDER * 2) / SIZEX);
		int compHeight = (int) (Math.round(height - BORDER * 2) / SIZEY);

		int x = (parent.getWidth() - compWidth * SIZEX) / 2;
		int y = Math.min(parent.getHeight() - BORDER - compHeight, parent
				.getHeight()
				- ((parent.getHeight() - compHeight * SIZEY) / 2) - compHeight);

		for (int i = 0; i <= 64; i++)
		{
			c = parent.getComponent(i);
			c.setPreferredSize(new Dimension(compWidth, compHeight));

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			if (between(i, 0, 10) || between(i, 38, 46))
			{
				x += c.getPreferredSize().width;
			}
			else if (between(i, 10, 20) || between(i, 46, 52)
					|| between(i, 62, 65))
			{
				y -= c.getPreferredSize().height;
			}
			if (between(i, 20, 30) || between(i, 52, 55) || between(i, 59, 62))
			{
				x -= c.getPreferredSize().width;
			}
			else if (between(i, 30, 38) || between(i, 55, 59))
			{
				y += c.getPreferredSize().height;
			}
		}
	}
}
