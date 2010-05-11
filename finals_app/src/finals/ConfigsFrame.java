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
	JLabel startCommandLabel;
	JTextField startCommand;
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

	public ConfigsFrame(FinalsConfiguration config) {
		Insets ins = new Insets(5,5,5,5);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setTitle("Konfiguration: Finale");
		GridLayout gl = new GridLayout();
		gl.setColumns(1);
		gl.setRows(4);
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
		
		startCommandLabel = new JLabel();
		startCommandLabel.setText("Server Startbefehl");
		startCommandLabel.setVisible(false);
		//this.add(startCommandLabel);
		
		startCommand = new JTextField(config.getServerStartupCommand());
		startCommand.setVisible(false);
		//this.add(startCommand);
		
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

		save = new JButton("Save Configuration");
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
			config.setServerStartupCommand(startCommand.getText());
			if(main != null){
				main.setTitle(main.titleText+config.getSpielname());
			}
			if(chooser.getSelectedFile() != null){
				config.introReplayPath = chooser.getSelectedFile().toString();
				System.out.println("Intro path: "+config.introReplayPath);
			}
			config.save(main);
			this.setVisible(false);
		}
		
		if (e.getSource() == openIntroSelectionButton) {
			chooser.showOpenDialog(this);
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
