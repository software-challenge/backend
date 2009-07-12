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
import javax.swing.JPanel;

import sc.plugin2010.Player.Action;
import sc.plugin2010.util.GameUtil;

/**
 * An class to display the information about the current game and the current
 * player on an Panel.
 * 
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class InformationBar extends JPanel
{

	private final String	CARROTCOUNT		= "Karottenanzahl: ";
	private final String	ROUNDCOUNT		= "Runde: ";
	private final String	MOVESCOUNT		= "Zuganzahl möglich: ";
	private final String	TURNCOUNT		= "An der Reihe:";
	private final String	PLAYER			= "Spieler:";
	private final String	ENEMY			= "Gegner:";
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

	public InformationBar()
	{

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridLayout(1, 1));

		Color light_black = new Color(0, 0, 0, 180);
		Color mycolor = new Color(0, 0, 255, 180);
		Color enemycolor = new Color(255, 0, 0, 180);

		final JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.setBackground(Color.WHITE);

		final JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		center.setBackground(Color.WHITE);

		final JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.setBackground(Color.WHITE);

		player.setHorizontalAlignment(JLabel.CENTER);
		carrots.setHorizontalAlignment(JLabel.CENTER);
		hasenjoker.setHorizontalAlignment(JLabel.CENTER);

		enemy.setHorizontalAlignment(JLabel.CENTER);
		enemycarrots.setHorizontalAlignment(JLabel.CENTER);
		enemyhasenjoker.setHorizontalAlignment(JLabel.CENTER);

		rounds.setHorizontalAlignment(JLabel.LEFT);
		turn.setHorizontalAlignment(JLabel.LEFT);
		turnicon.setHorizontalAlignment(JLabel.LEFT);

		setColor(false);

		rounds.setForeground(light_black);
		rounds.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		turn.setForeground(light_black);
		turn.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		center.add(player, BorderLayout.NORTH);
		center.add(carrots, BorderLayout.CENTER);
		center.add(hasenjoker, BorderLayout.SOUTH);

		right.add(enemy, BorderLayout.NORTH);
		right.add(enemycarrots, BorderLayout.CENTER);
		right.add(enemyhasenjoker, BorderLayout.SOUTH);

		left.add(rounds, BorderLayout.NORTH);
		left.add(turn, BorderLayout.CENTER);
		left.add(turnicon, BorderLayout.SOUTH);

		this.add(left);
		this.add(center);
		this.add(right);

		setVisible(true);
	}

	public void setColor(boolean iAmRed)
	{
		Color mycolor = new Color(0, 0, 255, 180);
		Color enemycolor = new Color(255, 0, 0, 180);

		if (iAmRed)
		{
			mycolor = new Color(255, 0, 0, 180);
			enemycolor = new Color(0, 0, 255, 180);
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

	public void setCarrots(final int count)
	{
		setMyCarrots(carrots, count);
	}

	public void setEnemyCarrots(final int count)
	{
		setMyCarrots(enemycarrots, count);
	}

	private void setMyCarrots(JLabel carrots, final int count)
	{
		carrots.setText("<html><center>" + CARROTCOUNT + String.valueOf(count)
				+ "</center><center>" + MOVESCOUNT
				+ GameUtil.calculateMoveableFields(count) + "</center></html>");
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
		String text = "<html><center>" + HASENJOKER + "</center>";
		for (Action jo : joker)
		{
			switch (jo)
			{
				case TAKE_OR_DROP_CARROTS:
					text = "<center>Du kannst 20 Karotten nehmen oder abgeben</center>";
					break;
				case EAT_SALAD:
					text = "<center>Friss sofort einen Salat</center>";
					break;
				case FALL_BACK:
					text = "<center>Gehe eine Position zurück</center>";
					break;
				case HURRY_AHEAD:
					text = "<center>Rücke eine Position vor</center>";
					break;
				default:
					break;
			}

		}
		label.setText(text + "</html>");
	}

	public void setRound(final int count)
	{
		rounds.setText(ROUNDCOUNT + String.valueOf(count));
	}

	public void setTurn(final String color)
	{
		ImageIcon icon = new ImageIcon("resource/" + color + ".png");
		icon.setImage(icon.getImage().getScaledInstance(40, 40,
				Image.SCALE_SMOOTH));
		turnicon.setIcon(icon);
	}
}
