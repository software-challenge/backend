/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FieldButton extends JButton
{
	private Image					img			= null;
	private Image					icon		= null;
	private int						fieldNumber	= 0;
	private final IClickObserver	obs;
	private String					mycolor		= "";
	private Border					bord;
	private boolean					reachable	= false;

	public FieldButton(final String imagefile, final int fieldNumber,
			final IClickObserver obs)
	{
		super();
		// this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		this.fieldNumber = fieldNumber;
		this.obs = obs;
		addMouseListener(new ClickListener());
		if (imagefile != null)
		{
			final MediaTracker mt = new MediaTracker(this);
			img = Toolkit.getDefaultToolkit().getImage(imagefile);
			mt.addImage(img, 0);
			try
			{
				mt.waitForAll();
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getFieldnumber()
	{
		return fieldNumber;
	}

	public void setOccupied(final String color)
	{
		if (color != null)
		{
			final MediaTracker mt = new MediaTracker(this);
			icon = Toolkit.getDefaultToolkit().getImage(
					"resource/" + color + ".png");
			mt.addImage(icon, 0);
			mycolor = color;
			try
			{
				mt.waitForAll();
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setReachable(final boolean reachable)
	{
		this.reachable = reachable;

		if (reachable)
		{
			bord = BorderFactory.createLineBorder(Color.BLUE, 2);
		}
		else
		{
			bord = null;
		}
	}

	public void setFree()
	{
		icon = null;
		mycolor = "";
	}

	public boolean wasColorOn(final String color)
	{
		return mycolor.equals(color);
	}

	class ClickListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(final MouseEvent e)
		{
			bord = BorderFactory.createLineBorder(Color.RED, 2);
		}

		@Override
		public void mouseExited(final MouseEvent e)
		{
			if (reachable)
			{
				bord = BorderFactory.createLineBorder(Color.BLUE, 2);
			}
			else
			{
				bord = null;
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				obs.updateClicked(getFieldnumber());
			}
		}
	}

	@Override
	protected void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		if (icon != null)
		{
			g.drawImage(icon, 5, 5, getWidth() - 20, getHeight() - 15, this);
		}
		final String text = String.valueOf(fieldNumber);
		g.drawString(text, getWidth() - 15, getHeight() - 5);
		setBorder(bord);
	}
}
