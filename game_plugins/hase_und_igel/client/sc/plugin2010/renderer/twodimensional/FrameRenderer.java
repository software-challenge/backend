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
public class FrameRenderer extends JPanel implements Renderer, IClickObserver
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
	private boolean					onlyObserving	= false;

	// Strings used for asking Questions to the user
	private String					moveForward		= "Weiter ziehen";
	private String					takeCarrots		= "10 Karotten nehmen";
	private String					dropCarrots		= "10 Karotten abgeben";
	private String					carrotAnswer	= "carrots";

	private String					take20carrots	= "Nimm 20 Karotten";
	private String					doNothing		= "Nichts";
	private String					give20carrots	= "Gib 20 Karotten ab";
	private String					eatsalad		= "Friss sofort einen Salat";
	private String					hurryahead		= "Rücke eine Position vor";
	private String					fallback		= "Rücke eine Position vor";
	private String					jokerAnswer		= "joker";

	public FrameRenderer()
	{
		handler = null;
		createInitFrame();
	}

	public FrameRenderer(final GUIGameHandler handler,
			final boolean onlyObserving)
	{
		this.handler = handler; // TODO when game is over, block input...
		this.onlyObserving = onlyObserving;
		createInitFrame();
	}

	private void createInitFrame()
	{

		this.setSize(800, 600);

		final BackgoundPane bg = new BackgoundPane("resource/background.png");

		for (int i = 0; i < 65; i++)
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
				actionb.addRow("Spieler " + currentColor
						+ " frisst einen Salat");
				break;
			case MOVE:
				actionb.addRow("Spieler " + currentColor + " setzt auf "
						+ String.valueOf(player.getLastMove().getN()));
				break;
			case TAKE_OR_DROP_CARROTS:
				if (player.getLastMove().getN() == 10)
				{
					actionb.addRow("Spieler " + currentColor
							+ " nimmt 10 Karotten");
				}
				else if (player.getLastMove().getN() == -10)
				{
					actionb.addRow("Spieler " + currentColor
							+ " gibt 10 Karotten ab");
				}
				break;
			case FALL_BACK:
				actionb.addRow("Spieler " + currentColor
						+ " lässt sich auf Igel zurückfallen");
				break;
			case PLAY_CARD:
				switch (player.getLastMove().getCard())
				{
					case TAKE_OR_DROP_CARROTS:
						actionb.addRow("Spieler " + currentColor
								+ " spielt 'Nimm oder gib 20 Karotten'");
						break;
					case EAT_SALAD:
						actionb.addRow("Spieler " + currentColor
								+ " spielt 'Friss sofort einen Salat'");
						break;
					case FALL_BACK:
						actionb.addRow("Spieler " + currentColor
								+ " spielt 'Falle eine Position zurück'");
						break;
					case HURRY_AHEAD:
						actionb.addRow("Spieler " + currentColor
								+ " spielt 'Rücke eine Position vor'");
						break;
					default:
						break;
				}
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

			if (GameUtil.isValidToTakeOrDrop10Carrots(board, player, 10))
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				answers.add(takeCarrots);
				if (GameUtil.isValidToTakeOrDrop10Carrots(board, player, -10))
				{
					answers.add(dropCarrots);
				}
				askQuestion("Was wollen Sie tun?", answers, carrotAnswer);
			}
			else if (GameUtil.isValidToEat(board, player))
			{
				sendMove(new Move(Move.MoveTyp.EAT));
			}
			else if ((board.getTypeAt(player.getPosition()) == Board.FieldTyp.RABBIT)
					&& (player.getActions().size() > 0))
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.TAKE_OR_DROP_CARROTS, 1))
				{
					answers.add(take20carrots);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.TAKE_OR_DROP_CARROTS, 0))
				{
					answers.add(doNothing);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.TAKE_OR_DROP_CARROTS, -1))
				{
					answers.add(give20carrots);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.EAT_SALAD, 0))
				{
					answers.add(eatsalad);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.HURRY_AHEAD, 0))
				{
					answers.add(hurryahead);
				}
				if (GameUtil.isValidToPlayCard(board, player,
						Player.Action.FALL_BACK, 0))
				{
					answers.add(fallback);
				}

				askQuestion("Welchen Hasenjoker wollen Sie spielen?", answers,
						jokerAnswer);
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
				sendMove(new Move(Move.MoveTyp.TAKE_OR_DROP_CARROTS, 10));
			}
			else if (answer.equals(dropCarrots))
			{
				sendMove(new Move(Move.MoveTyp.TAKE_OR_DROP_CARROTS, -10));
			}
		}
		if (type.equals(jokerAnswer))
		{ // TODO
			if (answer.equals(takeCarrots))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.TAKE_OR_DROP_CARROTS, 1));
			}
			else if (answer.equals(dropCarrots))
			{
				// sendMove(new Move(Move.MoveTyp.PLAY_CARD_CHANGE_CARROTS, 0));
			} // TODO more cases
		}
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
		if ((!onlyObserving) && (myturn))
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

	private void sendMove(Move move)
	{
		if (myturn)
		{
			handler.sendAction(move);
			myturn = false;
		}
	}
}
