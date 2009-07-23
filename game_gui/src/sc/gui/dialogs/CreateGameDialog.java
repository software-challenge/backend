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
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
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
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import sc.common.HelperMethods;
import sc.common.UnsupportedFileExtensionException;
import sc.gui.ContextDisplay;
import sc.gui.PresentationFacade;
import sc.gui.SCMenuBar;
import sc.gui.StatusBar;
import sc.gui.stuff.KIInformation;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.GUIConfiguration;
import sc.plugin.GUIPluginInstance;
import sc.shared.GameResult;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	private static final String HOST_IP = "localhost";

	private static final int DEFAULT_PORT = SharedConfiguration.DEFAULT_PORT;

	private final PresentationFacade presFac;
	private final Properties lang;
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

		presFac = PresentationFacade.getInstance();
		lang = presFac.getLogicFacade().getLanguageData();
		this.frame = frame;
		createGUI();
	}

	/**
	 * Creates the dialog for creating a custom game.
	 */
	private void createGUI() {

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		Vector<String> pluginNames = presFac.getLogicFacade().getPluginNames(plugins);

		// ---------------------------------------------------

		setLayout(new BorderLayout());

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

		ckbDim = new JCheckBox(lang.getProperty("dialog_create_pref_dim"));
		ckbDebug = new JCheckBox(lang.getProperty("dialog_create_pref_debug"));
		ckbDebug.setToolTipText(lang.getProperty("dialog_create_pref_debug_hint"));
		txfPort = new JTextField(5);
		txfPort.setText("" + DEFAULT_PORT);
		txfPort.setEditable(false);
		lblPort = new JLabel(lang.getProperty("dialog_create_pref_port"));
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
		cmbItems.add(lang.getProperty("dialog_create_plyType_human"));
		cmbItems.add(lang.getProperty("dialog_create_plyType_ki_intern"));
		cmbItems.add(lang.getProperty("dialog_create_plyType_ki_extern"));
		// cmbItems.add(lang.getProperty("dialog_create_plyType_observer"));
		// cmbItems.add(lang.getProperty("dialog_create_plyType_closed"));

		// add columns
		playersModel = new MyTableModel();
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_pos"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_name"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_plytype"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_filename"));

		tblPlayers = new JTable(playersModel);
		tblPlayers.setRowHeight(25);
		// set single selection on one cell
		tblPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// don't let the user change the columns' order or width
		tblPlayers.getTableHeader().setReorderingAllowed(false);
		tblPlayers.getTableHeader().setResizingAllowed(false);
		// add rows (default)
		addRows(tblPlayers);

		tblPlayers.getColumnModel().getColumn(2).setCellEditor(
				new MyComboBoxEditor(cmbItems));
		tblPlayers.getColumnModel().getColumn(2).setCellRenderer(
				new MyComboBoxRenderer(cmbItems));

		pnlTable.add(new JScrollPane(tblPlayers));

		// ---------------------------------------------------

		/* okButton */
		final JButton okButton = new JButton(lang.getProperty("dialog_create_create"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGame(playersModel);
			}
		});

		/* cancelButton */
		JButton cancelButton = new JButton(lang.getProperty("dialog_create_cancel"));
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
		this.setTitle(lang.getProperty("dialog_create_title"));
		this.setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
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
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_create_error_port_msg"), lang
					.getProperty("dialog_create_error_port_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final ContextDisplay contextPanel = (ContextDisplay) presFac.getContextDisplay();

		// start server
		presFac.getLogicFacade().startServer(port);

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(contextPanel.recreateGameField(),
				threeDimensional);

		List<SlotDescriptor> descriptors = new ArrayList<SlotDescriptor>(model
				.getRowCount());
		for (int i = 0; i < model.getRowCount(); i++) {
			String playerName = (String) model.getValueAt(i, 1);
			int index = extractIndex((String) model.getValueAt(i, 2));
			descriptors.add(new SlotDescriptor(playerName, index != 0
					&& !ckbDebug.isSelected(), index != 0));
		}

		IGamePreparation prep;
		try {
			prep = selPlugin.getPlugin().prepareGame(ip, port,
					descriptors.toArray(new SlotDescriptor[descriptors.size()]));
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_create_error_network_msg"), lang
					.getProperty("dialog_create_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			cancelGameCreation(null);
			return;
		}

		// set observation
		final IObservation observer = prep.getObserver();
		presFac.getLogicFacade().setObservation(observer);

		final ConnectingDialog connDial = new ConnectingDialog();

		observer.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				System.out.println(">>> ready");
				connDial.close();
				contextPanel.updateButtonBar(false);
			}
		});

		observer.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded(GameResult result) {
				// presFac.getLogicFacade().stopServer();//TODO wieder rein?
				contextPanel.updateButtonBar(true);
				try {
					observer.saveReplayToFile("./replays/replay-"
							+ System.currentTimeMillis() + ".xml");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(CreateGameDialog.this, lang
							.getProperty("dialog_create_error_replay_msg"), lang
							.getProperty("dialog_create_error_replay_title"),
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		observer.addNewTurnListener(contextPanel);

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
					cancelGameCreation(observer);
					return;
				}
				break;
			case 1: // KI intern
				String path = (String) model.getValueAt(i, 3);
				// check path
				if (path == null || path.equals("")) {
					JOptionPane.showMessageDialog(this, lang
							.getProperty("dialog_create_error_path_msg"), lang
							.getProperty("dialog_create_error_path_title"),
							JOptionPane.ERROR_MESSAGE);
					cancelGameCreation(observer);
					return;
				}
				System.out.println("PATH >>> " + path);
				KIs.add(new KIInformation(slot.asClient(), path));
				break;
			case 2: // KI extern
				slot.asRemote();
				break;
			default:
				cancelGameCreation(observer);
				throw new RuntimeException("Selection range out of bounds (" + index
						+ ")");
			}
		}

		// start KI (intern) clients
		for (KIInformation kinfo : KIs) {
			String filename = kinfo.getPath();
			String[] params = kinfo.getParameters();
			try {
				HelperMethods.exec(filename, params);
			} catch (IOException e) {
				e.printStackTrace();
				cancelGameCreation(observer);
				JOptionPane.showMessageDialog(this, lang
						.getProperty("dialog_create_error_client_msg"), lang
						.getProperty("dialog_create_error_client_title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (UnsupportedFileExtensionException e) {
				e.printStackTrace();
				cancelGameCreation(observer);
				JOptionPane.showMessageDialog(this, lang
						.getProperty("dialog_error_fileext_msg"), lang
						.getProperty("dialog_error_fileext_title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// show connecting dialog
		if (connDial.showDialog() == JOptionPane.CANCEL_OPTION) {
			observer.cancel();
			cancelGameCreation(observer);
		} else {
			// add game specific info item in menu bar
			((SCMenuBar) presFac.getMenuBar()).setGameSpecificInfo(selPlugin
					.getDescription().name(), selPlugin.getDescription().version(), null,
					selPlugin.getPlugin().getPluginInfoText(), selPlugin.getDescription()
							.author());
			// update status bar
			((StatusBar) presFac.getStatusBar()).setStatus(lang
					.getProperty("statusbar_status_currentgame")
					+ " " + selPlugin.getDescription().name());
			// close dialog
			dispose();
		}

	}

	/**
	 * Returns an identifier for the player type: human, internal KI or external
	 * KI.
	 * 
	 * @param plyType
	 * @return
	 */
	private int extractIndex(final String plyType) {
		if (plyType.equals(lang.getProperty("dialog_create_plyType_human"))) {
			return 0;
		} else if (plyType.equals(lang.getProperty("dialog_create_plyType_ki_intern"))) {
			return 1;
		} else if (plyType.equals(lang.getProperty("dialog_create_plyType_ki_extern"))) {
			return 2;
		}

		return -1;
	}

	/**
	 * Closes the server.
	 */
	private void cancelGameCreation(IObservation observer) {
		if (null != observer) {
			observer.cancel();
		}
		presFac.getLogicFacade().stopServer();
		// clear panel
		/*
		 * ((ContextDisplay) presFac.getContextDisplay()).getGameField()
		 * .removeAll();
		 */// TODO does it work?
		((ContextDisplay) presFac.getContextDisplay()).recreateGameField();
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(combPlugins.getSelectedIndex());
	}

	/**
	 * Adds standard rows after selecting a game type.
	 * 
	 * @param table
	 */
	private void addRows(final JTable table) {
		GUIPluginInstance selPlugin = getSelectedPlugin();
		MyTableModel model = (MyTableModel) table.getModel();

		for (int i = 0; i < selPlugin.getPlugin().getMinimalPlayerCount(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(new Integer(i + 1));
			rowData.add("Player " + (i + 1));
			rowData.add(lang.getProperty("dialog_create_plyType_human")); // default
			rowData.add("-");
			model.addRow(rowData);
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

		int index = cbox.getSelectedIndex();
		switch (index) {
		case 0:// human
			// set path
			playersModel.setValueAt("-", row, 3);
			break;
		case 1:// KI intern
			JFileChooser chooser = new JFileChooser(GUIConfiguration.instance()
					.getCreateGameDialogPath());
			chooser.setDialogTitle(lang.getProperty("dialog_create_dialog_title"));

			switch (chooser.showOpenDialog(frame)) {
			case JFileChooser.APPROVE_OPTION:
				// set name
				String clientname = chooser.getSelectedFile().getName();
				clientname = HelperMethods.getFilenameWithoutFileExt(clientname) + " "
						+ (row + 1);
				playersModel.setValueAt(clientname, row, 1);
				// set path
				playersModel.setValueAt(chooser.getSelectedFile().getAbsolutePath(), row,
						3);
				// save config
				GUIConfiguration.instance().setCreateGameDialogPath(
						chooser.getSelectedFile().getParent());
				break;
			case JFileChooser.CANCEL_OPTION:
				cbox.setSelectedIndex(0); // set back to default (here: human)
				break;
			}
			break;
		case 2: // KI extern
			playersModel.setValueAt("-", row, 3);
			break;
		default:
			// throw new RuntimeException("Selection range out of bounds (" +
			// index + ")");
		}
	}

	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------
	// ------------------------------------------------------------------------

	private class MyTableModel extends DefaultTableModel {

		@Override
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
			JComboBox cbox = (JComboBox) getComponent();
			/**
			 * Only react on opening (i.e. having the focus), not additionally
			 * on closing the roll menu
			 */
			if (e.getStateChange() == ItemEvent.SELECTED && cbox.hasFocus()) {
				int row = tblPlayers.getSelectedRow();
				updatePlayerTable(cbox, row);
			}
		}
	}
}
