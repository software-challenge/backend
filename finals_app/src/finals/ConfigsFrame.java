package finals;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigsFrame extends JFrame implements ActionListener,
		ChangeListener {
	JLabel contestNameLabel;
	JTextField contestName;
	JCheckBox isServerMaximized;
	FinalsConfiguration config;
	JButton save;
	JLabel speedLabel;
	JSlider speed;
	String name = "";
	MainFrame main;
	JLabel introLabel;
	JButton openIntroSelectionButton;
	JFileChooser chooser;
	JLabel serverJarPathLabel;
	JFileChooser serverJarPath;
	JButton serverJarPathButton;

	public ConfigsFrame(FinalsConfiguration config) {
		Insets ins = new Insets(5,5,5,5);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setTitle("Konfiguration: Finale");
		GridLayout gl = new GridLayout();
		gl.setColumns(1);
		gl.setRows(8);
		gl.setVgap(10);
		gl.setHgap(10);
		this.setLayout(gl);

		this.config = config;
		

		chooser = new JFileChooser();
		
		contestNameLabel = new JLabel();
		contestNameLabel.setText("Spielname");
		contestNameLabel.setVisible(true);
		this.add(contestNameLabel);
		
		contestName = new JTextField(config.getSpielname());
		this.add(contestName);
		
		serverJarPathLabel = new JLabel("Serverpfad");
		serverJarPathLabel.setVisible(true);
		this.add(serverJarPathLabel);
		
		serverJarPathButton = new JButton("Durchsuchen");
		serverJarPathButton.setVisible(true);
		serverJarPathButton.addActionListener(this);
		this.add(serverJarPathButton);
		
		serverJarPath = new JFileChooser();

		introLabel = new JLabel("Replay des Einführungsspiels:");
		introLabel.setVisible(true);
		this.add(introLabel);
		
		openIntroSelectionButton = new JButton("Durchsuchen");
		openIntroSelectionButton.setVisible(true);
		openIntroSelectionButton.addActionListener(this);
		this.add(openIntroSelectionButton);
		
		speedLabel = new JLabel();
		speedLabel.setText("Wiedergabegeschwindigkeit");
		speedLabel.setVisible(true);
		this.add(speedLabel);
		
		
		speed = new JSlider();
		speed.setValue(config.getSpeed());
		speed.addChangeListener(this);
		speed.setToolTipText("Wiedergabegeschwindigkeit: " + config.getSpeed()
				+ "%");
		this.add(speed);

		save = new JButton("Speichern");
		save.addActionListener(this);
		save.setAlignmentX(RIGHT_ALIGNMENT);
		save.setMargin(ins);
		this.add(save);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {
			config.setSpeed(speed.getValue());
			config.setSpielname(contestName.getText());
			if(main != null){
				main.setTitle(main.titleText+config.getSpielname());
			}
			if(chooser.getSelectedFile() != null){
				config.introReplayPath = chooser.getSelectedFile().toString();
				System.out.println("Intro path: "+config.introReplayPath);
			}
			
			if(serverJarPath.getSelectedFile() != null){
				config.setServerJarPath(serverJarPath.getSelectedFile().toString());
				config.setServerPluginPath(serverJarPath.getSelectedFile().getParent()+"/plugins/");
				System.out.println("Server Plugin-Path: "+serverJarPath.getSelectedFile().getParent()+"/plugins/");
				System.out.println("Server Jar: "+config.getServerJarPath());
			}
			config.save(main);
			this.setVisible(false);
		}
		
		if (e.getSource() == openIntroSelectionButton) {
			chooser.showOpenDialog(this);
		}
		
		if (e.getSource() == serverJarPathButton){
			serverJarPath.showOpenDialog(this);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == speed){
			speed.setToolTipText("Wiedergabegeschwindigkeit: " + speed.getValue()
					+ "%");
		}
	}
}
