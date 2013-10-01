package sc.plugin2013.gui.renderer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class RenderConfigurationDialog extends JDialog {

	private final JCheckBox[] checkBoxes = new JCheckBox[RenderConfiguration.OPTIONS.length];

	public RenderConfigurationDialog(JComponent parent) {

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

		for (int i = 0; i < RenderConfiguration.OPTIONS.length; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setSelected(RenderConfiguration.OPTIONS[i]);
			checkBoxes[i] = checkBox;
			add(checkBox, gbc);
			gbc.gridy++;
		}

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;

		for (int i = 0; i < RenderConfiguration.OPTION_NAMES.length; i++) {
			add(new JLabel(RenderConfiguration.OPTION_NAMES[i]), gbc);
			gbc.gridy++;
		}

		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		JButton button = new JButton("Speichern");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (int i = 0; i < RenderConfiguration.OPTIONS.length; i++) {
					RenderConfiguration.OPTIONS[i] = checkBoxes[i].isSelected();
				}
				RenderConfiguration.saveSettings();
				dispose();
			}

		});
		add(button, gbc);

	}

}
