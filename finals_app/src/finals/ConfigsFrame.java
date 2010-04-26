package finals;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigsFrame extends JFrame implements ActionListener,
		ChangeListener {
	JTextArea contestName;
	JTextArea startCommand;
	FinalsConfiguration config;
	JButton save;
	JSlider speed;
	String name = "";

	public ConfigsFrame(FinalsConfiguration config) {
		this.setSize(100, 100);
		GridLayout gl = new GridLayout();
		gl.setColumns(1);
		gl.setRows(4);
		this.setLayout(gl);

		this.config = config;

		contestName = new JTextArea(config.getSpielname());
		this.add(contestName);

		speed = new JSlider();
		speed.setValue(config.getSpeed());
		speed.setToolTipText("Wiedergabegeschwindigkeit: " + config.getSpeed()
				+ "%");
		this.add(speed);

		save = new JButton("Save Configuration");
		save.addActionListener(this);
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

	}
}
