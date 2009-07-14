package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import sc.common.HelperMethods;
import sc.common.UnsupportedFileExtensionException;
import sc.gui.PresentationFacade;
import sc.gui.stuff.KIInformation;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.plugin.GUIPluginInstance;
import sc.shared.GameResult;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;

@SuppressWarnings("serial")
public class TestRangeDialog extends JDialog {

	private static final Integer INTERN_PORT = 10500;
	private static final String HOST_IP = "localhost";
	private JPanel pnlTop;
	private JTable statTable;
	private JPanel pnlBottom;
	private JButton testStart;
	private JButton testCancel;
	private final ResourceBundle lang;
	private final PresentationFacade presFac;
	private JComboBox cmbGameType;
	private List<GUIPluginInstance> plugins;
	private JTextField[] txfclient;
	private JLabel[] lblclient;
	private JButton[] btnclient;
	private JPanel[] pnlclient;
	protected int curTest;
	protected int numTest;
	private JPanel pnlPref;
	private MyTableModel logModel;
	private JCheckBox ckbDebug;
	private JTextField txfNumTest;
	private JTextArea txtarea;
	private JPanel pnlCenter;
	private JLabel lblCenter;
	private IObservation obs;

	public TestRangeDialog(JFrame frame) {
		super();
		presFac = PresentationFacade.getInstance();
		lang = presFac.getLogicFacade().getLanguageData();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		setLayout(new BorderLayout());

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		Vector<String> items = presFac.getLogicFacade().getPluginNames(plugins);
		cmbGameType = new JComboBox(items);
		cmbGameType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					GUIPluginInstance selPlugin = getSelectedPlugin();
					drawSelectedPluginView(selPlugin);
				}
			}
		});

		txfNumTest = new JTextField(5);
		txfNumTest.setText("100"); // default
		JLabel lblNumTest = new JLabel(lang.getString("dialog_test_lbl_numtest"));
		lblNumTest.setLabelFor(lblNumTest);

		ckbDebug = new JCheckBox(lang.getString("dialog_create_pref_debug"));
		ckbDebug.setToolTipText(lang.getString("dialog_create_pref_debug_hint"));

		pnlPref = new JPanel();
		pnlPref.add(cmbGameType);
		pnlPref.add(lblNumTest);
		pnlPref.add(txfNumTest);
		pnlPref.add(ckbDebug);
		// -----------------------------------------------------------
		statTable = new JTable(new MyTableModel());
		statTable.setRowHeight(25);
		// set single selection on one cell
		statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		statTable.setCellSelectionEnabled(false);
		// don't let the user change the columns' order or width
		statTable.getTableHeader().setReorderingAllowed(false);
		// statTable.getTableHeader().setResizingAllowed(false);
		// -----------------------------------------------------------
		pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.PAGE_AXIS));
		// -----------------------------------------------------------
		GUIPluginInstance selPlugin = getSelectedPlugin();
		drawSelectedPluginView(selPlugin);

		// -----------------------------------------------------------

		txtarea = new JTextArea();
		lblCenter = new JLabel(lang.getString("dialog_test_tbl_log"), JLabel.CENTER);
		Font font = new Font(lblCenter.getFont().getName(), lblCenter.getFont()
				.getStyle(), lblCenter.getFont().getSize() + 4);
		lblCenter.setFont(font);
		// -----------------------------------------------------------

		testStart = new JButton(lang.getString("dialog_test_btn_start"));
		testStart.addActionListener(new ActionListener() {
			private boolean testing = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (testing) {
					cancelTest();
					testStart.setText(lang.getString("dialog_test_btn_start"));
					testStart.setEnabled(true);
					cmbGameType.setEnabled(true);
					txtarea.append(lang.getString("dialog_test_msg_cancel"));
				} else {
					if (prepareTest()) {
						testStart.setText(lang.getString("dialog_test_btn_stop"));
						testStart.setEnabled(false);
						cmbGameType.setEnabled(false);
						// first game with first player at the first position
						startTest(true);
					}
				}
			}
		});

		testCancel = new JButton(lang.getString("dialog_test_btn_cancel"));
		testCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TestRangeDialog.this.dispose();
			}
		});

		pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlBottom.add(testStart);
		pnlBottom.add(testCancel);

		pnlCenter = new JPanel();
		pnlCenter.setBorder(BorderFactory.createEtchedBorder());
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.PAGE_AXIS));
		pnlCenter.add(lblCenter);
		pnlCenter.add(new JScrollPane(txtarea));

		// add components
		this.add(pnlTop, BorderLayout.PAGE_START);
		this.add(pnlCenter, BorderLayout.CENTER);
		this.add(pnlBottom, BorderLayout.PAGE_END);

		// set dialog preferences
		setTitle(lang.getString("dialog_test_title"));
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(500, 500));
		setMinimumSize(getPreferredSize());
		pack();
		setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelTest();
				super.windowClosing(e);
			}
		});
	}

	protected void drawSelectedPluginView(GUIPluginInstance selPlugin) {

		// remove old rows
		DefaultTableModel model = (DefaultTableModel) statTable.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		// -------------------------------------------------------------

		model.addColumn(lang.getString("dialog_test_stats_pos"));
		model.addColumn(lang.getString("dialog_test_stats_name"));
		model.addColumn(lang.getString("dialog_test_stats_wins"));
		model.addColumn(lang.getString("dialog_test_stats_losses"));
		ScoreDefinition statColumns = selPlugin.getPlugin().getScoreDefinition();
		for (ScoreFragment column : statColumns) {
			model.addColumn(column.getName());
		}
		model.addColumn(lang.getString("dialog_test_stats_invalid"));
		model.addColumn(lang.getString("dialog_test_stats_crashes"));
		model.addColumn(lang.getString("dialog_test_stats_timeavg"));
		statTable.setModel(model);
		statTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		statTable.getColumnModel().getColumn(2).setPreferredWidth(20);
		statTable.getColumnModel().getColumn(3).setPreferredWidth(20);

		statTable.getColumnModel().getColumn(model.getColumnCount() - 3)
				.setPreferredWidth(20);
		statTable.getColumnModel().getColumn(model.getColumnCount() - 2)
				.setPreferredWidth(20);
		statTable.getColumnModel().getColumn(model.getColumnCount() - 1)
				.setPreferredWidth(30);

		// -------------------------------------------------------------

		int ki_count = selPlugin.getPlugin().getMinimalPlayerCount();

		txfclient = new JTextField[ki_count];
		lblclient = new JLabel[ki_count];
		btnclient = new JButton[ki_count];
		pnlclient = new JPanel[ki_count];

		// add new text fields, labels and rows
		for (int i = 0; i < ki_count; i++) {
			txfclient[i] = new JTextField(20);
			final JTextField txfClient = txfclient[i];

			lblclient[i] = new JLabel(lang.getString("dialog_test_lbl_ki") + " " + i);
			lblclient[i].setLabelFor(txfclient[i]);

			btnclient[i] = new JButton(lang.getString("dialog_test_btn_file"));
			btnclient[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadClient(txfClient);
				}
			});

			pnlclient[i] = new JPanel();
			pnlclient[i].add(lblclient[i]);
			pnlclient[i].add(txfclient[i]);
			pnlclient[i].add(btnclient[i]);
			// ------------------------------------------------
			String pos = "" + i;
			String name = txfclient[i].getText();

			Vector<String> rowData = new Vector<String>(); // default
			rowData.add(pos);
			rowData.add(new File(name).getName() + " " + i);
			model.addRow(rowData);
		}

		// show table without extra space
		statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());

		// display
		pnlTop.removeAll();
		pnlTop.add(pnlPref);
		for (int i = 0; i < txfclient.length; i++) {
			pnlTop.add(pnlclient[i]);
		}
		pnlTop.add(new JScrollPane(statTable));
		pnlTop.validate();
		pnlTop.invalidate();// TODO order?

		System.out.println("UPDATE: test range dialog");
	}

	/**
	 * Prepares the test range.
	 */
	private boolean prepareTest() {

		curTest = 0;
		numTest = new Integer(txfNumTest.getText());

		for (JTextField element : txfclient) {
			File file = new File(element.getText());
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, lang
						.getString("dialog_test_error_path_msg"), lang
						.getString("dialog_test_error_path_title"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		// start server
		presFac.getLogicFacade().startServer(INTERN_PORT, !ckbDebug.isSelected());

		return true;
	}

	protected void startTest(final boolean ascending) {

		GUIPluginInstance selPlugin = getSelectedPlugin();

		curTest++;

		String filename = "";
		int playerCount = txfclient.length;
		IGamePreparation prep;
		try {
			prep = selPlugin.getPlugin().prepareGame(HOST_IP, INTERN_PORT, playerCount,
					filename);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, lang
					.getString("dialog_test_error_network_msg"), lang
					.getString("dialog_test_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			stopServer();
			return;
		}

		// switch slot declaration
		final int offset;
		if (ascending) {
			offset = 0;
		} else {
			offset = prep.getSlots().size() - 1;
		}

		final ConnectingDialog connDial = new ConnectingDialog();

		// get observer
		obs = prep.getObserver();
		obs.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded(GameResult result) {
				updateStatistics(offset, result);
				// spaltenbeschreibungen:
				// selPlugin.getPlugin().getScoreDefinition().
				// add log message
				// results[0].equals("1") -> spieler 0 ist sieger//TODO
				String clientName = "";
				txtarea.append(clientName + " " + lang.getString("dialog_test_win"));
				// start new test if number of tests is not still reached
				if (curTest < numTest) {
					startTest(!ascending);
				} else {
					stopServer();
					cmbGameType.setEnabled(true);
					testStart.setEnabled(true);
				}
			}
		});
		obs.addNewTurnListener(new INewTurnListener() {
			@Override
			public void newTurn(int playerid, String info) {
				String path = txfclient[offset - playerid].getText();
				String clientName = new File(path).getName() + " " + (playerid + 1);
				// add log
				txtarea.append(clientName + ": " + info);
			}
		});
		obs.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				connDial.close();
				obs.start();
			}
		});

		List<KIInformation> KIs = new ArrayList<KIInformation>();

		txtarea.append(">>> " + lang.getString("dialog_test_switch"));
		// add slots
		for (int i = 0; i < prep.getSlots().size(); i++) {
			ISlot slot = prep.getSlots().get(i);
			String path = txfclient[offset - i].getText();

			String clientName = new File(path).getName() + " " + (i + 1);
			txtarea.append(clientName + " " + lang.getString("dialog_test_switchpos")
					+ " " + (i + 1));

			KIs.add(new KIInformation(slot.asClient(), path));
		}

		// start KI (intern) clients
		for (KIInformation kinfo : KIs) {
			String file = kinfo.getPath();
			String[] params = kinfo.getParameters();
			try {
				HelperMethods.exec(file, params);
			} catch (IOException e) {
				e.printStackTrace();
				stopServer();
				JOptionPane.showMessageDialog(this, lang
						.getString("dialog_test_error_client_msg"), lang
						.getString("dialog_test_error_client_title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (UnsupportedFileExtensionException e) {
				e.printStackTrace();
				stopServer();
				JOptionPane.showMessageDialog(this, lang
						.getString("dialog_error_fileext_msg"), lang
						.getString("dialog_error_fileext_msg"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// show connecting dialog
		if (connDial.showDialog() == JOptionPane.CANCEL_OPTION) {
			cancelTest();
		}
	}

	/**
	 * Updates the statistics table
	 * 
	 * @param offset
	 * @param result
	 */
	protected void updateStatistics(int offset, GameResult result) {
		// display wins/losses etc.
		for (int i = 0; i < result.getScores().size(); i++) {
			int playerId = offset - i;
			String path = txfclient[playerId].getText();
			String clientName = new File(path).getName() + " " + (i + 1);

			MyTableModel model = (MyTableModel) statTable.getModel();
			model.setValueAt(playerId, playerId, 0);
			model.setValueAt(clientName, playerId, 1);

			String[] stats = result.getScores().get(playerId).toStrings();
			for (int j = 0; j < stats.length; j++) {
				model.setValueAt(clientName, playerId, j + 2);
			}
		}
	}

	/**
	 * Cancels the active test range.
	 */
	private void cancelTest() {
		curTest = numTest;
		obs.cancel();
		stopServer();
	}

	private void loadClient(JTextField txf) {
		JFileChooser chooser = new JFileChooser();
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			txf.setText(f.getAbsolutePath());
		}
	}

	private GUIPluginInstance getSelectedPlugin() {
		return plugins.get(cmbGameType.getSelectedIndex());
	}

	/**
	 * Closes the server.
	 */
	private void stopServer() {
		presFac.getLogicFacade().stopServer();
	}

	/**
	 * Non-editable table model.
	 * 
	 * @author chw
	 * 
	 */
	private class MyTableModel extends DefaultTableModel {

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

	}

}
