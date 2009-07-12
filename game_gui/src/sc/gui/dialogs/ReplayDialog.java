package sc.gui.dialogs;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
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

import sc.gui.ContextDisplay;
import sc.gui.PresentationFacade;
import sc.guiplugin.interfaces.IObservation;
import sc.plugin.GUIPluginInstance;

@SuppressWarnings("serial")
public class ReplayDialog extends JDialog {

	private static final Integer REPLAY_PORT = 10500;
	private ResourceBundle lang;
	private PresentationFacade presFac;
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
		JLabel lblReplay = new JLabel(lang.getString("dialog_replay_lbl_file"));
		lblReplay.setLabelFor(txfReplay);
		JButton btnReplay = new JButton(lang.getString("dialog_replay_btn_file"));
		btnReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(presFac.getFrame()) == JFileChooser.APPROVE_OPTION) {
					loadReplay(chooser.getSelectedFile());
				}
			}
		});
		pnlReplay.add(lblReplay, FlowLayout.LEFT);
		pnlReplay.add(txfReplay, FlowLayout.LEFT);
		pnlReplay.add(btnReplay, FlowLayout.RIGHT);

		// ---------------------------------------------

		JButton btnStart = new JButton(lang.getString("dialog_replay_btn_start"));
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReplay();
			}
		});
		pnlButtons.add(btnStart);
		JButton btnCancel = new JButton(lang.getString("dialog_replay_btn_cancel"));
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
		this.setResizable(false);
		this.setTitle(lang.getString("dialog_replay_title"));
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	protected void loadReplay(File f) {
		txfReplay.setText(f.getAbsolutePath());
	}

	private void startReplay() {
		GUIPluginInstance selPlugin = getSelectedPlugin();

		String filename = txfReplay.getText();
		File f = new File(filename);
		if (!f.exists()) {
			JOptionPane.showMessageDialog(this,
					lang.getString("dialog_replay_error_msg"), lang
							.getString("dialog_replay_error_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// start server
		presFac.getLogicFacade().startServer(REPLAY_PORT);

		JPanel gameField = ((ContextDisplay) presFac.getContextDisplay()).getGameField();

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(gameField, threeDimensional);

		// load replay and set observation
		try {
			IObservation observation = selPlugin.getPlugin().loadReplay(filename);
			presFac.getLogicFacade().setObservation(observation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(cmbGameType.getSelectedIndex());
	}
}
