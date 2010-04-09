/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sc.plugin2010.Action;
import sc.plugin2010.Board;
import sc.plugin2010.FieldTyp;
import sc.plugin2010.FigureColor;
import sc.plugin2010.GameUtil;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.MoveTyp;
import sc.plugin2010.Player;
import sc.plugin2010.renderer.IRenderer;
import sc.plugin2010.renderer.RendererUtil;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JPanel implements IRenderer, IClickObserver
{
	// GUI Components
	private UpperInformationBar		upperInformationBar;

	@SuppressWarnings("unused")
	private ChatBar					chat;

	private BackgroundPane			centerBoard;
	private BorderInformationBar	leftPlayerBar;
	private BorderInformationBar	rightPlayerBar;
	private ActionBar				action;
	private final List<FieldButton>	fbuttons				= new ArrayList<FieldButton>();
	private final IGameHandler		handler;
	private QuestionPanel			qPanel;

	private final int				UPPERHEIGHT				= 80;
	private final int				LOWERHEIGHT				= 70;
	private final int				CENTERBORDERWIDTH		= 180;
	private final Color				borderColor				= new Color(255,
																	255, 255,
																	120);

	private final Image				backGroundImage			= RendererUtil
																	.getImage("resource/game/background.png");

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated			= false;
	private boolean					myturn					= false;
	private boolean					onlyObserving			= false;
	private boolean					questionOpen			= false;
	private boolean					showing					= false;
	private boolean					needBackgroundUpdate	= false;

	// Strings used for asking Questions to the user
	private final String			moveForward				= "Weiter ziehen";
	private final String			takeCarrots				= "10 Karotten nehmen";
	private final String			dropCarrots				= "10 Karotten abgeben";
	private final String			carrotAnswer			= "carrots";

	private final String			take20carrots			= "Nimm 20 Karotten";
	private final String			doNothing				= "Keine Karotten abgeben oder nehmen";
	private final String			give20carrots			= "Gib 20 Karotten ab";
	private final String			eatsalad				= "Friss sofort einen Salat";
	private final String			hurryahead				= "R\u00FCcke eine Position vor";
	private final String			fallback				= "Falle eine Position zur\u00FCck";
	private final String			jokerAnswer				= "joker";

	public FrameRenderer(final IGameHandler handler, final boolean onlyObserving)
	{
		this.handler = handler;
		this.onlyObserving = onlyObserving;
		createInitFrame();
	}

	public void shown()
	{
		showing = true;
		if (needBackgroundUpdate)
		{
			divideBackground(new Dimension(getWidth() - 200, getHeight()),
					borderColor);

			repaint();
			needBackgroundUpdate = false;
		}
	}

	public void hidden()
	{
		showing = false;
	}

	private void createInitFrame()
	{
		setDoubleBuffered(true);

		addMouseListener(new ClickRefresher());

		centerBoard = new BackgroundPane();
		int scale = Math.min(getWidth(), getHeight());
		centerBoard.setPreferredSize(new Dimension(scale, scale));

		for (int i = 0; i < 65; i++)
		{
			fbuttons.add(new FieldButton("", i, FieldTyp.INVALID, this));
			fbuttons.get(i).setPreferredSize(new Dimension(40, 40));
			centerBoard.add("1", fbuttons.get(i));
		}

		final HaseUndIgelLayout paneLayout = new HaseUndIgelLayout();

		centerBoard.setLayout(paneLayout);

		upperInformationBar = new UpperInformationBar();
		leftPlayerBar = new BorderInformationBar(true);
		rightPlayerBar = new BorderInformationBar(false);
		// chat = new ChatBar();
		action = new ActionBar();

		JPanel leftPanel = new JPanel();

		final BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		JPanel centerPanel = new JPanel();

		final BorderLayout clayout = new BorderLayout();
		centerPanel.setLayout(clayout);

		leftPlayerBar.setPreferredSize(new Dimension(CENTERBORDERWIDTH, 100));
		rightPlayerBar.setPreferredSize(new Dimension(CENTERBORDERWIDTH, 100));

		centerPanel.add(leftPlayerBar, BorderLayout.WEST);
		centerPanel.add(centerBoard, BorderLayout.CENTER);
		centerPanel.add(rightPlayerBar, BorderLayout.EAST);

		upperInformationBar.setPreferredSize(new Dimension(getWidth(),
				UPPERHEIGHT));

		leftPanel.add(upperInformationBar, BorderLayout.NORTH);
		leftPanel.add(centerPanel, BorderLayout.CENTER);
		// leftPanel.add(chat, BorderLayout.SOUTH);

		qPanel = new QuestionPanel(this);
		leftPanel.add(qPanel, BorderLayout.AFTER_LAST_LINE);
		qPanel.setPreferredSize(new Dimension(getWidth(), LOWERHEIGHT));

		final BorderLayout framelayout = new BorderLayout();
		setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		action.setPreferredSize(new Dimension(200, getHeight()));
		this.add(action, BorderLayout.EAST);

		// chat.addOtherMessage("Chat: ");

		setVisible(true);

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				JPanel c = (JPanel) e.getSource();
				needBackgroundUpdate = true;
				if (c.getSize().width - 200 > 0 && c.getSize().height > 0
						&& showing)
				{
					divideBackground(new Dimension(c.getSize().width - 200, c
							.getSize().height), borderColor);

					repaint();
					needBackgroundUpdate = false;
				}
			}
		});
	}

	private void divideBackground(Dimension size, Color transparentColor)
	{
		int CENTERHEIGHT;

		if (size.height - UPPERHEIGHT - LOWERHEIGHT > 0)
		{
			CENTERHEIGHT = size.height - UPPERHEIGHT - LOWERHEIGHT;
		}
		else
		{
			CENTERHEIGHT = 0;
		}

		setVisible(true);
		BufferedImage wholeBackground = new BufferedImage(size.width,
				size.height, BufferedImage.TYPE_INT_ARGB);

		wholeBackground.getGraphics().drawImage(backGroundImage, 0, 0,
				size.width, size.height, 0, 0, backGroundImage.getWidth(this),
				backGroundImage.getHeight(this), this);

		Graphics2D g2d = wholeBackground.createGraphics();

		g2d.setColor(transparentColor);
		g2d.fillRect(0, 0, size.width, UPPERHEIGHT);
		g2d.fillRect(0, size.height - LOWERHEIGHT, size.width, size.height);
		g2d.fillRect(0, UPPERHEIGHT, CENTERBORDERWIDTH, CENTERHEIGHT);
		g2d.fillRect(size.width - CENTERBORDERWIDTH, UPPERHEIGHT, size.width,
				CENTERHEIGHT);

		Image upper = new BufferedImage(size.width, UPPERHEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		Image leftcenter = new BufferedImage(CENTERBORDERWIDTH, CENTERHEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		if (size.width - CENTERBORDERWIDTH * 2 <= 0)
		{
			return;
		}

		Image center = new BufferedImage(size.width - CENTERBORDERWIDTH * 2,
				CENTERHEIGHT, BufferedImage.TYPE_INT_ARGB);

		Image rightcenter = new BufferedImage(CENTERBORDERWIDTH, CENTERHEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		Image lower = new BufferedImage(size.width, LOWERHEIGHT,
				BufferedImage.TYPE_INT_ARGB);

		upper.getGraphics().drawImage(wholeBackground, 0, 0, size.width,
				UPPERHEIGHT, 0, 0, size.width, UPPERHEIGHT, this);

		leftcenter.getGraphics().drawImage(wholeBackground, 0, 0,
				CENTERBORDERWIDTH, CENTERHEIGHT, 0, UPPERHEIGHT,
				CENTERBORDERWIDTH, UPPERHEIGHT + CENTERHEIGHT, this);

		center.getGraphics().drawImage(wholeBackground, 0, 0,
				size.width - CENTERBORDERWIDTH * 2, CENTERHEIGHT,
				CENTERBORDERWIDTH, UPPERHEIGHT, size.width - CENTERBORDERWIDTH,
				UPPERHEIGHT + CENTERHEIGHT, this);

		rightcenter.getGraphics().drawImage(wholeBackground, 0, 0,
				CENTERBORDERWIDTH, CENTERHEIGHT,
				size.width - CENTERBORDERWIDTH, UPPERHEIGHT, size.width,
				UPPERHEIGHT + CENTERHEIGHT, this);

		lower.getGraphics().drawImage(wholeBackground, 0, 0, size.width,
				LOWERHEIGHT, 0, size.height - LOWERHEIGHT, size.width,
				size.height, this);

		upperInformationBar.setBackground(upper);
		leftPlayerBar.setBackground(leftcenter);
		centerBoard.setBackground(center);
		rightPlayerBar.setBackground(rightcenter);
		qPanel.setBackground(lower);
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
				upperInformationBar.setPlayer(player.getDisplayName());
				leftPlayerBar.setAttributes(player.getCarrotsAvailable(),
						player.getSaladsToEat(), player.getActions());

				upperInformationBar.setOtherPlayer(enemy.getDisplayName());
				rightPlayerBar.setAttributes(enemy.getCarrotsAvailable(), enemy
						.getSaladsToEat(), enemy.getActions());
				break;
			case BLUE:
				addHistory(enemy, player);
				currentColorPath = "blue";
				upperInformationBar.setPlayer(enemy.getDisplayName());
				leftPlayerBar.setAttributes(enemy.getCarrotsAvailable(), enemy
						.getSaladsToEat(), enemy.getActions());

				upperInformationBar.setOtherPlayer(player.getDisplayName());
				rightPlayerBar.setAttributes(player.getCarrotsAvailable(),
						player.getSaladsToEat(), player.getActions());
				break;
			default:
				break;
		}

		upperInformationBar.setTurn(currentColorPath);

		repaint();
	}

	@Override
	public void updateBoard(Board board, int round)
	{
		this.board = board;

		upperInformationBar.setRound(round + 1);
		if (round == 1)
		{
			qPanel.hideComponents();
		}

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

			boolean moveable = false;

			if (GameUtil.isValidToFallBack(board, player))
			{
				moveable = true;
			}
			else
			{
				for (int i = player.getFieldNumber(); i < 65; i++)
				{
					if (GameUtil.isValidToMove(board, player, i
							- player.getFieldNumber()))
					{
						moveable = true;
						break;
					}
				}
			}

			if (moveable)
			{
				answers.add(moveForward);
			}

			askQuestion("<html>Was wollen Sie tun, " + color + " ?</html>",
					answers, carrotAnswer);

		}
		else if (GameUtil.isValidToEat(board, player))
		{
			//sendMove(new Move(MoveTyp.EAT));
			List<String> answers = new LinkedList<String>();
			answers.add(eatsalad);
			answers.add(moveForward);
			askQuestion("<html>Wollen sie einen Salat fressen, "+color+" ?</html", answers, eatsalad);
		}
		else if ((board.getTypeAt(player.getFieldNumber()) == FieldTyp.RABBIT)
				&& (player.getActions().size() > 0))
		{
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
		
                if (type.equals(eatsalad)){
		  if (answer.equals(eatsalad)){
                    sendMove(new Move(MoveTyp.EAT, Action.EAT_SALAD));
		  }else if(answer.equals(moveForward)){
		    qPanel.hideComponents();
                    setReachableFields(player.getFieldNumber());
		    this.repaint();
	          }
	        }
	
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
		if (!myturn)
		{
			return;
		}

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
	public void gameEnded(final GameResult data)
	{
		final Runnable awtAction = new Runnable() {
			@Override
			public void run()
			{
				action.addNormal("----------------");

				if (data == null)
				{
					action.addNormal("Leeres Spielresultat!");
					action.setScrollBarToEnd();
					return;
				}

				action.addNormal("Spielresultat:");

				if (data.getScores().get(0).getCause() == ScoreCause.LEFT)
				{
					addGameEndedRightColors(FigureColor.RED,
							" hat das Spiel verlassen!");
				}

				if (data.getScores().get(1).getCause() == ScoreCause.LEFT)
				{
					addGameEndedRightColors(FigureColor.BLUE,
							" hat das Spiel verlassen!");
				}

				if (data.getScores().get(0).getCause() == ScoreCause.RULE_VIOLATION)
				{
					addGameEndedRightColors(FigureColor.RED,
							" hat einen falschen Zug gesetzt!");
				}

				if (data.getScores().get(1).getCause() == ScoreCause.RULE_VIOLATION)
				{
					addGameEndedRightColors(FigureColor.BLUE,
							" hat einen falschen Zug gesetzt!");
				}

				String[] results = data.getScores().get(0).toStrings();
				if (results[0].equals("1"))
				{
					addGameEndedRightColors(FigureColor.RED, ": Gewinner");
				}
				else if (results[0].equals("0"))
				{
					addGameEndedRightColors(FigureColor.RED, ": Verlierer");
				}
				else
				{
					action.addNormal("Unentschieden");
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
		};

		SwingUtilities.invokeLater(awtAction);
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
