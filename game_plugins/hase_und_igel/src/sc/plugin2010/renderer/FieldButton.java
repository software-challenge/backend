/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/**
 * @author ffi
 * 
 */
public class FieldButton extends JButton
{
	private Image			img			= null;
	private Image			icon		= null;
	private int				fieldNumber	= 0;
	private IClickObserver	obs;
	private String			mycolor		= "";

	public FieldButton(String imagefile, int fieldNumber, IClickObserver obs)
	{
		super();
		// this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		this.fieldNumber = fieldNumber;
		this.obs = obs;
		this.addMouseListener(new ClickListener());
		if (imagefile != null)
		{
			MediaTracker mt = new MediaTracker(this);
			img = Toolkit.getDefaultToolkit().getImage(imagefile);
			mt.addImage(img, 0);
			try
			{
				mt.waitForAll();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getFieldnumber()
	{
		return fieldNumber;
	}

	public void setOccupied(String color)
	{
		if (color != null)
		{
			MediaTracker mt = new MediaTracker(this);
			icon = Toolkit.getDefaultToolkit().getImage(
					"resource/" + color + ".png");
			mt.addImage(icon, 0);
			mycolor = color;
			try
			{
				mt.waitForAll();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setFree()
	{
		icon = null;
		mycolor = "";
	}

	public boolean wasColorOn(String color)
	{
		return mycolor.equals(color);
	}

	class ClickListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			// setBorder(BorderFactory.createLineBorder(Color.RED, 2));
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				obs.updateClicked(FieldButton.this.getFieldnumber());
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
		if (icon != null)
		{
			g.drawImage(icon, 5, 5, this.getWidth() - 20,
					this.getHeight() - 20, this);
		}
		String text = String.valueOf(fieldNumber);
		g.drawString(text, this.getWidth() - 15, this.getHeight() - 5);
	}
}
