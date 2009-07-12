/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sc.plugin2010.renderer.RendererUtil;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class QuestionDialog extends JDialog
{
	private static final int	DEFAULTHEIGHT	= 100;
	private static final int	DEFAULTWIDTH	= 300;
	private FrameRenderer		obs;
	private String				type;

	public QuestionDialog(String question, List<String> answers,
			FrameRenderer obs, String type)
	{
		setTitle("?");

		this.type = type;

		this.obs = obs;

		setIconImage(RendererUtil.getImage("resource/hase_und_igel_icon.png"));

		// Size of Screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Position to show in center
		int top = (screenSize.height - DEFAULTHEIGHT) / 2;
		int left = (screenSize.width - DEFAULTWIDTH) / 2;

		// set position
		setLocation(left, top);

		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);

		AnswerListener awListener = new AnswerListener();

		JPanel buttonPanel = new JPanel();

		FlowLayout buttonLayout = new FlowLayout();

		buttonPanel.setLayout(buttonLayout);

		// add Buttons with answers
		for (int i = 0; i < answers.size(); i++)
		{
			JButton jbut = new JButton(answers.get(i));
			jbut.setName(answers.get(i));
			jbut.addMouseListener(awListener);
			buttonPanel.add(jbut);
		}

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		BorderLayout dialogLayout = new BorderLayout();
		setLayout(dialogLayout);

		this.add(new JLabel(question, JLabel.CENTER), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		setModal(true);
		setVisible(true);
	}

	private class AnswerListener extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				obs.answerQuestion(e.getComponent().getName(), type);
				dispose();
			}
		}
	}
}
