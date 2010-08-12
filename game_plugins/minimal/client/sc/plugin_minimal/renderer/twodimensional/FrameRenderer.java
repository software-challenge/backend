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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.Node;
import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.GameUtil;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.renderer.IRenderer;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

/**
 * @author ffi, sca
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer
{
	
	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;
	
	// We also have a game handler to know whats going on
	private IGameHandler			handler;
	
	// Am I only observing? Then don't show any controls or such
	private boolean					onlyObserving;
	
	// Am I currently visible?
	private boolean					showing;

	private boolean					myturn;
	
	/*
	 * Some example gui elements, not nice to create everything directly in here
	 */
	private JButton			moveButton;
	private JTextPane		actionPane;
	private JScrollPane		scrollPane;

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
	
	/**
	 * Create the GUI the first time
	 * Add all GUI elements to the panel here
	 */
	private void createInitFrame()
	{
		setDoubleBuffered(true);
		this.setLayout(new BorderLayout());
		
		moveButton = new JButton("Mache Zug");
		moveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMove(new Move());
			}
		});
		this.add(moveButton, BorderLayout.SOUTH);
		
		actionPane = new JTextPane();
		actionPane.setText("");
		actionPane.setEditable(false);
		actionPane.setAutoscrolls(true);
		actionPane.setDoubleBuffered(true);

		scrollPane = new JScrollPane(actionPane);
		scrollPane.setAutoscrolls(true);
		scrollPane.setDoubleBuffered(true);
		scrollPane.setPreferredSize(new Dimension(250, 300));

		this.add(scrollPane, BorderLayout.CENTER);
		
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

	/**
	 * Prints out the whole move history of both players to our example text pane
	 * @param redPlayer
	 * @param bluePlayer
	 */
	public void test() {
		
		
		actionPane.setText("test");
		if(board != null){
			
			for (Node node: board.getNodes()){
				actionPane.setText(actionPane.getText() + "\n" + node.index);
			}
			
			
		}
		
	}
	
	/**
	 * Player got updated, let the gui react to it
	 */
	@Override
	public void updatePlayer(final Player player, final Player otherPlayer)
	{

		this.player = player;
		enemy = otherPlayer;
		
		// Set the color of the move button depending on what color I have
		// Only needs to be done once because this panel is directly connected to the player
		moveButton.setForeground(
				player.getColor() == PlayerColor.PLAYER1 ? Color.RED : Color.BLUE
			);
		
		test();
		
		repaint();
	}

	/**
	 * Board got updated, update local things
	 */
	@Override
	public void updateBoard(Board board, int round)
	{
		
		test();
		this.board = board;
		
	}

	/**
	 * Create Image of the current gui view
	 */
	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}

	/**
	 * Send given move (using the game handler)
	 * @param move
	 */
	private void sendMove(Move move)
	{
		if (myturn)
		{
			handler.sendAction(move);
			myturn = false;
		}
	}

	/**
	 * Move is requested
	 */
	@Override
	public void requestMove()
	{
		// For this example, only recognize that it's our turn
		// We don't need to disable the move button because it will be invisible until next turn anyway
		myturn = true;
	}

	/**
	 * Game is over, print out game result
	 */
	@Override
	public void gameEnded(final GameResult data, PlayerColor color, String errorMessage)
	{		
		try
		{
			actionPane.getDocument().insertString(
					actionPane.getDocument().getLength(), "\n\nSpiel beendet", new SimpleAttributeSet());

		}
		catch (BadLocationException e)
		{
			// should not happen
			System.err.println(e.getStackTrace());
		}
		
		moveButton.setEnabled(false);
		actionPane.setCaretPosition(actionPane.getDocument().getLength());
	}

	/**
	 * Repaint when something is clicked... useful if used more often
	 * @author sven
	 *
	 */
	private class ClickRefresher extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			FrameRenderer.this.repaint();
		}
	}

	/**
	 * An error (in most cases client failure) happened
	 */
	@Override
	public void gameError(String errorMessage)
	{
	}

	/**
	 * Obviously it was planned to implement internet games and chat functionality for
	 * this server. Maybe we will do that later, for now it's unused.
	 */
	@Override
	public void updateChat(String chatMsg) {
		// TODO Auto-generated method stub
		
	}
}
