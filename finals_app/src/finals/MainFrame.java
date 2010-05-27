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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
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
	JPanel pan;
	JPanel contestPanel;
	JButton nextButton = new JButton();
	JButton lastButton = new JButton();
	JButton undoAll = new JButton();
	JButton doAll = new JButton();
	JButton publish = new JButton();
	JMenuItem showPresentation;
	JMenuItem showRanking;
	JCheckBox openReplay;
	FinalsConfiguration config;
	GridBagLayout mgr;
	GridBagConstraints c;
	JFrame contestFrame;
	ConfigsFrame configFrame;
	JMenuBar menuBar;
	JMenuItem configItem;
	JMenuItem startTestGame;
	File finaleArchiv;
	JFrame fileFrame;
	JFileChooser fileSelectionFrame;
	String titleText = "Software Challenge Finale: ";
	ResultsFrame results = new ResultsFrame();
	Ranking ranking;

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
		menuBar.setVisible(true);
		this.setJMenuBar(menuBar);
		
		results = new ResultsFrame();
		//results.setSize(800,800);
		
		this.setLayout(mgr);
		this.c = new GridBagConstraints();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(titleText);
		this.setSize(1000, 1000);
		this.setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		this.setVisible(true);
		pan = new JPanel();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		//c.anchor = GridBagConstraints.CENTER;
		//pan.setAlignmentX(RIGHT_ALIGNMENT);
		//pan.setAlignmentY(CENTER_ALIGNMENT);
		mgr.addLayoutComponent(pan,c);
		this.add(pan);

		// Config the contest window!
		this.contestFrame = new PresentationFrame(this);
		this.contestFrame.setSize(700, 700);
		this.contestFrame.setTitle("asgard");
		this.contestFrame.setVisible(false);
		// Add contestPanel to contest window
		this.contestPanel = new JPanel();
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
			unzip(zipFile);
			parseInput(zipFile.getInputStream(zipFile.getEntry("final.xml")));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Beim Laden des Archivs trat ein Fehler auf!");
			fileFrame.setVisible(true);
			return;
		}
		
		config.load(this);
		
		this.setTitle(titleText + config.getSpielname());
		
		for (Final_Step step : steps){
			step.createStepWidgets();
		}
		
		
		steps.get(0).showAllNames();

		configFrame = new ConfigsFrame(config);
		configFrame.main = this;
		configFrame.setSize(300,300);
		configFrame.setVisible(false);
		
		JPanel grp = new JPanel();
		
		doAll.setText(">>");
		doAll.addActionListener(this);
		doAll.setVisible(true);
		
		nextButton = new JButton();
		nextButton.setText(">");
		nextButton.addActionListener(this);
		nextButton.setVisible(true);
		nextButton.setLocation(400, 400);

		openReplay = new JCheckBox("Replays ausführen");
		openReplay.setVisible(true);
		openReplay.addActionListener(this);
		openReplay.setSelected((config == null ? true : config.isOpenReplay()));

		
		lastButton.setText("<");
		lastButton.addActionListener(this);
		lastButton.setVisible(true);
		
		undoAll.setText("<<");
		undoAll.addActionListener(this);
		undoAll.setVisible(true);
		
		
		grp.add(undoAll);
		grp.add(lastButton);
		grp.add(openReplay);
		grp.add(nextButton);
		grp.add(doAll);
		c.weightx = 0.25;
		c.weighty = 0.25;
		//c.fill = GridBagConstraints.NONE;
		c.gridy = 1;
		//c.gridx = 0;
		//grp.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		//grp.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		grp.setVisible(true);
		c.weighty = 0.1;
		mgr.addLayoutComponent(grp,c);
		this.add(grp);
		

		publish.setText("Veröffentlichen");
		publish.addActionListener(this);
		publish.setVisible(true);
		publish.setEnabled(false);
		c.gridy = 2;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.NONE;
		this.add(publish,c);
		
		for (Final_Step step : steps) {
			step.publishToContestPanel();
		}
		
		
	}
	
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
		//e.clearRect(0, 0, this.HEIGHT, this.WIDTH);
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
		
		if(e.getSource() == showRanking){
			if(!results.isVisible()){
				int resp = JOptionPane.showConfirmDialog(this,"Sind Sie sicher, dass Sie die Ergebnisse anzeigen lassen wollen, obwohl noch nicht alle Spiele gespielt wurden?");
				if(resp != JOptionPane.OK_OPTION){ return; };
			}
			results.setVisible(true);
			results.createRanking(ranking.getRanking());
			
		}
		if(e.getSource() == openReplay){
			config.setOpenReplay(openReplay.isSelected());
		}
		
		if(e.getSource() == publish){
			for (Final_Step step : steps) {
				step.publishToContestPanel();
			}
		}
		
		if(e.getSource() == startTestGame){
			if(config.introReplayPath == null){
				JOptionPane.showMessageDialog(this,"Bitte wählen die zuerst im Konfigurationsmenu ein entsprechendes Replay aus");
			}else{
				System.out.println("Running intro replay!");
				boolean t = config.isOpenReplay();
				config.setOpenReplay(true);
				runReplay(config.introReplayPath);
				config.setOpenReplay(t);
			}
		}
		
		Final_Step curr = steps.get(currentStep);
		if (e.getSource() == nextButton) {
			if (curr.isFinished() && currentStep < steps.size() - 1) {
				currentStep++;
			}
			curr.stepForward();
			//this.repaint();
		}else if (e.getSource() == lastButton) {
			if(curr.isInit() && currentStep > 0){
				currentStep--;
			}
			curr.stepBackward();
			//this.repaint();
		}else if (e.getSource() == doAll) {
			for (Final_Step step : steps) {
				step.doAllMatches();
				//step.repaint();
				currentStep = steps.size()-1;
			}
		}else if (e.getSource() == undoAll) {
			for (Final_Step step : steps) {
				step.undoAllMatches();
				//step.repaint();
				currentStep = 0;
			}
		}else if (e.getSource() == showPresentation) {
			contestFrame.setVisible(true);
			contestFrame.repaint();
			publish.setEnabled(true);
		}else if (e.getSource() == configItem) {
			configFrame.setVisible(true);
			configFrame.repaint();
		}
		
		//Graphics g = pan.getGraphics();
		//g.clearRect(0,0,2000,2000);
		
		for (Final_Step step : steps) {
		  step.repaint();
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
	public void runReplay(String path){
		if(config.isOpenReplay()){
			try {
				Process prcs;
				System.out.println("Executing Server:");
				prcs = Runtime.getRuntime().exec("java -jar "+config.getServerJarPath() + " --plugin "+config.getServerPluginPath()+ " --stepspeed "+config.getSpeed()+" -m -f -r "+path);
				InputStream cmd_output = prcs.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(cmd_output));
				String out = reader.readLine();
				while(out != null){
					System.out.println(out);
					out = reader.readLine();
				}		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}
	}
}
