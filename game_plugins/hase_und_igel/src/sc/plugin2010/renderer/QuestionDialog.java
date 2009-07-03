/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * @author ffi
 * 
 */
public class QuestionDialog extends JDialog
{
	private static final int	DEFAULTHEIGHT	= 100;
	private static final int	DEFAULTWIDTH	= 300;
	private List<JButton>		answerButtons	= new ArrayList<JButton>();

	public QuestionDialog(String question, List<String> answers)
	{
		setTitle("?");

		// Size of Screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Position to show in center
		int top = (screenSize.height - DEFAULTHEIGHT) / 2;
		int left = (screenSize.width - DEFAULTWIDTH) / 2;

		// set position
		setLocation(left, top);

		setSize(DEFAULTWIDTH, DEFAULTHEIGHT);

		for (int i = 0; i < answers.size(); i++)
		{
			JButton jbut = new JButton();
			answerButtons.add(jbut);
		}

		setModal(true);
		setVisible(true);
	}
}
