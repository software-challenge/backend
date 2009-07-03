/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.BorderLayout;
import java.awt.ScrollPane;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author ffi
 * 
 */
public class ChatBar extends JPanel
{
	private JTextArea	area;
	private JTextField	text;

	public ChatBar()
	{
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		area = new JTextArea();
		area.setText("");
		area.setEditable(false);

		ScrollPane scroll = new ScrollPane();
		scroll.add(area);

		text = new JTextField();

		this.add(scroll, BorderLayout.CENTER);
		this.add(text, BorderLayout.SOUTH);
	}

	public void addRow(String val)
	{
		if (!area.getText().equals(""))
		{
			area.setText(area.getText() + "\n" + val);
		}
		else
		{
			area.setText(val);
		}
	}
}
