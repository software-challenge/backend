/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sc.plugin2010.gui.EViewerMode;

/**
 * @author ffi
 * 
 */
public class FrameRenderer extends JFrame implements Renderer, IClickObserver
{
	private InformationBar			info;
	private ChatBar					chat;
	private ActionBar				actionb;
	private ArrayList<FieldButton>	fbuttons	= new ArrayList<FieldButton>();
	private EViewerMode				viewerMode;
	private JFrame					frame;

	public FrameRenderer(JFrame frame, EViewerMode mode)
	{
		this.viewerMode = mode;
		this.frame = frame;
		createInitFrame();
	}

	private void createInitFrame()
	{

		this.setSize(800, 600);

		BackgoundPane bg = new BackgoundPane("resource/background.png");

		GridBagLayout paneLayout = new GridBagLayout();
		GridBagConstraints c1 = new GridBagConstraints();

		bg.setLayout(paneLayout);

		int MAXROW = 5;

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
		ScrollPane action = new ScrollPane();
		action.add(actionb);

		JPanel leftPanel = new JPanel();

		BorderLayout layout = new BorderLayout();
		leftPanel.setLayout(layout);

		leftPanel.add(info, BorderLayout.NORTH);
		leftPanel.add(bg, BorderLayout.CENTER);
		leftPanel.add(chat, BorderLayout.SOUTH);

		BorderLayout framelayout = new BorderLayout();
		this.setLayout(framelayout);

		this.add(leftPanel, BorderLayout.CENTER);
		this.add(action, BorderLayout.EAST);

		actionb.addRow("Aktionen: ");
		chat.addRow("Chat: ");
		chat.addRow("Prototyp: 0.1alpha :)");

		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}

	@Override
	public void updateData()
	{
		// TODO 2 spieler und observer...
		info.setTurn(0);
		info.setRound(0);

		info.setCarrots(0);
	}

	public void updatePlayer(int playerid)
	{

	}

	public void updateBoard()
	{

	}

	public void updateAction(String doneAction)
	{
		actionb.addRow(doneAction);
	}

	public void askQuestion(String question, List<String> answers)
	{
		new QuestionDialog(question, answers);
	}

	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				new FrameRenderer(null, null).setVisible(true);
			}
		});
	}

	@Override
	public void updateClicked(int fieldNumber)
	{
		int index = fieldNumber - 1;

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
