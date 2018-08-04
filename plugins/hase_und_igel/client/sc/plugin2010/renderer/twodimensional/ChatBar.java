package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class ChatBar extends JPanel
{
	private SimpleAttributeSet	BLUE	= new SimpleAttributeSet();
	private SimpleAttributeSet	RED		= new SimpleAttributeSet();

	private JTextPane			pane;
	private JTextField			text;

	public ChatBar()
	{
		StyleConstants.setForeground(BLUE, Color.BLUE);
		StyleConstants.setForeground(RED, Color.RED);

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		pane = new JTextPane();
		pane.setText("");
		pane.setEditable(false);

		ScrollPane scroll = new ScrollPane();
		scroll.add(pane);

		text = new JTextField();

		this.add(scroll, BorderLayout.CENTER);
		this.add(text, BorderLayout.SOUTH);
	}

	private void appendText(String text, AttributeSet set)
	{
		try
		{
			pane.getDocument().insertString(pane.getDocument().getLength(),
					text, set);
		}
		catch (BadLocationException e)
		{
			// shouldnt happen
		}

	}

	public void addOwnMessage(String val)
	{
		if (pane.getText().equals(""))
		{
			appendText(val, BLUE);
		}
		else
		{
			appendText("\n" + val, BLUE);
		}
	}

	public void addOtherMessage(String val)
	{
		if (pane.getText().equals(""))
		{
			appendText(val, RED);
		}
		else
		{
			appendText("\n" + val, RED);
		}
	}
}
