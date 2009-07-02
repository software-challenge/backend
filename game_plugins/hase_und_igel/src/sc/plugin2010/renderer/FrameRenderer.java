/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * @author ffi
 * 
 */
public class FrameRenderer extends JFrame implements Renderer
{

	public FrameRenderer(JFrame frame)
	{
		createInitFrame(frame);
	}

	private void createInitFrame(JFrame frame)
	{
		ChatBar chat = new ChatBar(200, 200);
		InformationBar info = new InformationBar(100, 400);
		ActionBar action = new ActionBar(300, 100);

		GridBagLayout mylayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		this.setLayout(mylayout);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.8;
		c.weighty = 1.0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 0;
		this.add(info, c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 4;
		c.gridx = 3;
		c.gridy = 0;
		this.add(action, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.9;
		c.weighty = 0.9;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 4;
		this.add(chat, c);

		this.setSize(700, 500);
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
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				new FrameRenderer(null).setVisible(true);
			}
		});
	}
}
