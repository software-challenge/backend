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
public class VerticalFlowLayout implements LayoutManager
{

	private final Vector<Integer>	levels;
	private final Vector<Component>	comps;
	private int						INDENT	= 5;

	public VerticalFlowLayout(int indent)
	{
		levels = new Vector<Integer>();
		comps = new Vector<Component>();
		INDENT = indent;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		final Dimension d = new Dimension();

		d.width = 100;
		d.height = 100;

		return d;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		return minimumLayoutSize(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	@Override
	public void removeLayoutComponent(Component comp)
	{
		final int i = comps.indexOf(comp);
		if (i != -1)
		{
			levels.removeElementAt(i);
			comps.removeElementAt(i);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	@Override
	public void layoutContainer(Container parent)
	{
		int x = INDENT;
		int y = INDENT;

		Component c;

		for (int i = 0; i < parent.getComponentCount(); i++)
		{
			c = parent.getComponent(i);
			c.setBounds(x, y, c.getPreferredSize().width,
					c.getPreferredSize().height);
			y += c.getPreferredSize().height + INDENT;
			if (i == 2)
			{
				y += c.getPreferredSize().height + INDENT;
			}
		}

	}
}
