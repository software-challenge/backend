/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
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
	private final JPanel			panel;

	// local instances of current players and board
	private Player					player;
	private Player					enemy;
	private Board					board;

	// only draw the board the first time it updates
	private boolean					boardWasCreated	= false;

	// Strings used for asking Questions to the user
	private String					moveForward		= "Weiter ziehen";
	private String					takeCarrots		= "10 Karotten nehmen";
	private String					dropCarrots		= "10 Karotten abgeben";
	private String					carrotAnswer	= "carrots";

	public FrameRenderer(final JPanel panel)
	{
		this.panel = panel;
		createInitFrame();
	}

	private void createInitFrame()
	{

		setIconImage(new ImageIcon("resource/hase_und_igel_icon.png")
				.getImage());

		this.setSize(800, 600);

		setTitle("Hase und Igel");

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

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e)
			{
				System.exit(0);
			}
		});
	}

	@Override
	public void updatePlayer(final Player player, final boolean own)
	{
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
			this.player = player;

			setReachableFields(player.getPosition(), player
					.getCarrotsAvailable());

			if (board.getTypeAt(player.getPosition()) == Board.FieldTyp.CARROT)
			{
				List<String> answers = new LinkedList<String>();
				answers.add(moveForward);
				answers.add(takeCarrots);
				answers.add(dropCarrots);
				askQuestion("Was wollen Sie tun?", answers, "carrots");
			}
			else if (board.isValid(new Move(Move.MoveTyp.EAT), player))
			{
				// TODO send move
			}

			info.setCarrots(player.getCarrotsAvailable());
		}
		else
		{
			enemy = player;
		}
	}

	@Override
	public void updateBoard(BoardUpdated bu)
	{
		board = bu.getBoard();

		info.setTurn(bu.getRound());

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
				// new Move(type); TODO
			}
			else if (answer.equals(dropCarrots))
			{
				// new Move(type); TODO
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
		Move move = new Move(Move.MoveTyp.MOVE, fieldNumber);

		if (board.isValid(move, player))
		{
			// send answer TODO
		}
		else
		{
			new ErrorDialog("Dies ist kein valider Zug.");
		}
	}

	@Override
	public Image getImage()
	{
		BufferedImage img = new BufferedImage(panel.getWidth(), panel
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		panel.paint(img.getGraphics());
		return img;
	}
}
