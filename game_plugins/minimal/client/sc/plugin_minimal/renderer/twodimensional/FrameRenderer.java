/**
 * 
 */
package sc.plugin_minimal.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.FigureColor;
import sc.plugin_minimal.GameUtil;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.renderer.IRenderer;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer
{
	
	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;
	
	private IGameHandler			handler;
	private boolean					onlyObserving;
	private boolean					showing;
	private boolean					myturn;
	
	private JButton			moveButton;

	public FrameRenderer(final IGameHandler handler, final boolean onlyObserving)
	{
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		createInitFrame();
	}

	public void shown()
	{
		showing = true;
	}

	public void hidden()
	{
		showing = false;
	}

	private void createInitFrame()
	{
		setDoubleBuffered(true);

		moveButton = new JButton("Mache Zug");
		moveButton.setForeground(
					player.getColor() == FigureColor.RED ? Color.RED : Color.BLUE
				);
		moveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMove(new Move());
			}
		});
		add(moveButton);
		
		addMouseListener(new ClickRefresher());
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				if(showing) {
					repaint();
				}
			}
		});
	}

	@Override
	public void updatePlayer(final Player player, final Player otherPlayer)
	{
		this.repaint();

		this.player = player;
		enemy = otherPlayer;
		
		repaint();
	}

	@Override
	public void updateBoard(Board board, int round)
	{
		this.board = board;
	}

	private void askForAction(final Player player)
	{
		
	}
				
	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	private void sendMove(Move move)
	{
		if (myturn)
		{
			handler.sendAction(move);
			myturn = false;
		}
	}

	@Override
	public void requestMove()
	{
		myturn = true;
		
	}

	@Override
	public void gameEnded(final GameResult data, FigureColor color, String errorMessage)
	{		
	}

	private class ClickRefresher extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			FrameRenderer.this.repaint();
		}
	}

	@Override
	public void gameError(String errorMessage)
	{
	}

	@Override
	public void updateChat(String chatMsg) {
		// TODO Auto-generated method stub
		
	}
}
