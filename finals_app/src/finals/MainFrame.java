package finals;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

public class MainFrame extends JFrame implements ActionListener, MenuListener{
	private static final long serialVersionUID = 1328120526598916894L;
	LinkedList<Final_Step> steps = new LinkedList<Final_Step>();
	int currentStep = 0;
	JPanel pan;
	JPanel contestPanel;
	JButton nextButton = new JButton();
	JButton lastButton = new JButton();
	JButton undoAll = new JButton();
	JButton doAll = new JButton();
	JButton publish = new JButton();
	JMenuItem showPresentation;
	JMenuItem showRanking;
	JButton nextReplayButton;
	FinalsConfiguration config;
	GridBagLayout mgr;
	GridBagConstraints c;
	JFrame presentationFrame;
	ConfigsFrame configFrame;
	JMenuBar menuBar;
	JMenuItem configItem;
	JMenuItem startTestGame;
	JMenuItem info;
	File finaleArchiv;
	JFrame fileFrame;
	JFileChooser fileSelectionFrame;
	ResultsFrame results;
	Ranking ranking;

	MainFrame(String[] args) {
		config = new FinalsConfiguration();
		this.setEnabled(false);
	
		// Create Menu
		menuBar = new JMenuBar();
		JMenu files = new JMenu("Einstellungen", true);
		info = new JMenuItem("Info");
		info.addActionListener(this);
		configItem = new JMenuItem("Konfiguration");
		configItem.setVisible(true);
		configItem.addActionListener(this);
		files.addMenuListener(this);
		files.add(configItem);
		showPresentation = new JMenuItem("Präsentationsmodus");
		showPresentation.setVisible(true);
		showPresentation.addActionListener(this);
		showPresentation.setSelected(false);
		files.add(showPresentation);
		startTestGame = new JMenuItem("Einführungsspiel starten");
		startTestGame.setVisible(true);
		startTestGame.addActionListener(this);
		files.add(startTestGame);
		showRanking = new JMenuItem("Tabelle anzeigen");
		showRanking.setVisible(true);
		showRanking.addActionListener(this);
		files.add(showRanking);
		menuBar.add(files);
		JMenu help = new JMenu("Hilfe");
		help.add(info);
		help.addActionListener(this);
		menuBar.add(help);
		menuBar.setVisible(true);
		this.setJMenuBar(menuBar);
		
		// Create results frame
		results = new ResultsFrame(this,500,500);
		results.setVisible(false);
		
		// Setup Layout
		mgr = new GridBagLayout();
		setLayout(mgr);
		c = new GridBagConstraints();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(config.titleText);
		setSize(1000, 1000);
		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		this.setBackground(config.BACKGROUND);
		setVisible(true);
		
		// Create Panel
		pan = new JPanel();
		pan.setBackground(config.BACKGROUND);
		pan.setDoubleBuffered(true);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		mgr.addLayoutComponent(pan,c);
		this.add(pan);

		// Config the contest window!
		this.presentationFrame = new PresentationFrame(this);
		this.presentationFrame.setSize(700, 700);
		this.presentationFrame.setTitle("asgard");
		this.presentationFrame.setVisible(false);

		
		// Create presentation panel
		this.contestPanel = new JPanel();
		contestPanel.setBackground(config.BACKGROUND);
		presentationFrame.add(contestPanel);
	
		// Check if there was a contest file given as a parameter
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
	
	/**
	 * Parses the InputStream given to get the informations about the finale!
	 * 
	 * @param in
	 * 		input that should be parsed (should be xml ;) )
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseInput(InputStream in) throws ParserConfigurationException, SAXException, IOException{
		 SAXParserFactory factory = SAXParserFactory.newInstance();
	     SAXParser saxParser = factory.newSAXParser();
	     ImportHandler handler = new ImportHandler();
	     handler.main = this;
	     saxParser.parse(in, handler);
	}

	/**
	 * Create a lot of gui shit and setup the contest including final steps
	 */
	private void createGUI() {
		
		// Check if there is a .conf file and load given values
		config.load(this);
		
		// Create config frame
		configFrame = new ConfigsFrame(config);
		configFrame.main = this;
		configFrame.setSize(450,300);
		configFrame.setVisible(false);
		
		// Set windows name
		this.setTitle(config.titleText + config.getGameName());
		
		// Try to parse the input else ask for another file
		try {
			ZipFile zipFile = new ZipFile(finaleArchiv);
			unzip(zipFile);
			parseInput(zipFile.getInputStream(zipFile.getEntry("final.xml")));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Beim Laden des Archivs trat ein Fehler auf!");
			fileFrame.setVisible(true);
			return;
		}
		
		// If everything is parsed and created, the steps can create their widgets
		for (Final_Step step : steps){
			step.createStepWidgets();
		}
		
		// In step one all names should be visible
		steps.get(0).showAllNames();

		// Setup the whole hud to coordinate the finale
		setupHUD();

		
		for (Final_Step step : steps) {
			step.publishToContestPanel();
		}	
		
		repaint();
		
	}
	
	/**
	 * Setup a hud for this frame, to coordinate the finale
	 */
	public void setupHUD(){

		JPanel grp = new JPanel();
		grp.setBackground(config.BACKGROUND);
		
		doAll.setText(">>|");
		doAll.addActionListener(this);
		doAll.setVisible(true);
		
		nextButton = new JButton();
		nextButton.setText("Skip");
		nextButton.addActionListener(this);
		nextButton.setVisible(true);
		nextButton.setLocation(400, 400);

		nextReplayButton = new JButton("Play");
		nextReplayButton.setVisible(true);
		nextReplayButton.addActionListener(this);

		
		lastButton.setText("Back");
		lastButton.addActionListener(this);
		lastButton.setVisible(true);
		
		undoAll.setText("|<<");
		undoAll.addActionListener(this);
		undoAll.setVisible(true);
		
		
		grp.add(undoAll);
		grp.add(lastButton);
		grp.add(nextReplayButton);
		grp.add(nextButton);
		grp.add(doAll);
		c.weightx = 0.25;
		c.weighty = 0.05;
		c.gridy = 1;
		grp.setVisible(true);
		mgr.addLayoutComponent(grp,c);
		add(grp);
		
		// Create a JPanel containing the "publish" button!
		JPanel publishPanel = new JPanel();
		publishPanel.setBackground(config.BACKGROUND);
		publish.setText("Veröffentlichen");
		publish.addActionListener(this);
		publish.setVisible(true);
		publish.setEnabled(false);
		publish.setBackground(config.BACKGROUND);
		c.weighty = 0.1;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		publishPanel.add(publish);
		add(publishPanel,c);
		
	}
	
	/**
	 * Unzip the file given to a temporary folder, so the replays can be loaded by the server!
	 * 
	 * @param in
	 * 	ZipFile containing the contest files!
	 * @throws IOException
	 * 	Something went wrong!
	 */
	private void unzip(ZipFile in) throws IOException{
	    Enumeration enumeration = in.entries();
	    while (enumeration.hasMoreElements()) {
	      ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
	      System.out.println("Unzipping: " + zipEntry.getName());
	      BufferedInputStream bis = new BufferedInputStream(in.getInputStream(zipEntry));
	      int size;
	      byte[] buffer = new byte[2048];
	      BufferedOutputStream bos = new BufferedOutputStream(
	          new FileOutputStream(zipEntry.getName()), buffer.length);
	      while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
	        bos.write(buffer, 0, size);
	      }
	      bos.flush();
	      bos.close();
	      bis.close();
	      File n = new File(zipEntry.getName());
	      n.deleteOnExit();
	    }
	}

	
	@Override
	public void paint(Graphics e) {
		setTitle(config.titleText+config.getGameName());
		super.paint(e);
		for (Final_Step step : steps) {
			step.initConnections();
			step.repaint();
		}
	}
	
	/**
	 * Handles a click on the forward button (mostly switching the steps if finished)
	 * @param current
	 */
	private void handleForward(Final_Step current){
		System.out.println("SCHNORK!");
		if(current.isFinished()){
			System.out.println("Current step is finished, should go to next one!");
			for(MatchWidget wid : current.matchWidgets){
				wid.setSelected(false);
			}
			
			if(!current.isFinals()){
				current = steps.get(current.order);
			}
		}
		current.stepForward();
		this.currentStep = current.order-1;
		current.setNamesVisible(true);
		if(current.isSmallFinals()) current.getFinals().setNamesVisible(true);
	}

	/**
	 * Handles the whole actions on components
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Info button of the menu
		if(e.getSource() == info){
			InfoFrame inf = new InfoFrame(this);
			System.out.println("Info clicked!");
		}
		
		// Filechooser to load finale archiv
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
		
		// The results table button of the menu
		if(e.getSource() == showRanking){
			if(!results.isVisible()){
				int resp = JOptionPane.showConfirmDialog(this,"Sind Sie sicher, dass Sie die Ergebnisse anzeigen lassen wollen?");
				if(resp != JOptionPane.OK_OPTION){ return; };
			}
			results.setVisible(true);
			results.createRanking(ranking.getRanking());
			
		}
	
		// The publish button, to publish the contents of the panel to the presentation panel
		if(e.getSource() == publish){
			contestPanel.getGraphics().clearRect(0,0,contestPanel.getWidth(),contestPanel.getHeight());
			for (Final_Step step : steps) {
				step.publishToContestPanel();
			}
		}
		
		// The button to start the predefined intro-game
		if(e.getSource() == startTestGame){
			if(config.introReplayPath == null){
				JOptionPane.showMessageDialog(this,"Bitte wählen die zuerst im Konfigurationsmenu ein entsprechendes Replay aus");
			}else{
				System.out.println("Running intro replay!");
				config.setOpenReplay(true);
				runReplay(config.introReplayPath, true);
			}
		}
		
		// The button to start the next replay
		Final_Step curr = steps.get(currentStep);
		if(e.getSource() == nextReplayButton){
			setAllButtons(false);
			config.setOpenReplay(true);
			handleForward(curr);
		}
		
		// The button to skip the next replay
		if (e.getSource() == nextButton) {
			config.setOpenReplay(false);
			handleForward(steps.get(currentStep));
		}
		
		// The button to undo the last step
		if (e.getSource() == lastButton) {
			if(curr.isInit() && currentStep > 0){
				if(curr.isSmallFinals()) curr.getFinals().setNamesVisible(false);
				currentStep--;
			}
			curr.stepBackward();
		}
		
		// The button to do all matches
		if (e.getSource() == doAll) {
			for (Final_Step step : steps) {
				step.doAllMatches();
				currentStep = steps.size()-2;
			}
		}
		
		// The button to undo all matches
		if (e.getSource() == undoAll) {
			for (Final_Step step : steps) {
				step.undoAllMatches();
				//step.repaint();
				currentStep = 0;
			}
		}
		
		// The button to show the presentation frame 
		if (e.getSource() == showPresentation) {
			presentationFrame.setVisible(true);
			presentationFrame.repaint();
			publish.setEnabled(true);
		}
		
		// The button to show the config frame
		if (e.getSource() == configItem) {
			configFrame.setLocation(new Point (this.getLocation().x, this.getLocation().y+50));
			configFrame.setVisible(true);
			configFrame.repaint();
		}
		
		this.repaint();
	}
	
	/**
	 * Adds a finals step, this should only be used by the parser!
	 * 
	 * @param step
	 * @param order
	 */
	public void addFinalsStep(Final_Step step, int order){
		for (int i = 0; i < steps.size(); i++) {
			if(order < steps.get(i).order){
				steps.add(i,step);
				return;
			}
		}
		steps.add(step);
	}
	
	/**
	 * Start the replay at path
	 * @param path
	 * 		The path where the replay can be found at
	 */
	public void runReplay(String path){
		runReplay(path, false);
	}
	
	/**
	 * Start the replay at path
	 * @param path
	 * 		The path where the replay can be found at
	 * @param repeat
	 * 		Should the replay be repeatet automatically?
	 */
	public void runReplay(String path, boolean repeat) {
		runReplay(path, repeat, null);
	}
	
	/**
	 * Start the replay at path
	 * @param path
	 * 		The path where the replay can be found at
	 * @param repeat
	 * 		Should the replay be repeatet automatically?
	 * @param callback
	 * 		The matchwidget the game extends
	 * 		
	 */
	public void runReplay(String path, boolean repeat, MatchWidget callback){
		final String p = path;
		final MainFrame listener = this;
		final boolean rep = repeat;
		final MatchWidget callb = callback;
		new Thread() {
			@Override
			public void run() {			
				if(config.isOpenReplay()){
					try {
						Process prcs;
						String command = ("java -jar "+config.getServerJarPath() + " --plugin "+config.getServerPluginPath()+(rep ? " --repeat --repeat-delay 15000	" : "")+" --stepspeed "+config.getSpeed()+" -m -f -r "+p);
						System.out.println("Executing Server: "+command);
						prcs = Runtime.getRuntime().exec(command);
						InputStream cmd_output = prcs.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(cmd_output));
						String out = reader.readLine();
						while(out != null){
							System.out.println(out);
							out = reader.readLine();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}					
				}
				listener.setAllButtons(true);
				if(callb != null) {
					callb.getMatch().doNextStep();
					callb.setSelected(true);
					repaint();
				}
			}
		}.start();
		this.repaint();
	}
	
	/**
	 * Set the enabled state of all buttons to b
	 * @param b
	 * 		Are the buttons enabled?
	 */
	private void setAllButtons(Boolean b){
		menuBar.setEnabled(b);
		configFrame.setEnabled(b);
		results.setEnabled(b);
		nextButton.setEnabled(b&&config.isSkipActive());
		nextReplayButton.setEnabled(b);
		undoAll.setEnabled(b&&config.isSkipActive());
		doAll.setEnabled(b&&config.isSkipActive());
		publish.setEnabled(b);	
		lastButton.setEnabled(b&&config.isSkipActive());
	}
	
	/**
	 * MenuListener function
	 */
	@Override
	public void menuCanceled(MenuEvent e){
		this.repaint();
	}

	/**
	 * MenuListener function
	 */
	@Override
	public void menuDeselected(MenuEvent e) {
		this.repaint();
	}

	/**
	 * MenuListener function
	 */
	@Override
	public void menuSelected(MenuEvent e) {
		
	}
}
