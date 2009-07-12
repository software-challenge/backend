package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
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

import javax.swing.AbstractCellEditor;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import sc.gui.PresentationFacade;
import sc.gui.stuff.KIInformation;
import sc.gui.stuff.MyCombobox;
import sc.gui.stuff.YearComparator;
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

		plugins = getAvailablePlugins();
		Vector<String> pluginNames = getPluginNames(plugins);

		// ---------------------------------------------------

		this.setLayout(new BorderLayout());

		pnlTable = new JPanel();
		pnlBottom = new JPanel();
		pnlBottom.setName("Panel Bottom");// TODO test
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

		observer.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				contextPanel.updateButtonBar(false);
			}
		});

		observer.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded() {
				presFac.getLogicFacade().stopServer();
				contextPanel.updateButtonBar(true);
			}
		});

		// show connecting dialog
		String message = lang.getString("dialog_create_waiting");
		String title = "";
		if (JOptionPane.showInputDialog(message) == null) {
			observer.cancel();
		}

	}

	private int extractIndex(String plyType) {
		System.out.println("TEST " + plyType);
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

	void cancelGameCreation() {
		presFac.getLogicFacade().stopServer();
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

	private void addRows(final JTable table) {
		GUIPluginInstance selPlugin = getSelectedPlugin();
		MyTableModel model = (MyTableModel) table.getModel();

		for (int i = 0; i < selPlugin.getPlugin().getMinimalPlayerCount(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(new Integer(i + 1));
			rowData.add("Player " + (i + 1));
			rowData.add(null);// TODO std. player type
			rowData.add("");
			model.addRow(rowData);
		}
	}

	private class MyTableModel extends DefaultTableModel {

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

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

	public class MyComboBoxEditor extends DefaultCellEditor implements ActionListener {
		public MyComboBoxEditor(Vector<String> items) {
			super(new JComboBox(items));

			JComboBox cbox = (JComboBox) getComponent();
			cbox.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cbox = (JComboBox) getComponent();
			int row = tblPlayers.getSelectedRow();
			updatePlayerTable(cbox, row);
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
			if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				// set name
				playersModel.setValueAt(chooser.getSelectedFile().getName(), row, 1);
				// set path
				playersModel.setValueAt(chooser.getSelectedFile().getAbsolutePath(), row,
						3);
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
