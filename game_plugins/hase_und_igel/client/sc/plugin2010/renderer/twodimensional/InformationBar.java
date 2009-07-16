/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
	private final JLabel	maxfields		= new JLabel(MOVESCOUNT);
	private final JLabel	enemymaxfields	= new JLabel(MOVESCOUNT);
	private final JLabel	salads			= new JLabel(SALADCOUNT);
	private final JLabel	enemysalads		= new JLabel(SALADCOUNT);
	private final JLabel	rounds			= new JLabel(ROUNDCOUNT);
	private final JLabel	hasenjoker		= new JLabel(HASENJOKER);
	private final JLabel	enemyhasenjoker	= new JLabel(HASENJOKER);
	private final JLabel	turn			= new JLabel(TURNCOUNT);
	private final JLabel	player			= new JLabel(PLAYER);
	private final JLabel	enemy			= new JLabel(ENEMY);
	private final JLabel	turnicon		= new JLabel("");

	private final String	FONTTYPE		= "New Courier";
	private final int		SIZE			= 12;
	private final int		ICONSIZE		= 24;

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
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setBackground(bg);

		final TransparentPanel center = new TransparentPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBackground(bg);

		final TransparentPanel right = new TransparentPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBackground(bg);

		player.setHorizontalAlignment(JLabel.LEFT);
		carrots.setHorizontalAlignment(JLabel.LEFT);
		maxfields.setHorizontalAlignment(JLabel.LEFT);
		salads.setHorizontalAlignment(JLabel.LEFT);
		hasenjoker.setHorizontalAlignment(JLabel.LEFT);

		enemy.setHorizontalAlignment(JLabel.LEFT);
		enemycarrots.setHorizontalAlignment(JLabel.LEFT);
		enemymaxfields.setHorizontalAlignment(JLabel.LEFT);
		enemysalads.setHorizontalAlignment(JLabel.LEFT);
		enemyhasenjoker.setHorizontalAlignment(JLabel.LEFT);

		rounds.setHorizontalAlignment(JLabel.LEFT);
		turn.setHorizontalAlignment(JLabel.LEFT);
		turnicon.setHorizontalAlignment(JLabel.LEFT);

		carrots.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/carrots.png").getScaledInstance(ICONSIZE, ICONSIZE,
				Image.SCALE_SMOOTH)));

		maxfields.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/background.png").getScaledInstance(ICONSIZE,
				ICONSIZE, Image.SCALE_SMOOTH)));

		salads.setIcon(new ImageIcon(RendererUtil
				.getImage("resource/salad.png").getScaledInstance(ICONSIZE,
						ICONSIZE, Image.SCALE_SMOOTH)));

		hasenjoker.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/rabbit.png").getScaledInstance(ICONSIZE, ICONSIZE,
				Image.SCALE_SMOOTH)));

		enemycarrots.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/carrots.png").getScaledInstance(ICONSIZE, ICONSIZE,
				Image.SCALE_SMOOTH)));

		enemymaxfields.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/background.png").getScaledInstance(ICONSIZE,
				ICONSIZE, Image.SCALE_SMOOTH)));

		enemysalads.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/salad.png").getScaledInstance(ICONSIZE, ICONSIZE,
				Image.SCALE_SMOOTH)));

		enemyhasenjoker.setIcon(new ImageIcon(RendererUtil.getImage(
				"resource/rabbit.png").getScaledInstance(ICONSIZE, ICONSIZE,
				Image.SCALE_SMOOTH)));

		setColor(false);

		rounds.setForeground(light_black);
		rounds.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		turn.setForeground(light_black);
		turn.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		left.add(rounds);
		left.add(turn);
		left.add(turnicon);

		center.add(player);
		center.add(carrots);
		center.add(maxfields);
		center.add(salads);
		center.add(hasenjoker);

		right.add(enemy);
		right.add(enemycarrots);
		right.add(enemymaxfields);
		right.add(enemysalads);
		right.add(enemyhasenjoker);

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

		maxfields.setForeground(mycolor);
		maxfields.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		salads.setForeground(mycolor);
		salads.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		hasenjoker.setForeground(mycolor);
		hasenjoker.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemy.setForeground(enemycolor);
		enemy.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemycarrots.setForeground(enemycolor);
		enemycarrots.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemymaxfields.setForeground(enemycolor);
		enemymaxfields.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemysalads.setForeground(enemycolor);
		enemysalads.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemyhasenjoker.setForeground(enemycolor);
		enemyhasenjoker.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));
	}

	public void setAttributes(final int carrotCount, final int saladsCount,
			final List<Action> joker)
	{
		setMyAttributes(carrots, maxfields, salads, carrotCount, saladsCount);
		setMyHasenjoker(hasenjoker, joker);
	}

	public void setEnemyAttributes(final int carrotCount,
			final int saladsCount, final List<Action> joker)
	{
		setMyAttributes(enemycarrots, enemymaxfields, enemysalads, carrotCount,
				saladsCount);
		setMyHasenjoker(enemyhasenjoker, joker);
	}

	private void setMyAttributes(JLabel carrots, JLabel maxfields,
			JLabel salads, final int car, final int sal)
	{
		carrots.setText("<html><u>" + CARROTCOUNT + "</u>" + " "
				+ String.valueOf(car) + "</html>");
		maxfields.setText("<html><u>" + MOVESCOUNT + "</u>" + " "
				+ GameUtil.calculateMoveableFields(car) + "</html>");
		salads.setText("<html><u>" + SALADCOUNT + "</u>" + " "
				+ String.valueOf(sal) + "</html>");
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
