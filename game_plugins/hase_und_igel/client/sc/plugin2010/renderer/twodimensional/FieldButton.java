/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import sc.plugin2010.FieldTyp;
import sc.plugin2010.FigureColor;
import sc.plugin2010.renderer.RendererUtil;

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
	private Border					oldBorder;
	private final Border			defaultBorder;

	private int						fieldNumber	= 0;
	private boolean					reachable	= false;
	private boolean					occupied	= false;
	private FieldTyp				type;

	private Color					turn;

	public FieldButton(final String imagefile, final int fieldNumber,
			final FieldTyp type, final IClickObserver obs)
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
			img = RendererUtil.getImage(imagefile);
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

	public void setBackground(final String bg)
	{
		if (bg != null)
		{
			final MediaTracker mt = new MediaTracker(this);
			img = RendererUtil.getImage(bg);
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

	public void setOccupied(final FigureColor color)
	{
		if (color != null)
		{

			final MediaTracker mt = new MediaTracker(this);
			String colorString = "blue";

			if (color == FigureColor.RED)
			{
				colorString = "red";
			}

			border = createBorder(Color.CYAN);

			icon = RendererUtil.getImage("resource/game/" + colorString
					+ ".png");
			mt.addImage(icon, 0);
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

	public void setReachable(final boolean reachable, final boolean red)
	{
		this.reachable = reachable;

		if (reachable)
		{
			if (red)
			{
				border = createBorder(Color.BLACK);
			}
			else
			{
				border = createBorder(Color.BLACK);
			}
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

	public boolean needRepaint()
	{
		// return (reachable || occupied);
		return true;
	}

	class ClickListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(final MouseEvent e)
		{
			oldBorder = border;
			border = createBorder(turn);
		}

		@Override
		public void mouseExited(final MouseEvent e)
		{
			if (occupied)
			{
				border = oldBorder;
			}
			else if (reachable)
			{
				border = oldBorder;
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

	public void setType(final FieldTyp type)
	{
		this.type = type;
	}

	public FieldTyp getType()
	{
		return type;
	}

	public void setTurnRed()
	{
		turn = Color.RED;
	}

	public void setTurnBlue()
	{
		turn = Color.BLUE;
	}
}
