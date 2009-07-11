package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import sc.gui.PresentationFacade;
import sc.gui.stuff.KIInformation;
import sc.gui.stuff.YearComparator;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.ILogicFacade;
import sc.plugin.GUIPluginInstance;
import sc.server.Application;
import sc.server.Lobby;

@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	private static final String HOST_IP = null;
	private final PresentationFacade presFac;
	private final ResourceBundle lang;
	private JPanel pnlTable;
	private JPanel pnlButtons;
	private JTable tblPlayers;
	private JPanel pnlBottom;
	private JPanel pnlLeft;
	private JPanel pnlRight;
	private JComboBox combPlugins;
	private List<GUIPluginInstance> plugins;
	private JPanel pnlPref;
	private JCheckBox ckbDim;
	private JCheckBox ckbDebug;
	private JFrame frame;
	private JTextField txfPort;
	private JLabel lblPort;

	public CreateGameDialog(JFrame frame) {
		super();

		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		this.frame = frame;
		createGUI();
	}

	private void createGUI() {

		plugins = getAvailablePlugins();
		Vector<String> pluginNames = getPluginNames(plugins);

		// ---------------------------------------------------

		this.setLayout(new BorderLayout());

		pnlTable = new JPanel();
		pnlBottom = new JPanel();
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.PAGE_AXIS));

		// ---------------------------------------------------

		pnlPref = new JPanel();
		pnlButtons = new JPanel();
		pnlBottom.add(pnlPref);
		pnlBottom.add(pnlButtons);

		// ---------------------------------------------------

		ckbDim = new JCheckBox(lang.getString("dialog_create_pref_dim"));
		ckbDebug = new JCheckBox(lang.getString("dialog_create_pref_debug"));
		ckbDebug.setToolTipText(lang.getString("dialog_create_pref_debug_hint"));
		txfPort = new JTextField(5);
		lblPort = new JLabel();
		lblPort.setLabelFor(txfPort);
		// pnlPref.add(ckbDim); TODO for future
		pnlPref.add(ckbDebug);
		pnlPref.add(lblPort);
		pnlPref.add(txfPort);

		// ---------------------------------------------------

		pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlButtons.add(pnlLeft);
		pnlButtons.add(pnlRight);

		// ---------------------------------------------------

		combPlugins = new JComboBox(pluginNames);

		pnlLeft.add(combPlugins);

		// ---------------------------------------------------

		// add columns
		final DefaultTableModel playersModel = new DefaultTableModel();
		playersModel.addColumn(lang.getString("dialog_create_tbl_pos"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_name"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_plytype"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_filename"));
		// add rows (default)
		addRows(playersModel);
		tblPlayers = new JTable(playersModel);

		pnlTable.add(new JScrollPane(tblPlayers));

		// ---------------------------------------------------

		/* okButton */
		JButton okButton = new JButton(lang.getString("dialog_create_create"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGame(playersModel);
				// close dialog
				CreateGameDialog.this.dispose();
			}
		});

		/* cancelButton */
		JButton cancelButton = new JButton(lang.getString("dialog_create_cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// close dialog
				CreateGameDialog.this.dispose();
			}
		});

		pnlRight.add(okButton);
		pnlRight.add(cancelButton);

		// ---------------------------------------------------

		// add components
		this.add(pnlTable, BorderLayout.CENTER);
		this.add(pnlBottom, BorderLayout.PAGE_END);
		// set dialog preferences
		// this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setSize(800, 480);
	}

	protected void createGame(final DefaultTableModel model) {
		GUIPluginInstance selPlugin = getSelectedPlugin();

		// get host
		String ip = HOST_IP;
		Integer port;
		try {
			port = new Integer(txfPort.getText());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, lang
					.getString("dialog_create_error_port_msg"), lang
					.getString("dialog_create_error_port_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int playerCount = model.getRowCount();

		// prepare game
		String replayFilename = String.valueOf(new Date().getTime())
				+ ILogicFacade.EXT_REPLAY;
		IGamePreparation prep;
		try {
			prep = selPlugin.getPlugin().prepareGame(ip, port, playerCount,
					replayFilename);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, lang
					.getString("dialog_create_error_network_msg"), lang
					.getString("dialog_create_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<KIInformation> KIs = new ArrayList<KIInformation>();

		// configure slots
		for (int i = 0; i < prep.getSlots().size(); i++) {
			ISlot slot = prep.getSlots().get(i);
			JComboBox cmbPlyType = (JComboBox) model.getValueAt(i, 2);
			String path = (String) model.getValueAt(i, 3);
			int index = cmbPlyType.getSelectedIndex();
			switch (index) {
			case 0:
				slot.asHuman();
				break;
			case 1:
				KIs.add(new KIInformation(slot.asClient(), path));
				break;
			case 2: // nothing to do
			case 3: // nothing to do
			default:
				throw new RuntimeException("Selection range out of bounds (" + index
						+ ")");
			}
		}

		final ContextDisplay contextPanel = (ContextDisplay) presFac.getContextDisplay();

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(contextPanel.getGameField(),
				threeDimensional);

		// set observation
		IObservation observer = prep.getObserver();
		presFac.getLogicFacade().setObservation(observer);

		observer.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				contextPanel.updateButtonBar();
			}
		});
		
		// start server
		presFac.getLogicFacade().startServer(port);

		// show connecting dialog
		String message = lang.getString("dialog_create_waiting");
		String title = "";
		if (JOptionPane.showInputDialog(message) == null) {
			observer.cancel();
		}

	}

	/**
	 * Returns available plugins in sorted order.
	 * 
	 * @return plugins
	 */
	private List<GUIPluginInstance> getAvailablePlugins() {
		Collection<GUIPluginInstance> plugins = presFac.getLogicFacade()
				.getPluginManager().getAvailablePlugins();
		// sort by plugin's year
		List<GUIPluginInstance> sortedPlugins = new LinkedList<GUIPluginInstance>(plugins);
		Collections.sort(sortedPlugins, new YearComparator());
		return sortedPlugins;
	}

	private Vector<String> getPluginNames(List<GUIPluginInstance> plugins) {
		Vector<String> result = new Vector<String>();
		int last = 0;
		for (int i = 0; i < plugins.size(); i++) {
			GUIPluginInstance pluginInstance = plugins.get(i);
			if (pluginInstance.getPlugin().getPluginYear() > last) {
				result.add(pluginInstance.getDescription().name());
			}
		}

		return result;
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(combPlugins.getSelectedIndex());
	}

	private void addRows(final DefaultTableModel playersModel) {
		GUIPluginInstance selPlugin = getSelectedPlugin();

		for (int i = 0; i < selPlugin.getPlugin().getMinimalPlayerCount(); i++) {
			Vector<String> cmbItems = new Vector<String>();
			cmbItems.add(lang.getString("dialog_create_plyType_human"));
			cmbItems.add(lang.getString("dialog_create_plyType_ki"));
			cmbItems.add(lang.getString("dialog_create_plyType_observer"));
			cmbItems.add(lang.getString("dialog_create_plyType_closed"));
			final JComboBox cmbPlyTypes = new JComboBox(cmbItems);
			cmbPlyTypes.addActionListener(new PlayerTypeActionListener(playersModel, i));

			Vector<Object> rowData = new Vector<Object>();
			rowData.add(new Integer(i + 1));
			rowData.add("Player " + i + 1);
			rowData.add(cmbPlyTypes);
			rowData.add("");
			playersModel.addRow(rowData);
		}
	}

	/**
	 * Checkbox action listener for selecting the player type for each slot.
	 * 
	 * @author chw
	 * 
	 */
	private class PlayerTypeActionListener implements ActionListener {

		private DefaultTableModel playersModel;
		private int rowIndex;

		public PlayerTypeActionListener(DefaultTableModel model, int index) {
			this.playersModel = model;
			this.rowIndex = index;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cmbPlyTypes = (JComboBox) e.getSource();
			int index = cmbPlyTypes.getSelectedIndex();
			switch (index) {
			case 0:// human
				// set path
				playersModel.setValueAt("-", rowIndex, 3);
				break;
			case 1:// KI
				String currentDirectoryPath = "."; // TODO load config
				JFileChooser chooser = new JFileChooser(currentDirectoryPath);
				// chooser.setDialogTitle(dialogTitle); TODO
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					// set name
					playersModel.setValueAt(chooser.getSelectedFile().getName(),
							rowIndex, 1);
					// set path
					playersModel.setValueAt(chooser.getSelectedFile().getAbsolutePath(),
							rowIndex, 3);
				}
				break;
			case 2:// observer
				// set path
				playersModel.setValueAt("-", rowIndex, 3);
				break;
			case 3: // closed
				// set path
				playersModel.setValueAt("-", rowIndex, 1);
				// set path
				playersModel.setValueAt("-", rowIndex, 3);
				break;
			default:
				throw new RuntimeException("Selection range out of bounds (" + index
						+ ")");
			}

		}
	}
}
