package finals;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	FinalsConfiguration config;
	JButton save;
	JLabel speedLabel;
	JSlider speed;
	String name = "";

	public ConfigsFrame(FinalsConfiguration config) {
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setTitle("Konfiguration: Finale");
		GridLayout gl = new GridLayout();
		gl.setColumns(1);
		gl.setRows(4);
		this.setLayout(gl);

		this.config = config;
		
		contestNameLabel = new JLabel();
		contestNameLabel.setText("Spielname");
		contestNameLabel.setVisible(true);
		this.add(contestNameLabel);
		
		contestName = new JTextField(config.getSpielname());
		this.add(contestName);
		
		startCommandLabel = new JLabel();
		startCommandLabel.setText("Server Startbefehl");
		startCommandLabel.setVisible(true);
		this.add(startCommandLabel);

		startCommand = new JTextField(config.getServerStartupCommand());
		this.add(startCommand);
		
		
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
		save.setMargin(new Insets(5,5,5,5));
		this.add(save);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == save) {
			config.setSpeed(speed.getValue());
			config.setSpielname(contestName.getText());
			config.setServerStartupCommand(startCommand.getText());
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
