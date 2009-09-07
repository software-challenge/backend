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
 * @author starY
 * 
 */
public class UpperInformationBarLayout implements LayoutManager
{
	private final Vector<Integer>	levels;
	private final Vector<Component>	comps;

	public UpperInformationBarLayout()
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
		d.height = 80;

		return d;
	}

	private int getVerticalMiddle(Component c, int height)
	{
		return (height - c.getPreferredSize().height) / 2;
	}

	@Override
	public void layoutContainer(final Container parent)
	{

		Component c;

		int height = parent.getHeight();
		int width = parent.getWidth();

		int x = 5;
		int y;

		for (int i = 0; i < parent.getComponentCount(); i++)
		{
			c = parent.getComponent(i);

			y = getVerticalMiddle(c, height);

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);

			switch (i)
			{
				case 0:
					x += c.getPreferredSize().width + 50;
					c.setPreferredSize(new Dimension(200, 80));
					break;
				case 1:
					x += c.getPreferredSize().width + 40;
					break;
				case 2:
					x += c.getPreferredSize().width + 5;
					break;
				case 3:
					x += c.getPreferredSize().width + 50;
					break;
				default:
					break;
			}
		}
	}
}
