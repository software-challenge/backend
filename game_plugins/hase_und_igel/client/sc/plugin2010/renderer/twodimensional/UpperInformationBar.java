/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sc.plugin2010.renderer.RendererUtil;

/**
 * An class to display the information about the current game and the current
 * player on an Panel.
 * 
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class UpperInformationBar extends BackgroundPane
{

	private final String			ROUNDCOUNT			= "Runde:";
	private final String			TURNCOUNT			= "<html><h1>An der Reihe: </h1> </html> ";
	private final String			PLAYER				= "<html><h1>Spieler:</h1></html>";
	private final String			ENEMY				= "<html><h1>Gegner:</h1></html>";

	private final JLabel			rounds				= new JLabel(ROUNDCOUNT);
	private final JLabel			turn				= new JLabel(TURNCOUNT);
	private final JLabel			player				= new JLabel(PLAYER);
	private final JLabel			enemy				= new JLabel(ENEMY);
	private final JLabel			turnicon			= new JLabel("");

	private final String			FONTTYPE			= "New Courier";
	private final int				SIZE				= 12;

	private final Image				blue				= RendererUtil
																.getImage("resource/game/blue.png");
	private final Image				red					= RendererUtil
																.getImage("resource/game/red.png");

	private final int				CENTERBORDERWIDTH	= 180;

	private final BackgroundPane	left				= new BackgroundPane();
	private final BackgroundPane	center				= new BackgroundPane();
	private final BackgroundPane	right				= new BackgroundPane();
	private final BackgroundPane	turnPanel			= new BackgroundPane();

	public UpperInformationBar()
	{
		super();

		setDoubleBuffered(true);

		setLayout(new BorderLayout());

		Color light_black = new Color(0, 0, 0, 180);

		left.setLayout(new BorderLayout());
		center.setLayout(new BorderLayout());
		right.setLayout(new BorderLayout());

		player.setHorizontalAlignment(JLabel.CENTER);

		enemy.setHorizontalAlignment(JLabel.CENTER);

		rounds.setHorizontalAlignment(JLabel.CENTER);
		turn.setHorizontalAlignment(JLabel.CENTER);

		setColor();

		rounds.setForeground(light_black);
		rounds.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		turn.setForeground(light_black);
		turn.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		left.add(player, BorderLayout.CENTER);

		center.add(rounds, BorderLayout.NORTH);

		turnPanel.setLayout(new FlowLayout());

		turnPanel.add(turn);
		turnPanel.add(turnicon);

		center.add(turnPanel, BorderLayout.SOUTH);

		right.add(enemy, BorderLayout.CENTER);

		left.setPreferredSize(new Dimension(CENTERBORDERWIDTH, 100));
		right.setPreferredSize(new Dimension(CENTERBORDERWIDTH, 100));

		this.add(left, BorderLayout.WEST);
		this.add(center, BorderLayout.CENTER);
		this.add(right, BorderLayout.EAST);

		setVisible(true);
	}

	@Override
	public void setBackground(Image img)
	{
		int width = img.getWidth(this);
		int height = img.getHeight(this);

		Image leftImg = new BufferedImage(CENTERBORDERWIDTH, height,
				BufferedImage.TYPE_INT_ARGB);

		Image centerImg = new BufferedImage(width - CENTERBORDERWIDTH * 2,
				height, BufferedImage.TYPE_INT_ARGB);

		Image centerTurnImg = new BufferedImage(width - CENTERBORDERWIDTH * 2,
				height / 2, BufferedImage.TYPE_INT_ARGB);

		Image rightImg = new BufferedImage(CENTERBORDERWIDTH, height,
				BufferedImage.TYPE_INT_ARGB);

		leftImg.getGraphics().drawImage(img, 0, 0, CENTERBORDERWIDTH, height,
				0, 0, CENTERBORDERWIDTH, height, this);

		centerImg.getGraphics().drawImage(img, 0, 0,
				width - CENTERBORDERWIDTH * 2, height, CENTERBORDERWIDTH, 0,
				width - CENTERBORDERWIDTH, height, this);

		centerTurnImg.getGraphics().drawImage(img, 0, 0,
				width - CENTERBORDERWIDTH * 2, height / 2, CENTERBORDERWIDTH,
				height / 2, width - CENTERBORDERWIDTH, height, this);

		rightImg.getGraphics().drawImage(img, 0, 0, CENTERBORDERWIDTH, height,
				width - CENTERBORDERWIDTH, 0, width, height, this);

		left.setBackground(leftImg);
		center.setBackground(centerImg);
		turnPanel.setBackground(centerTurnImg);
		right.setBackground(rightImg);
	}

	private void setColor()
	{
		Color mycolor = new Color(255, 0, 0, 255);
		Color enemycolor = new Color(0, 0, 255, 255);

		player.setForeground(mycolor);
		player.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemy.setForeground(enemycolor);
		enemy.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));
	}

	public void setPlayer(final String playerName)
	{
		player.setText("<html><h1>" + playerName + "</h1></html>");
	}

	public void setOtherPlayer(final String playerName)
	{
		enemy.setText("<html><h1>" + playerName + "</h1></html>");
	}

	public void setRound(final int count)
	{
		rounds.setText("<html><h1>" + ROUNDCOUNT + " " + String.valueOf(count)
				+ "</h1>" + "</html>");
	}

	public void setTurn(final String color)
	{
		ImageIcon icon = new ImageIcon();
		int scaleValue = 60;
		if (color.equals("blue"))
		{
			icon.setImage(blue.getScaledInstance(scaleValue, scaleValue,
					Image.SCALE_SMOOTH));
		}
		else
		{
			icon.setImage(red.getScaledInstance(scaleValue, scaleValue,
					Image.SCALE_SMOOTH));
		}
		turnicon.setIcon(icon);
	}
}
