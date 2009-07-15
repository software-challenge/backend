/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class QuestionPanel extends JPanel
{
	private static final int	DEFAULTHEIGHT	= 100;
	private FrameRenderer		obs;
	private String				type;

	public QuestionPanel(String question, List<String> answers,
			FrameRenderer obs, String type)
	{
		// setTitle("?");

		this.type = type;

		this.obs = obs;

		// setIconImage(RendererUtil.getImage("resource/hase_und_igel_icon.png"));

		AnswerListener awListener = new AnswerListener();

		JPanel buttonPanel = new JPanel();

		FlowLayout buttonLayout = new FlowLayout();

		buttonPanel.setLayout(buttonLayout);

		int width = 50;

		// add Buttons with answers
		for (int i = 0; i < answers.size(); i++)
		{
			JButton jbut = new JButton(answers.get(i));
			jbut.setName(answers.get(i));
			jbut.addMouseListener(awListener);
			buttonPanel.add(jbut);
			width += jbut.getPreferredSize().width;
		}

		// setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		BorderLayout dialogLayout = new BorderLayout();
		setLayout(dialogLayout);

		this.add(new JLabel(question, JLabel.CENTER), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		setSize(width, 200);

		// setLocationRelativeTo(null);

		// setModal(true);
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
				// dispose();
			}
		}
	}
}
