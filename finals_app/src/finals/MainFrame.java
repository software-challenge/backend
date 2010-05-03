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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.zip.ZipFile;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

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
	File finaleArchiv;
	JFrame fileFrame;
	JFileChooser fileSelectionFrame;

	MainFrame(String[] args) {
		this.setEnabled(false);
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
		
	
		if(args.length > 0){
			finaleArchiv = new File(args[0]);
			this.setEnabled(true);
			createGUI();
		}else{
			fileFrame = new JFrame("Bitte Final-Archiv auswählen");
			fileSelectionFrame = new JFileChooser();
			fileSelectionFrame.addActionListener(this);
			fileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			fileFrame.setSize(500,400);
			fileFrame.setVisible(true);
			fileFrame.add(fileSelectionFrame);
			
		}

	}

	public static void main(String[] args){
		new MainFrame(args);	     
	}
	
	public void parseInput(InputStream in) throws ParserConfigurationException, SAXException, IOException{
		 SAXParserFactory factory = SAXParserFactory.newInstance();
	     SAXParser saxParser = factory.newSAXParser();
	     ImportHandler handler = new ImportHandler();
	     handler.main = this;
	     saxParser.parse(in, handler);
	}

	private void createGUI() {

		
		try {
			ZipFile zipFile = new ZipFile(finaleArchiv);
			parseInput(zipFile.getInputStream(zipFile.getEntry("final.xml")));
			
		} catch (IOException e) {
			System.out.println("Could not find file: " + finaleArchiv.getPath());
			return;
		} catch (ParserConfigurationException e) {
			System.out.println("Parser misconfiguration detected");
		} catch (SAXException e) {
			System.out.println("XML file is corrupted!");
		}
		
		
		Object[] stepsA = steps.toArray();
		
		for (Final_Step step : steps){
			step.createStepWidgets();
		}

		steps.get(0).showAllNames();
		// Add dummy config
		config = new FinalsConfiguration("HUND", "Affe gegen Kanacke!", new Date(), 100);
		// Config frame
		configFrame = new ConfigsFrame(config);
		configFrame.setSize(400,400);
		configFrame.setVisible(false);
		
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
		if (e.getSource().getClass()==JFileChooser.class) {
			finaleArchiv = ((JFileChooser)e.getSource()).getSelectedFile();
			if(finaleArchiv != null){
				fileFrame.setVisible(false);
				this.setEnabled(true);
				createGUI();
			}else{
				System.exit(ABORT);
			}
			return;
		}
		
		Final_Step curr = steps.get(currentStep);
		if (e.getSource() == nextButton) {
			if (curr.isFinished() && currentStep < steps.size() - 1) {
				currentStep++;
			}
			curr.stepForward();
			this.repaint();
		}else if (e.getSource() == lastButton) {
			if(curr.isInit() && currentStep > 0){
				currentStep--;
			}
			curr.stepBackward();
			this.repaint();
		}else if (e.getSource() == showPresentation) {
			contestFrame.setVisible(showPresentation.isSelected());
			contestFrame.repaint();
		}else if (e.getSource() == configItem) {
			configFrame.setVisible(true);
			configFrame.repaint();
		}

	}
	
	public void addFinalsStep(Final_Step step, int order){
		for (int i = 0; i < steps.size(); i++) {
			if(order < steps.get(i).order){
				steps.add(i,step);
				return;
			}
		}
		steps.add(step);
	}

}
