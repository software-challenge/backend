/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
public class ActionBar extends JPanel
{

	private SimpleAttributeSet	BLUE	= new SimpleAttributeSet();
	private SimpleAttributeSet	RED		= new SimpleAttributeSet();
	private SimpleAttributeSet	BLACK	= new SimpleAttributeSet();

	private JTextPane			pane;
	private JScrollPane			scroll;

	public ActionBar()
	{
		StyleConstants.setForeground(BLUE, Color.BLUE);
		StyleConstants.setForeground(RED, Color.RED);
		StyleConstants.setForeground(BLACK, Color.BLACK);

		setDoubleBuffered(true);

		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		pane = new JTextPane();
		pane.setText("");
		pane.setEditable(false);
		pane.setAutoscrolls(true);
		pane.setDoubleBuffered(true);

		scroll = new JScrollPane(pane);
		scroll.setAutoscrolls(true);

		scroll.setDoubleBuffered(true);

		this.add(scroll, BorderLayout.CENTER);
	}

	public synchronized void removeAllActions()
	{
		try
		{
			pane.getDocument().remove(0, pane.getDocument().getLength());
		}
		catch (BadLocationException e)
		{
			// should not happen
		}
	}

	private synchronized void appendText(final String text,
			final AttributeSet set)
	{
		try
		{
			pane.getDocument().insertString(
					pane.getDocument().getLength(), text, set);

		}
		catch (BadLocationException e)
		{
			// should not happen
			System.err.println(e.getStackTrace());
		}
	}

	public synchronized void setScrollBarToEnd()
	{
		pane.setCaretPosition(pane.getDocument().getLength());
	}

	public synchronized void addAction(String color, String playername,
			String action)
	{
		if (color.equals("Rot"))
		{
			if (pane.getText().equals(""))
			{
				appendText(playername, RED);
				appendText(action, BLACK);
			}
			else
			{
				appendText("\n" + playername, RED);
				appendText(action, BLACK);
			}
		}
		else if (color.equals("Blau"))
		{
			if (pane.getText().equals(""))
			{
				appendText(playername, BLUE);
				appendText(action, BLACK);
			}
			else
			{
				appendText("\n" + playername, BLUE);
				appendText(action, BLACK);
			}
		}

	}

	public synchronized void addNormal(String action)
	{
		if (pane.getText().equals(""))
		{
			appendText(action, BLACK);
		}
		else
		{
			appendText("\n" + action, BLACK);
		}

	}
}
