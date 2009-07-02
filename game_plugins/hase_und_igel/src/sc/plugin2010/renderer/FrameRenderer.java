/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

		setContentPane(new BackGroundPane("resource/test3.png"));

		BackGroundButton test = new BackGroundButton("resource/test.png");
		test.setIcon("resource/test2.png");

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

		c.fill = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 0.9;
		c.weighty = 0.9;
		c.gridheight = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.ipady = 100; // make this component tall
		c.ipadx = 100; // make this component tall

		this.add(test, c);

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

	class BackGroundButton extends JButton
	{
		Image	img		= null;
		Image	icon	= null;

		BackGroundButton(String imagefile)
		{
			super();
			if (imagefile != null)
			{
				MediaTracker mt = new MediaTracker(this);
				img = Toolkit.getDefaultToolkit().getImage(imagefile);
				mt.addImage(img, 0);
				try
				{
					mt.waitForAll();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		protected void setIcon(String iconimagefile)
		{
			MediaTracker mt = new MediaTracker(this);
			icon = Toolkit.getDefaultToolkit().getImage(iconimagefile);
			mt.addImage(icon, 0);
			try
			{
				mt.waitForAll();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
			if (!(icon == null))
			{
				g.drawImage(icon, 10, 10, this.getWidth() - 20, this
						.getHeight() - 20, this);
			}
		}
	}

	class BackGroundPane extends JPanel
	{
		Image	img	= null;

		BackGroundPane(String imagefile)
		{
			if (imagefile != null)
			{
				MediaTracker mt = new MediaTracker(this);
				img = Toolkit.getDefaultToolkit().getImage(imagefile);
				mt.addImage(img, 0);
				try
				{
					mt.waitForAll();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}

}
