package sc.plugin2016.gui.renderer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

import processing.core.PApplet;

@SuppressWarnings("serial")
public class RenderConfigurationDialog extends JDialog {

	//private JComboBox rendererCombo;
	private JComboBox antiAliasingCombo;
	private JCheckBox debugCheckBox;

	public RenderConfigurationDialog(PApplet parent) {

		super();
		setTitle("Konfiguration");
		createUI();
		validate();
		pack();

		setLocationRelativeTo(parent);

		setModal(true);
		setVisible(true);

	}

	private void createUI() {

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);

		gbc.gridx = 0;
		gbc.gridy = 0;
/*
		rendererCombo = new JComboBox(RenderConfiguration.RendererStrings);
		rendererCombo.setSelectedItem(RenderConfiguration.optionRenderer);
*/
		antiAliasingCombo = new JComboBox(RenderConfiguration.AntialiasingModes);
		antiAliasingCombo
				.setSelectedItem(RenderConfiguration.optionAntiAliasing);

		debugCheckBox = new JCheckBox();
		debugCheckBox.setSelected(RenderConfiguration.optionDebug);
		//add(new JLabel(RenderConfiguration.OPTION_NAMES[0]), gbc);

		gbc.gridx = 1;
		//add(rendererCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[1]), gbc);

		gbc.gridx = 1;
		add(antiAliasingCombo, gbc);


		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[2]), gbc);

		gbc.gridx = 1;
		add(debugCheckBox, gbc);
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		JButton button = new JButton("Speichern");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				/*
				RenderConfiguration.optionRenderer = (String) rendererCombo
						.getSelectedItem();
						*/
				RenderConfiguration.optionAntiAliasing = (Integer) antiAliasingCombo
						.getSelectedItem();
				RenderConfiguration.optionDebug = debugCheckBox.isSelected();
				RenderConfiguration.saveSettings();
				dispose();
			}

		});
		add(button, gbc);

	}

}
