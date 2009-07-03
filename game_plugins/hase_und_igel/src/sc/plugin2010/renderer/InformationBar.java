/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sc.plugin2010.shared.util.GameUtil;

/**
 * @author ffi
 * 
 */
public class InformationBar extends JPanel
{

	private final String	CARROTCOUNT	= "Karottenanzahl: ";
	private final String	ROUNDCOUNT	= "Rundenanzahl: ";
	private final String	MOVESCOUNT	= "Maximale Zuganzahl möglich: ";
	private final String	TURNCOUNT	= "An der Reihe: ";

	private JLabel			carrots		= new JLabel();
	private JLabel			rounds		= new JLabel();
	private JLabel			maxmoves	= new JLabel();
	private JLabel			turn		= new JLabel();

	public InformationBar(boolean showcarrots)
	{
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new GridLayout(1, 1));

		JPanel left = new JPanel();
		left.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel right = new JPanel();
		right.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		carrots.setHorizontalAlignment(JLabel.LEFT);
		maxmoves.setHorizontalAlignment(JLabel.LEFT);

		rounds.setHorizontalAlignment(JLabel.RIGHT);
		turn.setHorizontalAlignment(JLabel.RIGHT);

		if (showcarrots)
		{
			left.add(carrots);
			left.add(maxmoves);
		}
		right.add(rounds);
		right.add(turn);

		this.add(left);
		this.add(right);

		setCarrots(0);
		setRound(0);
		setTurn(1);

		setVisible(true);
	}

	public void setCarrots(int count)
	{
		carrots.setText(CARROTCOUNT + String.valueOf(count));
		maxmoves.setText(MOVESCOUNT + GameUtil.calculateCarrots(count));
	}

	public void setRound(int count)
	{
		rounds.setText(ROUNDCOUNT + String.valueOf(count));

	}

	public void setTurn(int playerid)
	{
		turn.setText(TURNCOUNT + String.valueOf(playerid));
	}
}
