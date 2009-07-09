/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private final String	TURNCOUNT		= "An der Reihe: Spieler ";
	private final String	PLAYER			= "Spieler:";
	private final String	ENEMY			= "Gegner:";

	private final JLabel	carrots			= new JLabel(CARROTCOUNT);
	private final JLabel	enemycarrots	= new JLabel(CARROTCOUNT);
	private final JLabel	rounds			= new JLabel(ROUNDCOUNT);
	private final JLabel	maxmoves		= new JLabel(MOVESCOUNT);
	private final JLabel	enemymaxmoves	= new JLabel(MOVESCOUNT);
	private final JLabel	turn			= new JLabel(TURNCOUNT);
	private final JLabel	player			= new JLabel(PLAYER);
	private final JLabel	enemy			= new JLabel(ENEMY);

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
		maxmoves.setHorizontalAlignment(JLabel.CENTER);

		enemy.setHorizontalAlignment(JLabel.CENTER);
		enemycarrots.setHorizontalAlignment(JLabel.CENTER);
		enemymaxmoves.setHorizontalAlignment(JLabel.CENTER);

		rounds.setHorizontalAlignment(JLabel.LEFT);
		turn.setHorizontalAlignment(JLabel.LEFT);

		setColor(false);

		rounds.setForeground(light_black);
		rounds.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		turn.setForeground(light_black);
		turn.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		center.add(player, BorderLayout.NORTH);
		center.add(carrots, BorderLayout.CENTER);
		center.add(maxmoves, BorderLayout.SOUTH);

		right.add(enemy, BorderLayout.NORTH);
		right.add(enemycarrots, BorderLayout.CENTER);
		right.add(enemymaxmoves, BorderLayout.SOUTH);

		left.add(rounds, BorderLayout.NORTH);
		left.add(turn, BorderLayout.CENTER);

		this.add(left);
		this.add(center);
		this.add(right);

		setCarrots(68);
		setRound(1);
		setTurn(1);

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

		maxmoves.setForeground(mycolor);
		maxmoves.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemy.setForeground(enemycolor);
		enemy.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemycarrots.setForeground(enemycolor);
		enemycarrots.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		enemymaxmoves.setForeground(enemycolor);
		enemymaxmoves.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));
	}

	public void setCarrots(final int count)
	{
		carrots.setText(CARROTCOUNT + String.valueOf(count));
		maxmoves.setText(MOVESCOUNT + GameUtil.calculateMoveableFields(count));
	}

	public void setRound(final int count)
	{
		rounds.setText(ROUNDCOUNT + String.valueOf(count));

	}

	public void setTurn(final int playerid)
	{
		turn.setText(TURNCOUNT + String.valueOf(playerid));
	}
}
