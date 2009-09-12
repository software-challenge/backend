/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sc.plugin2010.Action;
import sc.plugin2010.GameUtil;
import sc.plugin2010.renderer.RendererUtil;

/**
 * An class to display the information about the current game and the current
 * player on an Panel.
 * 
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class BorderInformationBar extends BackgroundPane
{

	private final String	CARROTCOUNT			= "Karottenanzahl";
	private final String	MOVESCOUNT			= "Maximale Zuganzahl durch Karotten möglich";
	private final String	SALADCOUNT			= "Salate";
	private final String	HASENJOKER			= "Hasenjoker";

	private final JLabel	carrots				= new JLabel(CARROTCOUNT);
	private final JLabel	maxfields			= new JLabel(MOVESCOUNT);
	private final JLabel	salads				= new JLabel(SALADCOUNT);
	private final JLabel	hasenjoker			= new JLabel(" ");

	private final Image		carrotsIcon			= RendererUtil
														.getImage("resource/game/carrots.png");
	private final Image		maxfieldIcon		= RendererUtil
														.getImage("resource/game/background.png");
	private final Image		saladIcon			= RendererUtil
														.getImage("resource/game/salad.png");
	private final Image		rabbitIcon			= RendererUtil
														.getImage("resource/game/rabbit.png");

	private final String	FONTTYPE			= "New Courier";
	private final int		SIZE				= 20;
	private int				iconsize			= 10;
	private final int		BIGGERCARROTSIZE	= 2;
	private final int		INDENT				= 15;

	private final Image		rabbit_carrot		= RendererUtil
														.getImage("resource/game/hasenjoker_carrots.png");
	private final Image		rabbit_salad		= RendererUtil
														.getImage("resource/game/hasenjoker_salad.png");
	private final Image		rabbit_forward		= RendererUtil
														.getImage("resource/game/hasenjoker_forward.png");
	private final Image		rabbit_backward		= RendererUtil
														.getImage("resource/game/hasenjoker_backward.png");

	private final JLabel	rabbit_carrotIcon	= new JLabel();
	private final JLabel	rabbit_saladIcon	= new JLabel();
	private final JLabel	rabbit_forwardIcon	= new JLabel();
	private final JLabel	rabbit_backwardIcon	= new JLabel();

	private BackgroundPane	rabbitjokerPane		= new BackgroundPane();
	private BackgroundPane	hasenjokerPanel		= new BackgroundPane();

	public BorderInformationBar(boolean red)
	{
		super();

		rabbit_carrotIcon.setToolTipText("20 Karotten nehmen oder abgeben");
		rabbit_saladIcon.setToolTipText("Friss sofort einen Salat");
		rabbit_forwardIcon.setToolTipText("Rücke eine Position vor");
		rabbit_backwardIcon.setToolTipText("Falle eine Position zurück");

		setDoubleBuffered(true);
		setBorder(BorderFactory.createEmptyBorder());

		setLayout(new VerticalFlowLayout(INDENT, this));

		rabbitjokerPane.setLayout(new BoxLayout(rabbitjokerPane,
				BoxLayout.Y_AXIS));

		salads.setToolTipText(SALADCOUNT);

		carrots.setHorizontalAlignment(JLabel.LEFT);
		maxfields.setHorizontalAlignment(JLabel.LEFT);
		hasenjoker.setHorizontalAlignment(JLabel.LEFT);

		carrots.setToolTipText(CARROTCOUNT);
		maxfields.setToolTipText(MOVESCOUNT);
		hasenjoker.setToolTipText(HASENJOKER);

		if (red)
		{
			setRedColor();
		}
		else
		{
			setBlueColor();
		}

		this.add(carrots);
		this.add(maxfields);
		this.add(salads);

		hasenjokerPanel.setLayout(new BoxLayout(hasenjokerPanel,
				BoxLayout.X_AXIS));

		hasenjokerPanel.add(hasenjoker);
		hasenjokerPanel.add(rabbitjokerPane);

		this.add(hasenjokerPanel);

		setVisible(true);
	}

	public void setIconSize(int size)
	{
		if (iconsize != size)
		{
			iconsize = size;

			ImageIcon TempIcon = new ImageIcon();
			TempIcon.setImage(rabbit_carrot.getScaledInstance(size, size,
					Image.SCALE_SMOOTH));
			rabbit_carrotIcon.setIcon(TempIcon);

			TempIcon = new ImageIcon();
			TempIcon.setImage(rabbit_salad.getScaledInstance(size, size,
					Image.SCALE_SMOOTH));
			rabbit_saladIcon.setIcon(TempIcon);

			TempIcon = new ImageIcon();
			TempIcon.setImage(rabbit_forward.getScaledInstance(size, size,
					Image.SCALE_SMOOTH));
			rabbit_forwardIcon.setIcon(TempIcon);

			TempIcon = new ImageIcon();
			TempIcon.setImage(rabbit_backward.getScaledInstance(size, size,
					Image.SCALE_SMOOTH));
			rabbit_backwardIcon.setIcon(TempIcon);

			carrots.setIcon(new ImageIcon(carrotsIcon.getScaledInstance(size,
					size, Image.SCALE_SMOOTH)));

			maxfields.setIcon(new ImageIcon(maxfieldIcon.getScaledInstance(
					size, size, Image.SCALE_SMOOTH)));

			salads.setIcon(new ImageIcon(saladIcon.getScaledInstance(size,
					size, Image.SCALE_SMOOTH)));

			hasenjoker.setIcon(new ImageIcon(rabbitIcon.getScaledInstance(size,
					size, Image.SCALE_SMOOTH)));

			carrots.setPreferredSize(new Dimension(180, size));
			maxfields.setPreferredSize(new Dimension(180, size));
			salads.setPreferredSize(new Dimension(180, size));

			hasenjokerPanel.setPreferredSize(new Dimension(180, size * 4));
		}
	}

	@Override
	public void setBackground(Image img)
	{
		int width = img.getWidth(this);

		Image hasenjokerBGImg = new BufferedImage(width, iconsize * 4,
				BufferedImage.TYPE_INT_ARGB);

		hasenjokerBGImg.getGraphics().drawImage(img, 0, 0, width, iconsize * 4,
				INDENT, iconsize * 3 + INDENT * 5, width,
				iconsize * 3 + INDENT * 5 + iconsize * 4, this);

		super.setBackground(img);
		hasenjokerPanel.setBackground(hasenjokerBGImg);
	}

	private void setRedColor()
	{
		Color mycolor = new Color(255, 0, 0, 255);

		setColor(mycolor);
	}

	private void setBlueColor()
	{
		Color mycolor = new Color(0, 0, 255, 255);
		setColor(mycolor);
	}

	private void setColor(Color col)
	{
		carrots.setForeground(col);
		carrots.setFont(new Font(FONTTYPE, Font.BOLD, SIZE + BIGGERCARROTSIZE));

		maxfields.setForeground(col);
		maxfields.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		salads.setForeground(col);
		salads.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		hasenjoker.setForeground(col);
		hasenjoker.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));
	}

	public void setAttributes(final int carrotCount, final int saladsCount,
			final List<Action> joker)
	{
		setMyAttributes(carrots, maxfields, carrotCount, saladsCount);
		setMyHasenjoker(joker);
	}

	private void setMyAttributes(JLabel carrots, JLabel maxfields,
			final int car, final int sal)
	{
		carrots.setText("<html><u>" + String.valueOf(car) + "</u></html>");
		maxfields.setText("<html>" + GameUtil.calculateMoveableFields(car)
				+ "</html>");
		salads.setText("<html>" + String.valueOf(sal) + "</html>");
	}

	private void setMyHasenjoker(final List<Action> joker)
	{
		rabbitjokerPane.removeAll();

		for (Action jo : joker)
		{
			switch (jo)
			{
				case TAKE_OR_DROP_CARROTS:
					rabbitjokerPane.add(rabbit_carrotIcon);
					break;
				case EAT_SALAD:
					rabbitjokerPane.add(rabbit_saladIcon);
					break;
				case FALL_BACK:
					rabbitjokerPane.add(rabbit_backwardIcon);
					break;
				case HURRY_AHEAD:
					rabbitjokerPane.add(rabbit_forwardIcon);
					break;
				default:
					break;
			}

		}
	}
}
