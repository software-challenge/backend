/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
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

	private final String	CARROTCOUNT	= "Karottenanzahl: ";
	private final String	ROUNDCOUNT	= "Rundenanzahl: ";
	private final String	MOVESCOUNT	= "Maximale Zuganzahl möglich: ";
	private final String	TURNCOUNT	= "An der Reihe: Spieler ";
	private final String	PLAYER		= "Spieler ";

	private final JLabel	carrots		= new JLabel();
	private final JLabel	rounds		= new JLabel();
	private final JLabel	maxmoves	= new JLabel();
	private final JLabel	turn		= new JLabel();
	private final JLabel	player		= new JLabel();

	public InformationBar(final boolean showcarrots)
	{
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridLayout(1, 1));

		final JPanel left = new JPanel();
		left.setLayout(new BorderLayout());

		final JPanel right = new JPanel();
		right.setLayout(new BorderLayout());

		player.setForeground(Color.BLUE);
		carrots.setForeground(Color.BLUE);
		maxmoves.setForeground(Color.BLUE);

		rounds.setHorizontalAlignment(JLabel.RIGHT);
		turn.setHorizontalAlignment(JLabel.RIGHT);

		rounds.setForeground(Color.RED);
		turn.setForeground(Color.RED);

		left.add(player, BorderLayout.NORTH);

		if (showcarrots)
		{
			left.add(carrots, BorderLayout.CENTER);
			left.add(maxmoves, BorderLayout.SOUTH);
		}
		right.add(rounds, BorderLayout.NORTH);
		right.add(turn, BorderLayout.CENTER);

		this.add(left);
		this.add(right);

		setCarrots(68);
		setRound(1);
		setTurn(1);
		setPlayer(1);

		setVisible(true);
	}

	public void setPlayer(final int id)
	{
		player.setText(PLAYER + String.valueOf(id));
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
