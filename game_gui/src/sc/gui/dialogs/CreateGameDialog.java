package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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

import sc.common.CouldNotFindAnyPluginException;
import sc.gui.PresentationFacade;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.ISlot;
import sc.plugin.GUIPluginInstance;
import sc.plugin.GUIPluginManager;

@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	private final PresentationFacade presFac;
	/**
	 * The model of the used JTable
	 */
	private DefaultTableModel dataModel;
	private JTextField txfname;
	private JTextField txfPort;
	private JTextField txfFile;

	private final ResourceBundle lang;
	private JTable plugins;
	private DefaultTableModel pluginModel;

	public CreateGameDialog(JFrame frame) {
		super();

		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		createGUI(frame);
	}

	private void createGUI(final JFrame frame) {

		this.setLayout(new BorderLayout());

		JPanel left = new JPanel();
		left.setBorder(BorderFactory.createEtchedBorder());
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		left.setPreferredSize(new Dimension(300, 200));
		// left.setMinimumSize(new Dimension(300, 200));

		JPanel right = new JPanel();
		right.setBorder(BorderFactory.createEtchedBorder());
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));
		right.setPreferredSize(new Dimension(300, 200));
		// right.setMinimumSize(new Dimension(300, 200));

		JPanel center = new JPanel(new GridLayout(1, 2));
		center.add(left);
		center.add(right);
		// ------------------------------------------
		final JTable table = new JTable();
		// set single selection on one cell
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// don't let the user change the columns' order or width
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		// set model
		dataModel = new DefaultTableModel();
		dataModel.addColumn(lang.getString("dialog_create_tbl_name"));
		dataModel.addColumn(lang.getString("dialog_create_tbl_port"));
		dataModel.addColumn(lang.getString("dialog_create_tbl_filename"));
		table.setModel(dataModel);
		// set column width
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);
		table.getColumnModel().getColumn(2).setPreferredWidth(10);
		// ------------------------------------------
		plugins = new JTable();
		// set single selection on one cell
		plugins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// don't let the user change the columns' order or width
		plugins.getTableHeader().setReorderingAllowed(false);
		plugins.getTableHeader().setResizingAllowed(false);
		// set model
		pluginModel = new DefaultTableModel();
		pluginModel.addColumn(lang.getString("dialog_create_plugin_name"));
		loadPlugins(pluginModel);
		plugins.setModel(pluginModel);

		// Buttons
		JButton addButton = new JButton(lang.getString("dialog_create_add_client"));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(txfname.getText());
				rowData.add(txfPort.getText());
				rowData.add(txfFile.getText());
				dataModel.addRow(rowData);
			}
		});

		JButton addHumanButton = new JButton(lang.getString("dialog_create_add_human"));
		addHumanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<Object> rowData = new Vector<Object>();
				rowData.add(txfname.getText());
				rowData.add("0");
				rowData.add("human");
				dataModel.addRow(rowData);
			}
		});

		JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int dataRow = table.convertRowIndexToModel(row);
				dataModel.removeRow(dataRow);
			}
		});

		JButton choose = new JButton(lang.getString("dialog_create_choose"));
		choose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSize(2, 3);
				fileChooser.setMultiSelectionEnabled(true);
				fileChooser.setControlButtonsAreShown(false);
				// show
				if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					// add client's filename
					txfFile.setText(file.getAbsolutePath());
				}
			}
		});

		/* okButton */
		JButton okButton = new JButton(lang.getString("dialog_create_create"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGame();
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
		// ------------------------------------------
		txfname = new JTextField(20);
		JLabel lblname = new JLabel(lang.getString("dialog_create_lbl_name"));
		lblname.setLabelFor(txfname);

		JPanel line1 = new JPanel();
		line1.add(lblname);
		line1.add(txfname);
		// ------------------------------------------
		txfPort = new JTextField(5);
		JLabel lblPort = new JLabel(lang.getString("dialog_create_lbl_port"));
		lblPort.setLabelFor(txfPort);

		JPanel line2 = new JPanel();
		line2.add(lblPort);
		line2.add(txfPort);
		// ------------------------------------------
		txfFile = new JTextField(20);
		JLabel lblFile = new JLabel(lang.getString("dialog_create_lbl_file"));
		lblPort.setLabelFor(txfPort);

		JPanel line3 = new JPanel();
		line3.add(lblFile);
		line3.add(txfFile);
		line3.add(choose);
		// ------------------------------------------
		JPanel line4 = new JPanel();
		line4.add(addButton);
		line4.add(addHumanButton);
		line4.add(removeButton);
		// ------------------------------------------
		left.add(line1);
		left.add(line2);
		left.add(line3);
		left.add(line4);
		// ------------------------------------------
		right.add(new JScrollPane(table));
		right.add(new JScrollPane(plugins));
		// ------------------------------------------
		JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonBar.add(okButton);
		buttonBar.add(cancelButton);

		this.add(center, BorderLayout.CENTER);
		this.add(buttonBar, BorderLayout.PAGE_END);

		// set dialog preferences
		this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		this.setSize(800, 480);
	}

	private void loadPlugins(DefaultTableModel pluginModel) {
		System.out.println("Loading Plugins...");
		GUIPluginManager man = presFac.getLogicFacade().getPluginManager();
		// list all available plugins
		for (GUIPluginInstance plugin : man.getAvailablePlugins()) {
			Vector<String> rowData = new Vector<String>();
			rowData.add(plugin.getDescription().name());
			pluginModel.addRow(rowData);
			System.out.println("Loaded " + plugin.getDescription().name());
		}
	}

	protected void createGame() {
		// get port
		int port;
		try {
			port = Integer.parseInt(txfPort.getText());
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, lang
					.getString("dialog_create_error_port_msg"), lang
					.getString("dialog_create_error_port_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// get selected plugin
		int pluginRow = plugins.getSelectedRow();
		int dataRow = plugins.convertColumnIndexToModel(pluginRow);
		String selPluginName = (String) pluginModel.getValueAt(dataRow, 0);

		// get selected plugin
		GUIPluginManager man = presFac.getLogicFacade().getPluginManager();
		GUIPluginInstance selPlugin = null;
		for (GUIPluginInstance plugin : man.getAvailablePlugins()) {
			String pluginName = plugin.getDescription().name();
			if (pluginName.equals(selPluginName)) {
				selPlugin = plugin;
				break;
			}
		}
		
		// prepare game
		String replayFilename = String.valueOf(new Date().getTime());
		IGamePreparation prep = selPlugin.getPlugin().prepareGame(ip, port,
				replayFilename);

		for (int i = 0; i < prep.getSlots().size(); i++) {
			ISlot slot = prep.getSlots().get(i);
			//slot.
		}

		for (int row = 0; row < dataModel.getRowCount(); row++) {
			String name = (String) dataModel.getValueAt(row, 0);
			// String port_Str = (String) dataModel.getValueAt(row, 1);
			if (port > 0) { // an external client, not a human
				String clientPath = (String) dataModel.getValueAt(row, 2);

			} else {

			}

		}

		// close dialog
		this.dispose();
	}
}
