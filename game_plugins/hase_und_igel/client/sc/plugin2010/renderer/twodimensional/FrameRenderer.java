/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import sc.plugin2010.Action;
import sc.plugin2010.Board;
import sc.plugin2010.FieldTyp;
import sc.plugin2010.FigureColor;
import sc.plugin2010.GameUtil;
import sc.plugin2010.Move;
import sc.plugin2010.MoveTyp;
import sc.plugin2010.Player;
import sc.plugin2010.gui.HumanGameHandler;
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
	private final HumanGameHandler	handler;
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

	// Strings used for asking Questions to the user
	private String					moveForward		= "Weiter ziehen";
	private String					takeCarrots		= "10 Karotten nehmen";
	private String					dropCarrots		= "10 Karotten abgeben";
	private String					carrotAnswer	= "carrots";

	private String					take20carrots	= "Nimm 20 Karotten";
	private String					doNothing		= "Keine Karotten abgeben oder nehmen";
	private String					give20carrots	= "Gib 20 Karotten ab";
	private String					eatsalad		= "Friss sofort einen Salat";
	private String					hurryahead		= "Rücke eine Position vor";
	private String					fallback		= "Falle eine Position zurück";
	private String					jokerAnswer		= "joker";

	public FrameRenderer(final HumanGameHandler handler,
			final boolean onlyObserving)
	{
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		createInitFrame();
	}

	private void createInitFrame()
	{
		setDoubleBuffered(true);

		addMouseListener(new ClickRefresher());

		final TransparentPanel bg = new TransparentPanel();
		int scale = Math.min(bg.getWidth(), bg.getHeight());
		bg.setPreferredSize(new Dimension(scale, scale));

		for (int i = 0; i < 65; i++)
		{
			fbuttons.add(new FieldButton("", i, FieldTyp.INVALID, this));
			fbuttons.get(i).setPreferredSize(new Dimension(40, 40));
			bg.add("1", fbuttons.get(i));
		}

		final HaseUndIgelLayout paneLayout = new HaseUndIgelLayout();

		bg.setLayout(paneLayout);

		info = new InformationBar();
		// chat = new ChatBar();
		action = new ActionBar();

		JPanel leftPanel = new BackgoundPane("resource/game/background.png");

		final BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		info.setPreferredSize(new Dimension(Frame.WIDTH, 220));

		leftPanel.add(info, BorderLayout.NORTH);
		leftPanel.add(bg, BorderLayout.CENTER);
		// leftPanel.add(chat, BorderLayout.SOUTH);

		qPanel = new QuestionPanel(this);
		leftPanel.add(qPanel, BorderLayout.AFTER_LAST_LINE);
		qPanel.setPreferredSize(new Dimension(Frame.WIDTH, 50));

		final BorderLayout framelayout = new BorderLayout();
		setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		action.setPreferredSize(new Dimension(200, getHeight() - 100));
		this.add(action, BorderLayout.EAST);

		// chat.addOtherMessage("Chat: ");
		// chat.addOwnMessage("Prototyp: 0.1 alpha :)");

		setVisible(true);
	}

	private int printHistroyTillNewTurn(final Player curPlayer, int i,
			final String color)
	{
		if (i < curPlayer.getHistory().size())
		{
			action
					.addAction(color, curPlayer.getDisplayName(), " "
							+ GameUtil.displayMoveAction(curPlayer.getHistory()
									.get(i)));
			i++;
			if (i < curPlayer.getHistory().size()
					&& curPlayer.getHistory().get(i).getTyp() == MoveTyp.PLAY_CARD)
			{
				action.addAction(color, curPlayer.getDisplayName(), " "
						+ GameUtil.displayMoveAction(curPlayer.getHistory()
								.get(i)));
				i++;
				if (i < curPlayer.getHistory().size()
						&& curPlayer.getHistory().get(i).getTyp() == MoveTyp.PLAY_CARD)
				{
					action.addAction(color, curPlayer.getDisplayName(), " "
							+ GameUtil.displayMoveAction(curPlayer.getHistory()
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

		action.setScrollBarToEnd();
	}

	@Override
	public void updatePlayer(final Player player, final Player otherPlayer)
	{
		this.repaint();

		this.player = player;
		enemy = otherPlayer;

		for (int i = 0; i < fbuttons.size(); i++)
		{
			if (player.getColor() == FigureColor.RED)
			{
				fbuttons.get(i).setTurnRed();
			}
			else
			{
				fbuttons.get(i).setTurnBlue();
			}

			if (fbuttons.get(i).needRepaint())
			{
				fbuttons.get(i).setFree();
				fbuttons.get(i).setReachable(false, false);
				fbuttons.get(i).repaint();
			}
		}

		fbuttons.get(enemy.getFieldNumber()).setOccupied(enemy.getColor());
		fbuttons.get(player.getFieldNumber()).setOccupied(player.getColor());

		String currentColorPath = "";
		switch (player.getColor())
		{
			case RED:
				addHistory(player, enemy);
				currentColorPath = "red";
				info.setPlayer(player.getDisplayName());
				info.setAttributes(player.getCarrotsAvailable(), player
						.getSaladsToEat(), player.getActions());

				info.setOtherPlayer(enemy.getDisplayName());
				info.setEnemyAttributes(enemy.getCarrotsAvailable(), enemy
						.getSaladsToEat(), enemy.getActions());
				break;
			case BLUE:
				addHistory(enemy, player);
				currentColorPath = "blue";
				info.setPlayer(enemy.getDisplayName());
				info.setAttributes(enemy.getCarrotsAvailable(), enemy
						.getSaladsToEat(), enemy.getActions());

				info.setOtherPlayer(player.getDisplayName());
				info.setEnemyAttributes(player.getCarrotsAvailable(), player
						.getSaladsToEat(), player.getActions());
				break;
			default:
				break;
		}

		info.setTurn(currentColorPath);

		repaint();
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
						back = "resource/game/carrots.png";
						break;
					case HEDGEHOG:
						back = "resource/game/hedgehog.png";
						break;
					case RABBIT:
						back = "resource/game/rabbit.png";
						break;
					case SALAD:
						back = "resource/game/salad.png";
						break;
					case POSITION_1:
						back = "resource/game/position_1.png";
						break;
					case POSITION_2:
						back = "resource/game/position_2.png";
						break;
					case START:
						back = "resource/game/start.png";
						break;
					case GOAL:
						back = "resource/game/finish.png";
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
		qPanel.showQuestion(question, answers, type);

		repaint();
	}

	private void askForAction(final Player player)
	{
		String color = "";
		switch (player.getColor())
		{
			case RED:
				color = "<font color='#ff0000'>" + player.getDisplayName()
						+ "</font>";
				break;
			case BLUE:
				color = "<font color='#0000ff'>" + player.getDisplayName()
						+ "</font>";
				break;
			default:
				break;
		}

		if (GameUtil.isValidToSkip(board, player))
		{
			sendMove(new Move(MoveTyp.SKIP));
		}
		else if (GameUtil.isValidToTakeOrDrop10Carrots(board, player, 10))
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
			sendMove(new Move(MoveTyp.EAT));
		}
		else if ((board.getTypeAt(player.getFieldNumber()) == FieldTyp.RABBIT)
				&& (player.getActions().size() > 0))
		{

			// TODO if the width of the window isnt enough for the buttons than
			// only a few buttons are shown
			List<String> answers = new LinkedList<String>();
			if (GameUtil.isValidToPlayCard(board, player,
					Action.TAKE_OR_DROP_CARROTS, 20))
			{
				answers.add(take20carrots);
			}
			if (GameUtil.isValidToPlayCard(board, player,
					Action.TAKE_OR_DROP_CARROTS, 0))
			{
				answers.add(doNothing);
			}
			if (GameUtil.isValidToPlayCard(board, player,
					Action.TAKE_OR_DROP_CARROTS, -20))
			{
				answers.add(give20carrots);
			}
			if (GameUtil.isValidToPlayCard(board, player, Action.EAT_SALAD, 0))
			{
				answers.add(eatsalad);
			}
			if (GameUtil
					.isValidToPlayCard(board, player, Action.HURRY_AHEAD, 0))
			{
				answers.add(hurryahead);
			}
			if (GameUtil.isValidToPlayCard(board, player, Action.FALL_BACK, 0))
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
				sendMove(new Move(MoveTyp.TAKE_OR_DROP_CARROTS, 10));
			}
			else if (answer.equals(dropCarrots))
			{
				sendMove(new Move(MoveTyp.TAKE_OR_DROP_CARROTS, -10));
			}
			else if (answer.equals(moveForward))
			{
				qPanel.hideComponents();
				this.repaint();
			}
		}
		if (type.equals(jokerAnswer))
		{
			if (answer.equals(take20carrots))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD,
						Action.TAKE_OR_DROP_CARROTS, 20));
			}
			else if (answer.equals(doNothing))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD,
						Action.TAKE_OR_DROP_CARROTS, 0));
			}
			else if (answer.equals(give20carrots))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD,
						Action.TAKE_OR_DROP_CARROTS, -20));
			}
			else if (answer.equals(eatsalad))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD, Action.EAT_SALAD));
			}
			else if (answer.equals(hurryahead))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD, Action.HURRY_AHEAD));
			}
			else if (answer.equals(fallback))
			{
				sendMove(new Move(MoveTyp.PLAY_CARD, Action.FALL_BACK));
			}
		}
	}

	@Override
	public void updateChat(final String chatMsg)
	{
		// chat.addOtherMessage(chatMsg);
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
					if (player.getColor() == FigureColor.RED)
					{
						fbuttons.get(i).setReachable(true, true);
					}
					else
					{
						fbuttons.get(i).setReachable(true, false);
					}

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
					if (player.getColor() == FigureColor.RED)
					{
						fbuttons.get(index).setReachable(true, true);
					}
					else
					{
						fbuttons.get(index).setReachable(true, false);
					}

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
					sendMove(new Move(MoveTyp.FALL_BACK));
				}
			}
			else
			{
				if (GameUtil.isValidToMove(board, player, relativeFieldsToMove))
				{
					sendMove(new Move(MoveTyp.MOVE, relativeFieldsToMove));
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
				qPanel.hideComponents();
				questionOpen = false;

				repaint();
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

	public void addGameEndedRightColors(FigureColor col, String text)
	{
		String colorStr = "";

		switch (col)
		{
			case RED:
				colorStr = "Rot";
				break;
			case BLUE:
				colorStr = "Blau";
				break;

			default:
				break;
		}

		if (player.getColor() == col)
		{
			action.addAction(colorStr, player.getDisplayName(), text);
		}
		else
		{
			action.addAction(colorStr, enemy.getDisplayName(), text);
		}
	}

	@Override
	public void gameEnded(GameResult data)
	{
		action.addNormal("----------------");
		action.addNormal("Spielresultat:");

		String[] results = data.getScores().get(0).toStrings();
		if (results[0].equals("1"))
		{
			addGameEndedRightColors(FigureColor.RED, ": Gewinner");
		}
		else if (results[0].equals("0"))
		{
			addGameEndedRightColors(FigureColor.RED, ": Verlierer");
		}

		addGameEndedRightColors(FigureColor.RED, ": erreichtes Feld: "
				+ results[1]);

		results = data.getScores().get(1).toStrings();
		if (results[0].equals("1"))
		{
			addGameEndedRightColors(FigureColor.BLUE, ": Gewinner");
		}
		else if (results[0].equals("0"))
		{
			addGameEndedRightColors(FigureColor.BLUE, ": Verlierer");
		}

		addGameEndedRightColors(FigureColor.BLUE, ": erreichtes Feld: "
				+ results[1]);

		action.setScrollBarToEnd();
	}

	private class ClickRefresher extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			FrameRenderer.this.repaint();
		}
	}
}
