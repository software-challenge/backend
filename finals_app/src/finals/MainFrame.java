package finals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame implements ActionListener {
	LinkedList<Final_Step> steps = new LinkedList<Final_Step>();
	int currentStep = 0;
	Panel pan;
	Panel contestPanel;
	JButton next = new JButton();
	JButton last = new JButton();
	JButton config = new JButton();
	GridBagLayout mgr;
	GridBagConstraints c;
	JFrame contestFrame;

	MainFrame() {
		mgr = new GridBagLayout();

		this.setLayout(mgr);
		this.c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Hund");
		this.setSize(1000, 1000);
		this.setVisible(true);
		pan = new Panel();
		pan.setSize(300, 300);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		this.add(pan, c);

		// Config the contest window!
		this.contestFrame = new PresentationFrame(this);
		this.contestFrame.setSize(700, 700);
		this.contestFrame.setTitle("asgard");
		this.contestFrame.setVisible(true);
		// Add contestPanel to contest window
		this.contestPanel = new Panel();
		contestFrame.add(contestPanel);

		createGUI();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainFrame();
	}

	private void createGUI() {
		// Add dummy shit....should be imported from XML!
		Contestant one = new Contestant("Test A", "A Stadt");
		Contestant two = new Contestant("Test B", "B Stadt");
		Contestant three = new Contestant("Test C", "C Stadt");
		Contestant four = new Contestant("Test D", "D Stadt");
		LinkedList contestants = new LinkedList<Contestant>();
		contestants.add(one);
		contestants.add(two);
		contestants.add(three);
		contestants.add(four);
		LinkedList<Round> rounds1 = new LinkedList<Round>();
		rounds1.add(new Round("123.zip", one, 123, 321));
		rounds1.add(new Round("123.zip", one, 123, 321));
		rounds1.add(new Round("123.zip", one, 123, 321));
		rounds1.add(new Round("123.zip", two, 123, 321));
		rounds1.add(new Round("123.zip", two, 123, 321));
		rounds1.add(new Round("123.zip", two, 123, 321));
		LinkedList<Round> rounds2 = new LinkedList<Round>();
		rounds2.add(new Round("567.zip", four, 456, 654));
		rounds2.add(new Round("567.zip", four, 456, 654));
		LinkedList<Match> matches = new LinkedList<Match>();
		matches.add(new Match(one, two, rounds1));
		matches.add(new Match(three, four, rounds2));
		this.steps.add(new Final_Step(pan, contestPanel, matches, contestants,
				new Ranking(contestants), this.steps, true));

		Contestant five = new Contestant("Test A", "A Stadt");
		Contestant six = new Contestant("Test B", "B Stadt");
		Contestant seven = new Contestant("Test C", "C Stadt");
		Contestant eight = new Contestant("Test D", "D Stadt");
		LinkedList<Contestant> contestants2 = new LinkedList<Contestant>();
		contestants2.add(one);
		contestants2.add(two);
		contestants2.add(three);
		contestants2.add(four);
		LinkedList<Round> rounds3 = new LinkedList<Round>();
		rounds3.add(new Round("123.zip", five, 123, 321));
		rounds3.add(new Round("123.zip", five, 123, 321));
		rounds3.add(new Round("123.zip", five, 123, 321));
		rounds3.add(new Round("123.zip", five, 123, 321));
		rounds3.add(new Round("123.zip", six, 123, 321));
		rounds3.add(new Round("123.zip", six, 123, 321));
		LinkedList<Match> matches2 = new LinkedList<Match>();
		matches2.add(new Match(five, six, rounds3));

		this.steps.add(new Final_Step(pan, contestPanel, matches2,
				contestants2, new Ranking(contestants2), this.steps, false));
		next = new JButton();
		next.setText("Vor");
		next.addActionListener(this);
		next.setVisible(true);
		next.setLocation(400, 400);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.25;
		c.weighty = 0.25;
		c.fill = GridBagConstraints.NONE;
		this.add(next, c);

		last.setText("Zurück");
		last.addActionListener(this);
		last.setVisible(true);
		c.gridx = 1;
		this.add(last, c);
		/*
		 * config.setText("test"); config.addActionListener(this);
		 * config.setVisible(true); c.gridy=2; this.add(config,c);
		 */

	}

	@Override
	public void paint(Graphics e) {
		e.clearRect(0, 0, this.HEIGHT, this.WIDTH);
		super.paint(e);
		for (Final_Step step : steps) {
			step.repaint();
			step.initConnections();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Final_Step curr = steps.get(currentStep);
		if (e.getSource() == next) {
			if (curr.isFinished() && currentStep < steps.size() - 1) {
				currentStep++;
			}
			curr.stepForward();
			this.repaint();
		} else if (e.getSource() == config) {
			ConfigsFrame frame = new ConfigsFrame(new FinalsConfiguration(
					"HUND", "Affe gegen Kanacke!", new Date(), 100));
			frame.setSize(100, 100);
			frame.setVisible(true);

		} else if (e.getSource() == last) {
			curr.stepBackward();
			this.repaint();
		}

	}

}
