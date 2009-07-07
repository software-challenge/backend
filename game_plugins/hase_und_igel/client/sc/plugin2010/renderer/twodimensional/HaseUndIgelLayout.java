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
	private final double			SIZEX	= 10;
	private final double			SIZEY	= 11.1;

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
		Component c;
		final int indent;
		final Dimension d = new Dimension();
		Dimension componentDim;

		for (int i = 0; i < parent.getComponentCount(); i++)
		{
			c = parent.getComponent(i);
			componentDim = c.getMinimumSize();
			d.width = Math.max(d.width, componentDim.width);
			d.height += componentDim.height;
		}
		return d;
	}

	private Dimension getCenterPosition(final Container parent)
	{
		final Dimension result = new Dimension(0, 0);
		result.setSize(parent.getWidth() / 2, parent.getHeight() / 2);
		return result;
	}

	private Dimension getSpiralCoord(final double phi, final double a)
	{
		return new Dimension((int) Math.round(phi * a * Math.cos(phi)),
				(int) Math.round(phi * a * Math.sin(phi)));
	}

	@Override
	public void layoutContainer(final Container parent)
	{
		/*
		 * Component c; double degrees = 0; double a = 0; int beforeWidth = 0;
		 * int beforeHeight = 0; final Dimension centerSpiral =
		 * getCenterPosition(parent);
		 * 
		 * for (int i = 1; i <= parent.getComponentCount(); i++) { c =
		 * parent.getComponent(i - 1); c.setBounds(centerSpiral.width +
		 * getSpiralCoord(degrees, a).width, centerSpiral.height +
		 * getSpiralCoord(degrees, a).height, c .getPreferredSize().width,
		 * c.getPreferredSize().height);
		 * 
		 * degrees = degrees + 0.3; a = 20; beforeWidth =
		 * c.getPreferredSize().width; beforeHeight =
		 * c.getPreferredSize().height;
		 * 
		 * }
		 */

		Component c;

		int x = BORDER, y = BORDER;
		final int compWidth = (int) (Math.round(parent.getWidth() - BORDER * 2) / SIZEX);
		final int compHeight = (int) (Math.round(parent.getHeight() - BORDER
				* 2) / SIZEY);

		for (int i = 0; i <= 64; i++)
		{
			c = parent.getComponent(i);
			c.setPreferredSize(new Dimension(compWidth, compHeight));
		}

		for (int i = 0; i < 9; i++)
		{
			c = parent.getComponent(i);

			if (i % 2 == 0)
			{
				y = parent.getHeight() - c.getPreferredSize().height - BORDER
						- 5;
			}
			else
			{
				y = parent.getHeight() - c.getPreferredSize().height - BORDER;
			}

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x += c.getPreferredSize().width;
		}

		for (int i = 9; i < 19; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			y -= c.getPreferredSize().height;
		}

		for (int i = 19; i < 28; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x -= c.getPreferredSize().width;
		}

		for (int i = 28; i < 36; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			y += c.getPreferredSize().height;
		}

		for (int i = 36; i < 43; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x += c.getPreferredSize().width;
		}

		for (int i = 43; i < 49; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			y -= c.getPreferredSize().height;
		}

		for (int i = 49; i < 54; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x -= c.getPreferredSize().width;
		}

		for (int i = 54; i < 58; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			y += c.getPreferredSize().height;
		}

		for (int i = 58; i < 61; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x += c.getPreferredSize().width;
		}

		for (int i = 61; i < 63; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			y -= c.getPreferredSize().height;
		}

		for (int i = 63; i <= 64; i++)
		{
			c = parent.getComponent(i);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			x -= c.getPreferredSize().width;
		}
	}
}
