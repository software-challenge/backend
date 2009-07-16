/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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
	private FrameRenderer		obs;
	private String				type		= "";
	private JLabel				textLabel;
	private TransparentPanel	buttonPanel;

	private final String		FONTTYPE	= "New Courier";
	private final int			SIZE		= 12;

	public QuestionPanel(FrameRenderer obs)
	{

		Color bg = new Color(255, 255, 255, 120);

		setBackground(bg);

		this.obs = obs;

		buttonPanel = new TransparentPanel();

		FlowLayout buttonLayout = new FlowLayout();

		buttonPanel.setLayout(buttonLayout);

		BorderLayout dialogLayout = new BorderLayout();
		setLayout(dialogLayout);

		textLabel = new JLabel("", JLabel.CENTER);

		textLabel.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		this.add(textLabel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		// setSize(100, 200);

		// setMinimumSize(new Dimension(100, 50));
		// setPreferredSize(new Dimension(100, 50));

		setVisible(true);
	}

	public void showQuestion(String question, List<String> answers, String type)
	{
		textLabel.setText(question);
		textLabel.setVisible(true);

		AnswerListener awListener = new AnswerListener();

		// add Buttons with answers
		for (int i = 0; i < answers.size(); i++)
		{
			JButton jbut = new JButton(answers.get(i));
			jbut.setName(answers.get(i));
			jbut.addMouseListener(awListener);
			buttonPanel.add(jbut);
		}

		this.type = type;
	}

	public void hideComponents()
	{
		textLabel.setVisible(false);
		buttonPanel.removeAll();
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
