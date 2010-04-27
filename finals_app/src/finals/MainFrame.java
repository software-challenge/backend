package finals;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1328120526598916894L;
	LinkedList<Final_Step> steps = new LinkedList<Final_Step>();
	int currentStep = 0;
	Panel pan;
	Panel contestPanel;
	JButton nextButton = new JButton();
	JButton lastButton = new JButton();
	JCheckBoxMenuItem showPresentation;
	FinalsConfiguration config;
	GridBagLayout mgr;
	GridBagConstraints c;
	JFrame contestFrame;
	ConfigsFrame configFrame;
	JMenuBar menuBar;
	JMenuItem configItem;

	MainFrame() {
		mgr = new GridBagLayout();
		
		// Create Menu
		menuBar = new JMenuBar();
		configItem = new JMenuItem("Konfiguration");
		configItem.setVisible(true);
		configItem.addActionListener(this);
		JMenu files = new JMenu("Einstellungen", true);
		files.add(configItem);
		showPresentation = new JCheckBoxMenuItem("Präsentationsmodus");
		showPresentation.setVisible(true);
		showPresentation.addActionListener(this);
		showPresentation.setSelected(false);
		files.add(showPresentation);
		menuBar.add(files);
		menuBar.setVisible(true);
		this.setJMenuBar(menuBar);
		
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
		this.contestFrame.setVisible(false);
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
		// Add dummy config
		config = new FinalsConfiguration("HUND", "Affe gegen Kanacke!", new Date(), 100);
		// Config frame
		configFrame = new ConfigsFrame(config);
		configFrame.setSize(400,400);
		configFrame.setVisible(false);
		
		// Add dummy shit....should be imported from XML!
		Contestant one = new Contestant("Test A", "A Stadt");
		Contestant two = new Contestant("Test B", "B Stadt");
		Contestant three = new Contestant("Test C", "C Stadt");
		Contestant four = new Contestant("Test D", "D Stadt");
		LinkedList<Contestant> contestants = new LinkedList<Contestant>();
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
		nextButton = new JButton();
		nextButton.setText(">");
		nextButton.addActionListener(this);
		nextButton.setVisible(true);
		nextButton.setLocation(400, 400);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.25;
		c.weighty = 0.25;
		c.fill = GridBagConstraints.NONE;
		this.add(nextButton, c);

		lastButton.setText("<");
		lastButton.addActionListener(this);
		lastButton.setVisible(true);
		c.gridx = 0;
		this.add(lastButton, c);
		
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
		if (e.getSource() == nextButton) {
			if (curr.isFinished() && currentStep < steps.size() - 1) {
				currentStep++;
			}
			curr.stepForward();
			this.repaint();
		}else if (e.getSource() == lastButton) {
			curr.stepBackward();
			this.repaint();
		}else if (e.getSource() == showPresentation) {
			contestFrame.setVisible(!showPresentation.isSelected());
			contestFrame.repaint();
		}else if (e.getSource() == configItem) {
			configFrame.setVisible(true);
			configFrame.repaint();
		}

	}


}
