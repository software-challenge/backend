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

	private final String		CARROTCOUNT			= "Karottenanzahl";
	private final String		MOVESCOUNT			= "Maximale Zuganzahl durch Karotten möglich";
	private final String		SALADCOUNT			= "Salate";
	private final String		HASENJOKER			= "Hasenjoker";

	private final JLabel		carrots				= new JLabel(CARROTCOUNT);
	private final JLabel		maxfields			= new JLabel(MOVESCOUNT);
	private final SaladPanel	salads				= new SaladPanel();
	private final JLabel		hasenjoker			= new JLabel(" ");

	private final String		FONTTYPE			= "New Courier";
	private final int			SIZE				= 14;
	private final int			ICONSIZE			= 30;
	private final int			BIGGERCARROTSIZE	= 4;
	private final int			INDENT				= 5;

	private final Image			rabbit_carrot		= RendererUtil
															.getImage("resource/game/hasenjoker_carrots.png");
	private final Image			rabbit_salad		= RendererUtil
															.getImage("resource/game/hasenjoker_salad.png");
	private final Image			rabbit_forward		= RendererUtil
															.getImage("resource/game/hasenjoker_forward.png");
	private final Image			rabbit_backward		= RendererUtil
															.getImage("resource/game/hasenjoker_backward.png");

	private final JLabel		rabbit_carrotIcon	= new JLabel();
	private final JLabel		rabbit_saladIcon	= new JLabel();
	private final JLabel		rabbit_forwardIcon	= new JLabel();
	private final JLabel		rabbit_backwardIcon	= new JLabel();

	private BackgroundPane		rabbitjokerPane		= new BackgroundPane();
	private BackgroundPane		hasenjokerPanel		= new BackgroundPane();

	public BorderInformationBar(boolean red)
	{
		super();

		int scaleValue = ICONSIZE;

		ImageIcon TempIcon = new ImageIcon();
		TempIcon.setImage(rabbit_carrot.getScaledInstance(scaleValue,
				scaleValue, Image.SCALE_SMOOTH));
		rabbit_carrotIcon.setIcon(TempIcon);
		rabbit_carrotIcon.setToolTipText("20 Karotten nehmen oder abgeben");

		TempIcon = new ImageIcon();
		TempIcon.setImage(rabbit_salad.getScaledInstance(scaleValue,
				scaleValue, Image.SCALE_SMOOTH));
		rabbit_saladIcon.setIcon(TempIcon);
		rabbit_saladIcon.setToolTipText("Friss sofort einen Salat");

		TempIcon = new ImageIcon();
		TempIcon.setImage(rabbit_forward.getScaledInstance(scaleValue,
				scaleValue, Image.SCALE_SMOOTH));
		rabbit_forwardIcon.setIcon(TempIcon);
		rabbit_forwardIcon.setToolTipText("Rücke eine Position vor");

		TempIcon = new ImageIcon();
		TempIcon.setImage(rabbit_backward.getScaledInstance(scaleValue,
				scaleValue, Image.SCALE_SMOOTH));
		rabbit_backwardIcon.setIcon(TempIcon);
		rabbit_backwardIcon.setToolTipText("Falle eine Position zurück");

		setDoubleBuffered(true);
		setBorder(BorderFactory.createEmptyBorder());

		setLayout(new VerticalFlowLayout(INDENT));

		rabbitjokerPane.setLayout(new BoxLayout(rabbitjokerPane,
				BoxLayout.Y_AXIS));

		salads.setIconSize(ICONSIZE);
		salads.setToolTipText(SALADCOUNT);

		carrots.setHorizontalAlignment(JLabel.LEFT);
		maxfields.setHorizontalAlignment(JLabel.LEFT);
		hasenjoker.setHorizontalAlignment(JLabel.LEFT);

		carrots.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/game/carrots.png").getScaledInstance(ICONSIZE,
				ICONSIZE, Image.SCALE_SMOOTH)));

		maxfields.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/game/background.png").getScaledInstance(ICONSIZE,
				ICONSIZE, Image.SCALE_SMOOTH)));

		hasenjoker.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/game/rabbit.png").getScaledInstance(ICONSIZE,
				ICONSIZE, Image.SCALE_SMOOTH)));

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

		carrots.setPreferredSize(new Dimension(180, ICONSIZE));
		maxfields.setPreferredSize(new Dimension(180, ICONSIZE));
		salads.setPreferredSize(new Dimension(180, ICONSIZE));

		this.add(carrots);
		this.add(maxfields);
		this.add(salads);

		hasenjokerPanel.setLayout(new BoxLayout(hasenjokerPanel,
				BoxLayout.X_AXIS));

		hasenjokerPanel.add(hasenjoker);
		hasenjokerPanel.add(rabbitjokerPane);

		hasenjokerPanel.setPreferredSize(new Dimension(180, ICONSIZE * 4));

		this.add(hasenjokerPanel);

		setVisible(true);
	}

	@Override
	public void setBackground(Image img)
	{
		int width = img.getWidth(this);

		Image saladBGImg = new BufferedImage(width, ICONSIZE,
				BufferedImage.TYPE_INT_ARGB);

		saladBGImg.getGraphics().drawImage(img, 0, 0, width, ICONSIZE, INDENT,
				(ICONSIZE + INDENT) * 2, INDENT + width,
				(ICONSIZE + INDENT) * 3, this);

		Image hasenjokerBGImg = new BufferedImage(width, ICONSIZE * 4,
				BufferedImage.TYPE_INT_ARGB);

		hasenjokerBGImg.getGraphics().drawImage(img, 0, 0, width, ICONSIZE * 4,
				INDENT, (ICONSIZE + INDENT) * 3 + 1, INDENT + width,
				(ICONSIZE + INDENT) * 3 + 1 + ICONSIZE * 4, this);

		super.setBackground(img);
		salads.setBackground(saladBGImg);
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
		salads.setSaladCount(sal);
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
