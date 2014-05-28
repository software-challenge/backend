package sc.plugin2015.gui.renderer;

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
	
	private JComboBox rendererCombo;
	//private final JCheckBox[] checkBoxes = new JCheckBox[RenderConfiguration.OPTIONS.length];

<<<<<<< HEAD
=======
	private JComboBox rendererCombo;
	private JComboBox antiAliasingCombo;
	private JCheckBox animationCheckBox;
	private JCheckBox debugCheckBox;

>>>>>>> 81356a41e1bc81868cd77ae0e7c2dad510917d73
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
<<<<<<< HEAD
		
		rendererCombo = new JComboBox(RenderConfiguration.RendererStrings);
		rendererCombo.setSelectedItem(RenderConfiguration.optionRenderer);
		
		add(rendererCombo, gbc);
		gbc.gridx = 1;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[0]), gbc);
		
		/*
		for (int i = 0; i < RenderConfiguration.OPTIONS.length; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setSelected(RenderConfiguration.OPTIONS[i]);
			checkBoxes[i] = checkBox;
			add(checkBox, gbc);
			gbc.gridy++;
		}
=======

		rendererCombo = new JComboBox(RenderConfiguration.RendererStrings);
		rendererCombo.setSelectedItem(RenderConfiguration.optionRenderer);

		antiAliasingCombo = new JComboBox(RenderConfiguration.AntialiasingModes);
		antiAliasingCombo
				.setSelectedItem(RenderConfiguration.optionAntiAliasing);

		animationCheckBox = new JCheckBox();
		animationCheckBox.setSelected(RenderConfiguration.optionAnimation);

		debugCheckBox = new JCheckBox();
		debugCheckBox.setSelected(RenderConfiguration.optionDebug);
		add(new JLabel(RenderConfiguration.OPTION_NAMES[0]), gbc);
>>>>>>> 81356a41e1bc81868cd77ae0e7c2dad510917d73

		gbc.gridx = 1;
		add(rendererCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[1]), gbc);

		gbc.gridx = 1;
		add(antiAliasingCombo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[2]), gbc);

		gbc.gridx = 1;
		add(animationCheckBox, gbc);

<<<<<<< HEAD
		for (int i = 0; i < RenderConfiguration.OPTION_NAMES.length; i++) {
			add(new JLabel(RenderConfiguration.OPTION_NAMES[i]), gbc);
			gbc.gridy++;
		}
		*/
=======
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel(RenderConfiguration.OPTION_NAMES[3]), gbc);

		gbc.gridx = 1;
		add(debugCheckBox, gbc);
>>>>>>> 81356a41e1bc81868cd77ae0e7c2dad510917d73
		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		JButton button = new JButton("Speichern");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
<<<<<<< HEAD

				/*for (int i = 0; i < RenderConfiguration.OPTIONS.length; i++) {
					RenderConfiguration.OPTIONS[i] = checkBoxes[i].isSelected();
				}*/
				RenderConfiguration.optionRenderer = (String) rendererCombo.getSelectedItem();
=======
				RenderConfiguration.optionRenderer = (String) rendererCombo
						.getSelectedItem();
				RenderConfiguration.optionAntiAliasing = (Integer) antiAliasingCombo
						.getSelectedItem();
				RenderConfiguration.optionAnimation = animationCheckBox
						.isSelected();
				RenderConfiguration.optionDebug = debugCheckBox.isSelected();
>>>>>>> 81356a41e1bc81868cd77ae0e7c2dad510917d73
				RenderConfiguration.saveSettings();
				dispose();
			}

		});
		add(button, gbc);

	}

}
