/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.GameUtil;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.plugin2010.Board.FieldTyp;
import sc.plugin2010.Player.FigureColor;
import sc.plugin2010.gui.GUIGameHandler;
import sc.plugin2010.renderer.IRenderer;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer, IClickObserver
{
	// GUI Components
	private InformationBar			info;
	private ChatBar					chat;
	private ActionBar				action;
	private final List<FieldButton>	fbuttons		= new ArrayList<FieldButton>();
	private final GUIGameHandler	handler;
	private final JPanel			leftPanel		= new JPanel();
	private QuestionPanel			qPanel;

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated	= false;
	private boolean					myturn			= false;
	private boolean					onlyObserving	= false;
	private boolean					questionOpen	= false;

	private String					playername		= "";
	private String					otherPlayername	= "";

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

	public FrameRenderer(final GUIGameHandler handler,
			final boolean onlyObserving, String playername,
			String otherPlayername)
	{
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		this.playername = playername; // TODO use playername
		this.otherPlayername = otherPlayername;
		createInitFrame();
	}

	private void createInitFrame()
	{
		setDoubleBuffered(true);

		final BackgoundPane bg = new BackgoundPane("resource/background.png");
		int scale = Math.min(bg.getWidth(), bg.getHeight());
		bg.setPreferredSize(new Dimension(scale, scale));

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
		action = new ActionBar();

		final BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		leftPanel.add(info, BorderLayout.NORTH);
		leftPanel.add(bg, BorderLayout.CENTER);
		// leftPanel.add(chat, BorderLayout.SOUTH);

		final BorderLayout framelayout = new BorderLayout();
		setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		action.setPreferredSize(new Dimension(200, getHeight() - 100));
		this.add(action, BorderLayout.EAST);

		// chat.addOtherMessage("Chat: ");
		// chat.addOwnMessage("Prototyp: 0.1 alpha :)");

		setVisible(true);
	}

	private int printHistroyTillNewTurn(final Player player, int i,
			final String color)
	{
		if (i < player.getHistory().size())
		{
			action.addAction(color, " "
					+ GameUtil.displayMoveAction(player.getHistory().get(i)));
			i++;
			if (i < player.getHistory().size()
					&& player.getHistory().get(i).getTyp() == Move.MoveTyp.PLAY_CARD)
			{
				action.addAction(color, " "
						+ GameUtil
								.displayMoveAction(player.getHistory().get(i)));
				i++;
				if (i < player.getHistory().size()
						&& player.getHistory().get(i).getTyp() == Move.MoveTyp.PLAY_CARD)
				{
					action.addAction(color, " "
							+ GameUtil.displayMoveAction(player.getHistory()
									.get(i)));
					i++;
				}
			}
		}
		else
		{
			i++;
		}

		return i;
	}

	private void addHistory(final Player redPlayer, final Player bluePlayer)
	{
		String red = "Rot";
		String blue = "Blau";

		action.removeAllActions();
		action.addNormal("Aktionen: ");

		int max = Math.max(redPlayer.getHistory().size(), bluePlayer
				.getHistory().size());
		int i = 0;
		int j = 0;

		while (i <= max || j <= max)
		{
			i = printHistroyTillNewTurn(redPlayer, i, red);
			j = printHistroyTillNewTurn(bluePlayer, j, blue);
		}
	}

	@Override
	public void updatePlayer(final Player player, final Player otherPlayer)
	{
		this.player = player;
		enemy = otherPlayer;

		if (player.getColor() == FigureColor.RED)
		{
			info.setColor(true);
		}
		else
		{
			info.setColor(false);
		}

		String currentColorPath = "";
		switch (player.getColor())
		{
			case BLUE:
				addHistory(enemy, player);
				currentColorPath = "blue";
				break;
			case RED:
				addHistory(player, enemy);
				currentColorPath = "red";
				break;
			default:
				break;
		}

		info.setTurn(currentColorPath);

		for (int i = 0; i < fbuttons.size(); i++)
		{
			if (fbuttons.get(i).needRepaint())
			{
				fbuttons.get(i).setFree();
				fbuttons.get(i).setReachable(false);
				fbuttons.get(i).repaint();
			}
		}

		if (enemy != null)
		{
			fbuttons.get(enemy.getFieldNumber()).setOccupied(enemy.getColor());
		}

		fbuttons.get(player.getFieldNumber()).setOccupied(player.getColor());

		info.setAttributes(player.getCarrotsAvailable(), player
				.getSaladsToEat());
		info.setHasenjoker(player.getActions());

		info.setEnemyAttributes(enemy.getCarrotsAvailable(), enemy
				.getSaladsToEat());
		info.setEnemyHasenjoker(enemy.getActions());
	}

	@Override
	public void updateBoard(Board board, int round)
	{
		this.board = board;

		info.setRound(round + 1);

		if (!boardWasCreated)
		{
			String back = "";
			for (int i = 0; i < fbuttons.size(); i++)
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
				fbuttons.get(i).setType(board.getTypeAt(i));
			}
			boardWasCreated = true;
		}
	}

	public void askQuestion(final String question, final List<String> answers,
			String type)
	{
		questionOpen = true;
		qPanel = new QuestionPanel(question, answers, this, type);
		leftPanel.add(qPanel, BorderLayout.AFTER_LAST_LINE);
	}

	private void askForAction(final Player player)
	{
		String color = "";
		switch (player.getColor())
		{
			case RED:
				color = "<font color='#ff0000'>Rot</font>";
				break;
			case BLUE:
				color = "<font color='#0000ff'>Blau</font>";
				break;
			default:
				break;
		}

		if (GameUtil.isValidToTakeOrDrop10Carrots(board, player, 10))
		{
			setReachableFields(player.getFieldNumber());

			List<String> answers = new LinkedList<String>();
			answers.add(takeCarrots);
			if (GameUtil.isValidToTakeOrDrop10Carrots(board, player, -10))
			{
				answers.add(dropCarrots);
			}
			answers.add(moveForward);
			askQuestion("<html>Was wollen Sie tun, " + color + " ?</html>",
					answers, carrotAnswer);

		}
		else if (GameUtil.isValidToEat(board, player))
		{
			sendMove(new Move(Move.MoveTyp.EAT));
		}
		else if ((board.getTypeAt(player.getFieldNumber()) == Board.FieldTyp.RABBIT)
				&& (player.getActions().size() > 0))
		{

			List<String> answers = new LinkedList<String>();
			if (GameUtil.isValidToPlayCard(board, player,
					Player.Action.TAKE_OR_DROP_CARROTS, 20))
			{
				answers.add(take20carrots);
			}
			if (GameUtil.isValidToPlayCard(board, player,
					Player.Action.TAKE_OR_DROP_CARROTS, 0))
			{
				answers.add(doNothing);
			}
			if (GameUtil.isValidToPlayCard(board, player,
					Player.Action.TAKE_OR_DROP_CARROTS, -20))
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
			if (answers.size() > 0)
			{
				myturn = false;
				askQuestion("<html>Welchen Hasenjoker möchten Sie spielen, "
						+ color + " ?</html>", answers, jokerAnswer);
			}
		}
	}

	public void answerQuestion(final String answer, String type)
	{
		myturn = true;

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
			else if (answer.equals(moveForward))
			{
				// TODO close dialog
			}
		}
		if (type.equals(jokerAnswer))
		{
			if (answer.equals(take20carrots))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.TAKE_OR_DROP_CARROTS, 20));
			}
			else if (answer.equals(doNothing))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.TAKE_OR_DROP_CARROTS, 0));
			}
			else if (answer.equals(give20carrots))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.TAKE_OR_DROP_CARROTS, -20));
			}
			else if (answer.equals(eatsalad))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.EAT_SALAD));
			}
			else if (answer.equals(hurryahead))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.HURRY_AHEAD));
			}
			else if (answer.equals(fallback))
			{
				sendMove(new Move(Move.MoveTyp.PLAY_CARD,
						Player.Action.FALL_BACK));
			}
		}
	}

	@Override
	public void updateAction(final String doneAction)
	{
		// actionb.addRow(doneAction);
	}

	@Override
	public void updateChat(final String chatMsg)
	{
		chat.addOtherMessage(chatMsg);
	}

	private void setReachableFields(final int pos)
	{
		// if not in finish
		if (pos != 64)
		{
			for (int i = 0; i < fbuttons.size(); i++)
			{
				if (GameUtil.isValidToMove(board, player, i - pos))
				{
					fbuttons.get(i).setReachable(true);
					fbuttons.get(i).repaint();
				}
			}

			// if fall back is valid
			if (GameUtil.isValidToFallBack(board, player))
			{
				// seek for last hedgehog
				int index = board.getPreviousFieldByTyp(FieldTyp.HEDGEHOG, pos);
				if (index > 0 && index < fbuttons.size())
				{
					fbuttons.get(index).setReachable(true);
					fbuttons.get(index).repaint();
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
			int relativeFieldsToMove = fieldNumber - player.getFieldNumber();
			if (relativeFieldsToMove < 0)
			{
				if (GameUtil.isValidToFallBack(board, player)
						&& board.getTypeAt(fieldNumber) == FieldTyp.HEDGEHOG)
				{
					sendMove(new Move(Move.MoveTyp.FALL_BACK));
				}
				else
				{
					new ErrorDialog("Dies ist kein valider Zug.");
				}
			}
			else
			{
				if (GameUtil.isValidToMove(board, player, relativeFieldsToMove))
				{
					sendMove(new Move(Move.MoveTyp.MOVE, relativeFieldsToMove));
				}
				else
				{
					new ErrorDialog("Dies ist kein valider Zug.");
				}
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
			if (questionOpen)
			{
				leftPanel.remove(qPanel);
				questionOpen = false;
			}

			handler.sendAction(move);
			myturn = false;
		}
	}

	@Override
	public void requestMove()
	{
		myturn = true;

		askForAction(player);

		if (!questionOpen)
		{
			setReachableFields(player.getFieldNumber());
		}
	}

	@Override
	public void gameEnded(GameResult data)
	{
		String[] results = data.getScores().get(0).toStrings();
		if (results[0].equals("1"))
		{
			action.addAction("Rot", ": Gewinner");
		}
		else if (results[0].equals("0"))
		{
			action.addAction("Rot", ": Verlierer");
		}

		action.addAction("Rot", ": erreichtes Feld:" + results[1]);

		results = data.getScores().get(1).toStrings();
		if (results[0].equals("1"))
		{
			action.addAction("Blau", ": Gewinner");
		}
		else if (results[0].equals("0"))
		{
			action.addAction("Blau", ": Verlierer");
		}

		action.addAction("Blau", ": erreichtes Feld:" + results[1]);
	}
}
