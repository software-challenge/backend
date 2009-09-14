package sc.gui.dialogs;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import sc.common.HelperMethods;
import sc.gui.ContextDisplay;
import sc.gui.PresentationFacade;
import sc.gui.SCMenuBar;
import sc.gui.StatusBar;
import sc.guiplugin.interfaces.IObservation;
import sc.logic.save.GUIConfiguration;
import sc.plugin.GUIPluginInstance;

@SuppressWarnings("serial")
public class ReplayDialog extends JDialog {

	private final Properties lang;
	private final PresentationFacade presFac;
	private List<GUIPluginInstance> plugins;
	private JComboBox cmbGameType;
	private JTextField txfReplay;

	public ReplayDialog() {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		createGUI();
	}

	private void createGUI() {

		this.setLayout(new GridLayout(0, 1));

		JPanel pnlGameType = new JPanel();
		pnlGameType.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		JPanel pnlReplay = new JPanel();
		pnlReplay.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		JPanel pnlButtons = new JPanel();

		// ---------------------------------------------

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		Vector<String> items = presFac.getLogicFacade().getPluginNames(plugins);
		cmbGameType = new JComboBox(items);
		pnlGameType.add(cmbGameType);

		txfReplay = new JTextField(20);
		JLabel lblReplay = new JLabel(lang.getProperty("dialog_replay_lbl_file"));
		lblReplay.setLabelFor(txfReplay);
		JButton btnReplay = new JButton(lang.getProperty("dialog_replay_btn_file"));
		btnReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(GUIConfiguration.instance()
						.getLoadReplayPath());
				chooser.setDialogTitle(lang.getProperty("dialog_replay_dialog_title"));
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					// set new path
					txfReplay.setText(f.getAbsolutePath());
					// save path
					GUIConfiguration.instance().setLoadReplayPath(f.getParent());
				}
			}
		});
		pnlReplay.add(lblReplay, FlowLayout.LEFT);
		pnlReplay.add(txfReplay, FlowLayout.LEFT);
		pnlReplay.add(btnReplay, FlowLayout.RIGHT);

		// ---------------------------------------------

		JButton btnStart = new JButton(lang.getProperty("dialog_replay_btn_start"));
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReplay();
			}
		});
		pnlButtons.add(btnStart);
		JButton btnCancel = new JButton(lang.getProperty("dialog_replay_btn_cancel"));
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ReplayDialog.this.dispose();
			}
		});
		pnlButtons.add(btnCancel);

		// ---------------------------------------------

		this.add(pnlGameType);
		this.add(pnlReplay);
		this.add(pnlButtons);

		// set pref
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setResizable(false);
		this.setTitle(lang.getProperty("dialog_replay_title"));
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Starts the selected replay file, closes this dialog and displays the
	 * replay.
	 */
	private void startReplay() {
		GUIPluginInstance selPlugin = getSelectedPlugin();

		String filename = txfReplay.getText();
		File f = new File(filename);
		if (!f.exists()) {
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_replay_error_msg"), lang
					.getProperty("dialog_replay_error_title"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		final ContextDisplay contextPanel = ((ContextDisplay) presFac.getContextDisplay());

		if (presFac.getLogicFacade().isGameActive())
			contextPanel.cancelCurrentGame();

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(contextPanel.recreateGameField(),
				threeDimensional);

		// load replay and set observation
		try {
			IObservation observation = selPlugin.getPlugin().loadReplay(filename);
			observation.addNewTurnListener(contextPanel);
			presFac.getLogicFacade().setObservation(observation);
			contextPanel.updateButtonBar(false);

			// add game specific info item in menu bar
			((SCMenuBar) presFac.getMenuBar()).setGameSpecificInfo(selPlugin
					.getDescription().name(), selPlugin.getVersion(), selPlugin
					.getPlugin().getPluginImage(), selPlugin.getPlugin().getPluginIcon(),
					selPlugin.getPlugin().getPluginInfoText(), selPlugin.getDescription()
							.author(), selPlugin.getPlugin().getPluginYear());
			// update status bar
			((StatusBar) presFac.getStatusBar()).setStatus(lang
					.getProperty("statusbar_status_currentreplay")
					+ " " + HelperMethods.getFilenameWithoutFileExt(f.getName()));

			this.dispose();
		} catch (IOException e) {
			// path check is done before, i.e. this exception should not happen
			e.printStackTrace();
			return;
		}
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(cmbGameType.getSelectedIndex());
	}
}
