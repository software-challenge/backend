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
import javax.swing.border.EtchedBorder;

import sc.plugin2010.Board;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FieldButton extends JButton
{
	private Image					img			= null;
	private Image					icon		= null;

	private final IClickObserver	obs;

	private Border					border;
	private final Border			defaultBorder;

	private int						fieldNumber	= 0;
	private String					mycolor		= "";
	private boolean					reachable	= false;
	private boolean					occupied	= false;
	private Board.FieldTyp			type;

	public FieldButton(final String imagefile, final int fieldNumber,
			final Board.FieldTyp type, final IClickObserver obs)
	{
		super();
		setType(type);
		this.fieldNumber = fieldNumber;
		this.obs = obs;
		defaultBorder = BorderFactory.createLineBorder(
				new Color(20, 20, 20, 40), 1);
		border = defaultBorder;

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
			border = createBorder(Color.RED);
			final MediaTracker mt = new MediaTracker(this);
			icon = Toolkit.getDefaultToolkit().getImage(
					"resource/" + color + ".png");
			mt.addImage(icon, 0);
			mycolor = color;
			occupied = true;
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

	private Border createBorder(final Color col)
	{
		return BorderFactory.createCompoundBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED, new Color(col
						.getRed(), col.getGreen(), col.getBlue(), 150),
						new Color(col.getRed(), col.getGreen(), col.getBlue(),
								70)), BorderFactory.createEtchedBorder(
				EtchedBorder.LOWERED, new Color(col.getRed(), col.getGreen(),
						col.getBlue(), 70), new Color(col.getRed(), col
						.getGreen(), col.getBlue(), 150)));
	}

	public void setReachable(final boolean reachable)
	{
		this.reachable = reachable;

		if (reachable)
		{
			border = createBorder(Color.BLUE);
		}
		else
		{
			border = defaultBorder;
		}
	}

	public void setFree()
	{
		icon = null;
		occupied = false;
	}

	public boolean needRepaint(final String color)
	{
		return (mycolor.equals(color) || reachable || occupied);
	}

	class ClickListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(final MouseEvent e)
		{
			border = createBorder(Color.RED);
		}

		@Override
		public void mouseExited(final MouseEvent e)
		{
			if (occupied)
			{
				border = createBorder(Color.ORANGE);
			}
			else if (reachable)
			{
				border = createBorder(Color.BLUE);
			}
			else
			{
				border = defaultBorder;
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
		g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		if (icon != null)
		{
			g.drawImage(icon, 5, 5, getWidth() - 20, getHeight() - 15, this);
		}
		final String text = String.valueOf(fieldNumber);
		g.drawString(text, getWidth() - 15, getHeight() - 5);
		setBorder(border);
	}

	public void setType(final Board.FieldTyp type)
	{
		this.type = type;
	}

	public Board.FieldTyp getType()
	{
		return type;
	}
}
