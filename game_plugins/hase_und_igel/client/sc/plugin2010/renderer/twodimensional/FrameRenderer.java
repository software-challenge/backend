/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sc.plugin2010.Board;
import sc.plugin2010.Player;
import sc.plugin2010.gui.EViewerMode;
import sc.plugin2010.renderer.Renderer;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class FrameRenderer extends JFrame implements Renderer, IClickObserver
{
	private InformationBar					info;
	private ChatBar							chat;
	private ActionBar						actionb;
	private final ArrayList<FieldButton>	fbuttons	= new ArrayList<FieldButton>();
	private final EViewerMode				viewerMode;
	private final JFrame					frame;

	public FrameRenderer(final JFrame frame, final EViewerMode mode)
	{
		viewerMode = mode;
		this.frame = frame;
		createInitFrame();
	}

	private void createInitFrame()
	{

		setIconImage(new ImageIcon("resource/hase_und_igel_icon.png")
				.getImage());

		this.setSize(800, 600);

		final BackgoundPane bg = new BackgoundPane("resource/background.png");

		final GridBagLayout paneLayout = new GridBagLayout();
		final GridBagConstraints c1 = new GridBagConstraints();

		bg.setLayout(paneLayout);

		final int MAXROW = 5;

		for (int i = 0; i < MAXROW * MAXROW; i++)
		{

			c1.weightx = 1.0;
			c1.weighty = 1.0;
			c1.gridx = i % MAXROW;
			c1.gridy = i / MAXROW;
			c1.ipadx = 50; // make this component tall
			c1.ipady = 50; // make this component tall

			String back = "resource/igel.png";

			if (i % MAXROW == 0)
			{
				back = "resource/igel.png";
			}
			else if (i % MAXROW == 1)
			{
				back = "resource/rabbit.png";
			}
			else if (i % MAXROW == 2)
			{
				back = "resource/carrots.png";
			}
			else if (i % MAXROW == 3)
			{
				back = "resource/salad.png";
			}
			else if (i % MAXROW == 4)
			{
				back = "resource/position_2.png";
			}

			fbuttons.add(new FieldButton(back, i + 1, this));
			bg.add(fbuttons.get(i), c1);
		}

		info = new InformationBar(true);
		chat = new ChatBar();
		actionb = new ActionBar();
		final ScrollPane action = new ScrollPane();
		action.add(actionb);

		final JPanel leftPanel = new JPanel();

		final BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		leftPanel.add(info, BorderLayout.NORTH);
		leftPanel.add(bg, BorderLayout.CENTER);
		leftPanel.add(chat, BorderLayout.SOUTH);

		final BorderLayout framelayout = new BorderLayout();
		setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		this.add(action, BorderLayout.EAST);

		actionb.addRow("Aktionen: ");
		chat.addOtherMessage("Chat: ");
		chat.addOwnMessage("Prototyp: 0.1 alpha :)");

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

	}

	@Override
	public void updateBoard(final Board bo)
	{

	}

	@Override
	public void updateInfos(final int round)
	{

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

	public void askQuestion(final String question, final List<String> answers)
	{
		new QuestionDialog(question, answers);
	}

	public String answerQuestion(final String answer)
	{
		return answer;
	}

	public static void main(final String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				new FrameRenderer(null, null).setVisible(true);
			}
		});
	}

	public void updateClicked(final int fieldNumber)
	{
		final int index = fieldNumber - 1;

		for (int i = 0; i < fbuttons.size(); i++)
		{
			if (fbuttons.get(i).wasColorOn("blue"))
			{
				fbuttons.get(i).setFree();
				fbuttons.get(i).repaint();
			}
		}

		fbuttons.get(index).setOccupied("blue");
	}
}
