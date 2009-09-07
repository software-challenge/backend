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
	private final int				INDENT	= 15;

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

		int x = INDENT;
		int y;

		int roundWidth = parent.getComponent(1).getPreferredSize().width + 40
				+ parent.getComponent(2).getPreferredSize().width + 5
				+ parent.getComponent(3).getPreferredSize().width;

		int roundStartX = width / 2 - roundWidth / 2;
		int leftFreeSpace = roundStartX
				- parent.getComponent(0).getPreferredSize().width - 10;
		int rightFreeSpace = width - (width / 2 + roundWidth / 2) - 10;

		for (int i = 0; i < parent.getComponentCount(); i++)
		{

			c = parent.getComponent(i);

			y = getVerticalMiddle(c, height);

			switch (i)
			{
				case 0:
					if (leftFreeSpace < 30)
					{
						c.setPreferredSize(new Dimension(
								c.getPreferredSize().width / 2, c
										.getPreferredSize().height * 2));
					}
					break;
				case 1:
					x = roundStartX;
					break;
				case 2:
					x += parent.getComponent(1).getPreferredSize().width + 40;
					break;
				case 3:
					x += parent.getComponent(2).getPreferredSize().width + 5;
					break;
				case 4:
					if (rightFreeSpace < 30)
					{
						c.setPreferredSize(new Dimension(
								c.getPreferredSize().width / 2, c
										.getPreferredSize().height * 2));
					}

					if (c.getPreferredSize().width < 180 - INDENT)
					{
						x = width - 180 + INDENT;
					}
					else
					{
						x = width - c.getPreferredSize().width - INDENT;
					}
				default:
					break;
			}

			if (i == 4)
			{

			}

			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);
		}
	}
}
