/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class QuestionPanel extends BackgroundPane
{
	private FrameRenderer		obs;
	private String				type				= "";
	private JLabel				textLabel;
	private BackgroundPane		buttonPanel;
	private BackgroundPane		textPanel;
	private final List<JButton>	answerButtons		= new ArrayList<JButton>();
	private Image				totalBackgroundImg;
	private int					displayedCompCount	= 1;
	private String				ravensburger		= "<html>Die Nutzung des Spielkonzeptes \"Hase und Igel\" (Name, Spielregeln und Grafik) <br> &nbsp;&nbsp;&nbsp;erfolgt mit freundlicher Genehmigung der Ravensburger Spieleverlag GmbH.</html>";

	private final String		FONTTYPE			= "New Courier";
	private final int			SIZE				= 16;

	public QuestionPanel(FrameRenderer obs)
	{
		this.obs = obs;

		textPanel = new BackgroundPane();
		buttonPanel = new BackgroundPane();

		FlowLayout textLayout = new FlowLayout();
		FlowLayout buttonLayout = new FlowLayout();

		textPanel.setLayout(textLayout);
		buttonPanel.setLayout(buttonLayout);

		BorderLayout dialogLayout = new BorderLayout();
		setLayout(dialogLayout);

		textLabel = new JLabel(ravensburger, JLabel.CENTER);

		textLabel.setFont(new Font(FONTTYPE, Font.BOLD, SIZE));

		textPanel.add(textLabel);

		this.add(textPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	@Override
	public void setBackground(Image img)
	{
		super.setBackground(img);

		totalBackgroundImg = img;

		int width = img.getWidth(this);
		int height = img.getHeight(this);

		int partHeight = height / displayedCompCount;

		Image textImg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Image button1Img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		textImg.getGraphics().drawImage(img, 0, 0, width, height, 0, 0, width,
				partHeight, this);

		button1Img.getGraphics().drawImage(img, 0, 0, width, height, 0,
				partHeight, width, height, this);

		textPanel.setBackground(textImg);
		buttonPanel.setBackground(button1Img);
	}

	public void showQuestion(String question, List<String> answers, String type)
	{
		textLabel.setText(question);
		textLabel.setVisible(true);

		AnswerListener awListener = new AnswerListener();

		answerButtons.clear();

		// add Buttons with answers
		for (int i = 0; i < answers.size(); i++)
		{
			answerButtons.add(new JButton(answers.get(i)));
			answerButtons.get(i).setName(answers.get(i));
			answerButtons.get(i).addMouseListener(awListener);
			// answerButtons.get(i).setFont(
			// new Font(FONTTYPE, Font.PLAIN, SIZE - 4));
		}

		for (int i = 0; i < answerButtons.size(); i++)
		{
			buttonPanel.add(answerButtons.get(i));
		}

		displayedCompCount = 2;
		this.add(textPanel, BorderLayout.NORTH);
		this.add(buttonPanel, BorderLayout.CENTER);

		setBackground(totalBackgroundImg);

		this.type = type;
	}

	public void hideComponents()
	{
		textLabel.setVisible(false);
		buttonPanel.removeAll();

		removeAll();

		if (totalBackgroundImg != null)
		{
			setBackground(totalBackgroundImg);
		}
		displayedCompCount = 1;
	}

	private class AnswerListener extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				obs.answerQuestion(e.getComponent().getName(), type);
			}
		}
	}
}
