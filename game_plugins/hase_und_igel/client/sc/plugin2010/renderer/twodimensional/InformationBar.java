/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import sc.plugin2010.GameUtil;
import sc.plugin2010.Player.Action;
import sc.plugin2010.renderer.RendererUtil;

/**
 * An class to display the information about the current game and the current
 * player on an Panel.
 * 
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class InformationBar extends TransparentPanel
{

	private final String	CARROTCOUNT		= "Karottenanzahl:";
	private final String	ROUNDCOUNT		= "Runde:";
	private final String	MOVESCOUNT		= "Zuganzahl möglich:";
	private final String	TURNCOUNT		= "<html><h1> An der Reihe:</h1></html> ";
	private final String	PLAYER			= "<html><h1>Spieler:</h1></html>";
	private final String	ENEMY			= "<html><h1>Gegner:</h1></html>";
	private final String	SALADCOUNT		= "Salatanzahl:";
	private final String	HASENJOKER		= "Hasenjoker:";

	private final JLabel	carrots			= new JLabel(CARROTCOUNT);
	private final JLabel	enemycarrots	= new JLabel(CARROTCOUNT);
	private final JLabel	rounds			= new JLabel(ROUNDCOUNT);
	private final JLabel	hasenjoker		= new JLabel(HASENJOKER);
	private final JLabel	enemyhasenjoker	= new JLabel(HASENJOKER);
	private final JLabel	turn			= new JLabel(TURNCOUNT);
	private final JLabel	player			= new JLabel(PLAYER);
	private final JLabel	enemy			= new JLabel(ENEMY);
	private final JLabel	turnicon		= new JLabel("");

	private final String	FONTTYPE		= "New Courier";
	private final int		SIZE			= 12;

	private final Image		blue			= RendererUtil
													.getImage("resource/blue.png");
	private final Image		red				= RendererUtil
													.getImage("resource/red.png");

	public InformationBar()
	{
		setDoubleBuffered(true);

		setBorder(BorderFactory.createBevelBorder(3));

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridLayout(1, 1));

		Color light_black = new Color(0, 0, 0, 180);
		Color bg = new Color(255, 255, 255, 120);

		final TransparentPanel left = new TransparentPanel();
		left.setLayout(new BorderLayout());
		left.setBackground(bg);

		final TransparentPanel center = new TransparentPanel();
		center.setLayout(new BorderLayout());
		center.setBackground(bg);

		final TransparentPanel right = new TransparentPanel();
		right.setLayout(new BorderLayout());
		right.setBackground(bg);

		player.setHorizontalAlignment(JLabel.LEFT);
		carrots.setHorizontalAlignment(JLabel.LEFT);
		hasenjoker.setHorizontalAlignment(JLabel.LEFT);

		enemy.setHorizontalAlignment(JLabel.LEFT);
		enemycarrots.setHorizontalAlignment(JLabel.LEFT);
		enemyhasenjoker.setHorizontalAlignment(JLabel.LEFT);

		rounds.setHorizontalAlignment(JLabel.LEFT);
		turn.setHorizontalAlignment(JLabel.LEFT);
		turnicon.setHorizontalAlignment(JLabel.LEFT);

		setColor(false);

		rounds.setForeground(light_black);
		rounds.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		turn.setForeground(light_black);
		turn.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		left.add(rounds, BorderLayout.NORTH);
		left.add(turn, BorderLayout.CENTER);
		left.add(turnicon, BorderLayout.SOUTH);

		center.add(player, BorderLayout.NORTH);
		center.add(carrots, BorderLayout.CENTER);
		center.add(hasenjoker, BorderLayout.SOUTH);

		right.add(enemy, BorderLayout.NORTH);
		right.add(enemycarrots, BorderLayout.CENTER);
		right.add(enemyhasenjoker, BorderLayout.SOUTH);

		this.add(left);
		this.add(center);
		this.add(right);

		setVisible(true);
	}

	public void setColor(boolean iAmRed)
	{
		Color mycolor = new Color(0, 0, 255, 255);
		Color enemycolor = new Color(255, 0, 0, 255);

		if (iAmRed)
		{
			mycolor = new Color(255, 0, 0, 255);
			enemycolor = new Color(0, 0, 255, 255);
		}

		player.setForeground(mycolor);
		player.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		carrots.setForeground(mycolor);
		carrots.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		hasenjoker.setForeground(mycolor);
		hasenjoker.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemy.setForeground(enemycolor);
		enemy.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemycarrots.setForeground(enemycolor);
		enemycarrots.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemyhasenjoker.setForeground(enemycolor);
		enemyhasenjoker.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));
	}

	public void setAttributes(final int car, final int salads)
	{
		setMyAttributes(carrots, car, salads);
	}

	public void setEnemyAttributes(final int car, final int salads)
	{
		setMyAttributes(enemycarrots, car, salads);
	}

	private void setMyAttributes(JLabel carrots, final int car, final int sal)
	{
		carrots.setText("<html><u>" + CARROTCOUNT + "</u>" + " "
				+ String.valueOf(car) + "<BR><u>" + MOVESCOUNT + "</u>" + " "
				+ GameUtil.calculateMoveableFields(car) + "<BR><u>"
				+ SALADCOUNT + "</u>" + " " + String.valueOf(sal) + "</html>");
	}

	public void setHasenjoker(final List<Action> joker)
	{
		setMyHasenjoker(hasenjoker, joker);
	}

	public void setEnemyHasenjoker(final List<Action> joker)
	{
		setMyHasenjoker(enemyhasenjoker, joker);
	}

	private void setMyHasenjoker(JLabel label, final List<Action> joker)
	{
		String text = "<html><u>" + HASENJOKER + "</u>";
		String indent = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		for (Action jo : joker)
		{
			switch (jo)
			{
				case TAKE_OR_DROP_CARROTS:
					text += "<BR>" + indent + "20 Karotten nehmen oder abgeben";
					break;
				case EAT_SALAD:
					text += "<BR>" + indent + "Friss sofort einen Salat";
					break;
				case FALL_BACK:
					text += "<BR>" + indent + "Gehe eine Position zurück";
					break;
				case HURRY_AHEAD:
					text += "<BR>" + indent + "Rücke eine Position vor";
					break;
				default:
					break;
			}

		}
		label.setText(text + "</html>");
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
