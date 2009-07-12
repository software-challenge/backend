package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
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
import javax.swing.table.TableCellRenderer;

import sc.gui.ContextDisplay;
import sc.gui.PresentationFacade;
import sc.gui.SCMenuBar;
import sc.gui.stuff.KIInformation;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.ILogicFacade;
import sc.plugin.GUIPluginInstance;

@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	private static final String HOST_IP = "localhost";

	private static final String DEFAULT_PORT = "10500";

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
	private MyTableModel playersModel;

	public CreateGameDialog(JFrame frame) {
		super();

		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		this.frame = frame;
		createGUI();
	}

	private void createGUI() {

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		Vector<String> pluginNames = presFac.getLogicFacade().getPluginNames(plugins);

		// ---------------------------------------------------

		this.setLayout(new BorderLayout());

		pnlTable = new JPanel();
		pnlBottom = new JPanel();
		pnlBottom.setBorder(BorderFactory.createEtchedBorder());
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
		txfPort = new JTextField(DEFAULT_PORT);
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

		Vector<String> cmbItems = new Vector<String>();
		cmbItems.add(lang.getString("dialog_create_plyType_human"));
		cmbItems.add(lang.getString("dialog_create_plyType_ki"));
		cmbItems.add(lang.getString("dialog_create_plyType_observer"));
		cmbItems.add(lang.getString("dialog_create_plyType_closed"));

		// add columns
		playersModel = new MyTableModel();
		playersModel.addColumn(lang.getString("dialog_create_tbl_pos"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_name"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_plytype"));
		playersModel.addColumn(lang.getString("dialog_create_tbl_filename"));

		tblPlayers = new JTable(playersModel);
		// add rows (default)
		addRows(tblPlayers);

		tblPlayers.getColumnModel().getColumn(2).setCellEditor(
				new MyComboBoxEditor(cmbItems));
		tblPlayers.getColumnModel().getColumn(2).setCellRenderer(
				new MyComboBoxRenderer(cmbItems));

		pnlTable.add(new JScrollPane(tblPlayers));

		// ---------------------------------------------------

		/* okButton */
		JButton okButton = new JButton(lang.getString("dialog_create_create"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGame(playersModel);
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
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	/**
	 * Creates a game with the selected options and players.
	 * 
	 * @param model
	 */
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

		final ContextDisplay contextPanel = (ContextDisplay) presFac.getContextDisplay();

		// start server
		presFac.getLogicFacade().startServer(port);

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(contextPanel.getGameField(),
				threeDimensional);

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
			cancelGameCreation();
			return;
		}

		List<KIInformation> KIs = new ArrayList<KIInformation>();

		// configure slots
		for (int i = 0; i < prep.getSlots().size(); i++) {
			ISlot slot = prep.getSlots().get(i);
			// set slot
			int index = extractIndex((String) model.getValueAt(i, 2));
			switch (index) {
			case 0:
				try {
					slot.asHuman();
				} catch (IOException e) {
					e.printStackTrace();
					cancelGameCreation();
					return;
				}
				break;
			case 1:
				String path = (String) model.getValueAt(i, 3);
				// check path
				if (path == null || path.equals("")) {
					JOptionPane.showMessageDialog(this, lang
							.getString("dialog_create_error_path_msg"), lang
							.getString("dialog_create_error_path_title"),
							JOptionPane.ERROR_MESSAGE);
					cancelGameCreation();
					return;
				}

				KIs.add(new KIInformation(slot.asClient(), path));
				break;
			case 2: // nothing to do
				break;
			case 3: // nothing to do
				break;
			default:
				cancelGameCreation();
				throw new RuntimeException("Selection range out of bounds (" + index
						+ ")");
			}
		}

		// set observation
		IObservation observer = prep.getObserver();
		presFac.getLogicFacade().setObservation(observer);

		final ConnectingDialog connDial = new ConnectingDialog();

		observer.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				contextPanel.updateButtonBar(false);
				connDial.dispose();
				CreateGameDialog.this.requestFocus();
			}
		});

		observer.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded() {
				presFac.getLogicFacade().stopServer();
				contextPanel.updateButtonBar(true);
			}
		});

		// start clients
		for (KIInformation kinfo : KIs) {
			StringBuilder params = new StringBuilder();
			for (String p : kinfo.getParameters()) {
				params.append(p);
			}
			try {
				Process process = Runtime.getRuntime().exec(kinfo.getPath() + " " + params);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, lang
						.getString("dialog_create_error_client_msg"), lang
						.getString("dialog_create_error_client_title"),
						JOptionPane.ERROR_MESSAGE);
				cancelGameCreation();
				return;
			}
		}

		// show connecting dialog
		/*
		 * if (connDial.showDialog() == JOptionPane.CANCEL_OPTION) {
		 * observer.cancel(); cancelGameCreation(); } else
		 */{
			// add game specific info item in menu bar
			((SCMenuBar) presFac.getMenuBar()).setGameSpecificInfo(selPlugin
					.getDescription().name(), selPlugin.getPlugin()
					.getCurrentStateImage(), selPlugin.getPlugin().getPluginInfoText(),
					selPlugin.getDescription().author());
			// close dialog
			this.dispose();
		}

	}

	private int extractIndex(String plyType) {
		if (plyType.equals(lang.getString("dialog_create_plyType_human"))) {
			return 0;
		} else if (plyType.equals(lang.getString("dialog_create_plyType_ki"))) {
			return 1;
		} else if (plyType.equals(lang.getString("dialog_create_plyType_observer"))) {
			return 2;
		} else if (plyType.equals(lang.getString("dialog_create_plyType_closed"))) {
			return 3;
		}
		return -1;
	}

	/**
	 * Closes the server.
	 */
	void cancelGameCreation() {
		presFac.getLogicFacade().stopServer();
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(combPlugins.getSelectedIndex());
	}

	private void addRows(final JTable table) {
		GUIPluginInstance selPlugin = getSelectedPlugin();
		MyTableModel model = (MyTableModel) table.getModel();

		for (int i = 0; i < selPlugin.getPlugin().getMinimalPlayerCount(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(new Integer(i + 1));
			rowData.add("Player " + (i + 1));
			rowData.add(lang.getString("dialog_create_plyType_human")); // default
			rowData.add("");
			model.addRow(rowData);
		}
	}

	private class MyTableModel extends DefaultTableModel {

		public boolean isCellEditable(int row, int col) {
			return (col > 0);
		}

	}

	public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
		public MyComboBoxRenderer(Vector<String> items) {
			super(items);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			// Select the current value
			setSelectedItem(value);
			return this;
		}
	}

	public class MyComboBoxEditor extends DefaultCellEditor implements ItemListener {
		public MyComboBoxEditor(Vector<String> items) {
			super(new JComboBox(items));

			JComboBox cbox = (JComboBox) getComponent();
			cbox.setEditable(false);
			cbox.addItemListener(this);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				JComboBox cbox = (JComboBox) getComponent();
				int row = tblPlayers.getSelectedRow();
				updatePlayerTable(cbox, row);
			}
		}
	}

	/**
	 * Updates the player table at the given <code>row</code> according to the
	 * selected index of the given combobox.
	 * 
	 * @param cbox
	 * @param row
	 */
	public void updatePlayerTable(JComboBox cbox, int row) {

		// String cell1 = (String) playersModel.getValueAt(row, 1);
		// String cell3 = (String) playersModel.getValueAt(row, 3);

		int index = cbox.getSelectedIndex();
		switch (index) {
		case 0:// human
			// set path
			playersModel.setValueAt("-", row, 3);
			break;
		case 1:// KI
			String currentDirectoryPath = "."; // TODO load config
			JFileChooser chooser = new JFileChooser(currentDirectoryPath);
			// chooser.setDialogTitle(dialogTitle); TODO
			switch (chooser.showOpenDialog(frame)) {
			case JFileChooser.APPROVE_OPTION:
				// set name
				playersModel.setValueAt(chooser.getSelectedFile().getName(), row, 1);
				// set path
				playersModel.setValueAt(chooser.getSelectedFile().getAbsolutePath(), row,
						3);
				break;
			case JFileChooser.CANCEL_OPTION:
				cbox.setSelectedIndex(0); // set back to default (here: index 0)
				break;
			}
			break;
		case 2:// observer
			// set path
			playersModel.setValueAt("-", row, 3);
			break;
		case 3: // closed
			// set path
			playersModel.setValueAt("-", row, 1);
			// set path
			playersModel.setValueAt("-", row, 3);
			break;
		default:
			// throw new RuntimeException("Selection range out of bounds (" +
			// index + ")");
		}
	}

}
