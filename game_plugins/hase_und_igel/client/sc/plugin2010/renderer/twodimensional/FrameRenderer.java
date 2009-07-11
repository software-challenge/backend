/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.plugin2010.gui.GUIGameHandler;
import sc.plugin2010.renderer.Renderer;
import sc.plugin2010.util.GameUtil;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JFrame implements Renderer, IClickObserver
{

	// GUI Components
	private InformationBar			info;
	private ChatBar					chat;
	private ActionBar				actionb;
	private final List<FieldButton>	fbuttons		= new ArrayList<FieldButton>();
	private final GUIGameHandler	handler;

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated	= false;
	private boolean					myturn			= false;

	// Strings used for asking Questions to the user
	private String					moveForward		= "Weiter ziehen";
	private String					takeCarrots		= "10 Karotten nehmen";
	private String					dropCarrots		= "10 Karotten abgeben";
	private String					carrotAnswer	= "carrots";

	private String					take20carrots	= "Nimm 20 Karotten";
	private String					doNothing		= "Nichts";

	public FrameRenderer()
	{
		handler = null;
		createInitFrame();
	}

	public FrameRenderer(final GUIGameHandler handler)
	{
		this.handler = handler; // TODO when game is over, block input...
		createInitFrame();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void createInitFrame()
	{

		this.setSize(800, 600); // TODO get default size?

		final BackgoundPane bg = new BackgoundPane("resource/background.png");

		final int MAXROW = 8;

		for (int i = 0; i <= MAXROW * MAXROW; i++)
		{
			fbuttons.add(new FieldButton("", i, Board.FieldTyp.INVALID, this));
			fbuttons.get(i).setPreferredSize(new Dimension(40, 40));
			bg.add("1", fbuttons.get(i));
		}

		final HaseUndIgelLayout paneLayout = new HaseUndIgelLayout();

		bg.setLayout(paneLayout);

		info = new InformationBar();
		// chat = new ChatBar();
		actionb = new ActionBar();
		final ScrollPane action = new ScrollPane();
		action.add(actionb);

		final JPanel leftPanel = new JPanel();

		final BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		leftPanel.add(info, BorderLayout.NORTH);
		leftPanel.add(bg, BorderLayout.CENTER);
		// leftPanel.add(chat, BorderLayout.SOUTH);

		final BorderLayout framelayout = new BorderLayout();
		setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		action.setPreferredSize(new Dimension(180, 800));
		this.add(action, BorderLayout.EAST);

		actionb.addRow("Aktionen: ");
		// chat.addOtherMessage("Chat: ");
		// chat.addOwnMessage("Prototyp: 0.1 alpha :)");

		setVisible(true);
	}

	@Override
	public void updatePlayer(final Player player, final boolean own)
	{

		String currentColor = "";
		switch (player.getColor())
		{
			case BLUE:
				currentColor = "blue";
				break;
			case RED:
				currentColor = "red";
				break;
			default:
				break;
		}

		info.setTurn(currentColor);

		switch (player.getLastMove().getTyp())
		{
			case EAT:
				actionb
						.addRow("Spieler " + currentColor
								+ "frisst einen Salat");
				break;
			case MOVE:
				actionb.addRow("Spieler " + currentColor + "setzt auf "
						+ String.valueOf(player.getLastMove().getN()));
				break;
			case TAKE_10_CARROTS:
				actionb.addRow("Spieler " + currentColor + "nimmt 10 Karotten");
				break;
			case DROP_10_CARROTS:
				actionb.addRow("Spieler " + currentColor
						+ "gibt 10 Karotten ab");
				break;
			default:
				break;
		}

		for (int i = 0; i < fbuttons.size(); i++)
		{
			if (fbuttons.get(i).needRepaint(player.getColor()))
			{
				fbuttons.get(i).setFree();
				fbuttons.get(i).setReachable(false);
				fbuttons.get(i).repaint();
			}
		}

		fbuttons.get(player.getPosition()).setOccupied(player.getColor());

		if (own)
		{
			myturn = true;
			this.player = player;

			setReachableFields(player.getPosition(), player
					.getCarrotsAvailable());

			if (GameUtil.isValidToTakeCarrots(board, player))
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				answers.add(takeCarrots);
				if (GameUtil.isValidToDropCarrots(board, player))
				{
					answers.add(dropCarrots);
				}
				askQuestion("Was wollen Sie tun?", answers, "carrots");
			}
			else if (GameUtil.isValidToEat(board, player))
			{
				handler.sendAction(new Move(Move.MoveTyp.EAT));
			}
			else if ((board.getTypeAt(player.getPosition()) == Board.FieldTyp.RABBIT)
					&& (player.getActions().size() > 0))
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_CHANGE_CARROTS, 1))
				{
					answers.add(take20carrots);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_CHANGE_CARROTS, 0))
				{
					answers.add(doNothing);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_CHANGE_CARROTS, -1))
				{
					answers.add("Gib 20 Karotten ab");
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_EAT_SALAD, 0))
				{
					answers.add("Friss sofort einen Salat");
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_HURRY_AHEAD, 0))
				{
					answers.add("Rücke eine Position vor");
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Move.MoveTyp.PLAY_CARD_FALL_BACK, 0))
				{
					answers.add("Rücke eine Position vor");
				}

				askQuestion("Was wollen Sie tun?", answers, "joker");
			}

			info.setCarrots(player.getCarrotsAvailable());
			info.setHasenjoker(player.getActions());
		}
		else
		{
			myturn = false;
			enemy = player;
			info.setEnemyCarrots(enemy.getCarrotsAvailable());
			info.setEnemyHasenjoker(enemy.getActions());
		}
	}

	@Override
	public void updateBoard(BoardUpdated bu)
	{
		board = bu.getBoard();

		info.setRound(bu.getRound());

		if (!boardWasCreated)
		{
			String back = "";
			for (int i = 0; i <= fbuttons.size(); i++)
			{
				switch (board.getTypeAt(i))
				{
					case CARROT:
						back = "resource/carrots.png";
						break;
					case HEDGEHOG:
						back = "resource/hedgehog.png";
						break;
					case RABBIT:
						back = "resource/rabbit.png";
						break;
					case SALAD:
						back = "resource/salad.png";
						break;
					case POSITION_1:
						back = "resource/position_1.png";
						break;
					case POSITION_2:
						back = "resource/position_2.png";
						break;
					case START:
						back = "resource/start.png";
						break;
					case GOAL:
						back = "resource/finish.png";
						break;
				}
				fbuttons.get(i).setBackground(back);
			}
			boardWasCreated = true;
		}
	}

	@Override
	public void updateAction(final String doneAction)
	{
		actionb.addRow(doneAction);
	}

	@Override
	public void updateChat(final String chatMsg)
	{
		chat.addOtherMessage(chatMsg);
	}

	public void askQuestion(final String question, final List<String> answers,
			String type)
	{
		new QuestionDialog(question, answers, this, type);
	}

	public void answerQuestion(final String answer, String type)
	{
		if (type.equals(carrotAnswer))
		{
			if (answer.equals(takeCarrots))
			{
				handler.sendAction(new Move(Move.MoveTyp.TAKE_10_CARROTS));
			}
			else if (answer.equals(dropCarrots))
			{
				handler.sendAction(new Move(Move.MoveTyp.DROP_10_CARROTS));
			}
		}
	}

	public static void main(final String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				new FrameRenderer(null).setVisible(true);
			}
		});
	}

	private void setReachableFields(final int pos, final int carrots)
	{
		// if not in finish
		if (pos != 64)
		{
			final int moveable = GameUtil.calculateMoveableFields(carrots);

			int max = pos + moveable;

			if (max > 65)
			{
				max = 65;
			}

			for (int i = pos; i < max; i++)
			{
				if (fbuttons.get(i).getType() != Board.FieldTyp.HEDGEHOG)
				{
					fbuttons.get(i).setReachable(true);
					fbuttons.get(i).repaint();
				}
			}

			// if not on hedgehog
			if (fbuttons.get(pos).getType() != Board.FieldTyp.HEDGEHOG)
			{
				// seek for last hedgehog
				for (int i = pos - 1; i >= 0; i--)
				{
					if (fbuttons.get(i).getType() == Board.FieldTyp.HEDGEHOG)
					{
						fbuttons.get(i).setReachable(true);
						fbuttons.get(i).repaint();
						break;
					}
				}
			}
		}
	}

	/**
	 * user clicked on field <code>fieldNumber</code>
	 * 
	 * @param fieldNumber
	 *            the fieldnumber the user clicked onto
	 */
	public void updateClicked(final int fieldNumber)
	{
		if ((handler != null) && (myturn))
		{
			if (GameUtil.isValidToMove(board, player, fieldNumber))
			{
				handler.sendAction(new Move(Move.MoveTyp.MOVE, fieldNumber));
			}
			else
			{
				new ErrorDialog("Dies ist kein valider Zug.");
			}
		}
	}

	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		paint(img.getGraphics());
		return img;
	}
}
