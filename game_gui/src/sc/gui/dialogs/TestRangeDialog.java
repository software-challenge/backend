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
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import sc.common.HelperMethods;
import sc.common.UnsupportedFileExtensionException;
import sc.gui.PresentationFacade;
import sc.gui.stuff.KIInformation;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.GUIConfiguration;
import sc.plugin.GUIPluginInstance;
import sc.shared.GameResult;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import sc.shared.SlotDescriptor;
import sc.shared.ScoreFragment.Aggregation;

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
	/**
	 * -1 indicates "no testing"<br>
	 * 0..* indicates the completed number of tests so far, i.e. "testing"
	 */
	protected int curTest;
	protected int numTest;
	private JPanel pnlPref;
	private JCheckBox ckbDebug;
	private JTextField txfNumTest;
	private JTextArea txtarea;
	private JPanel pnlCenter;
	private JLabel lblCenter;
	private IObservation obs;
	private JScrollPane scrollTextArea;
	private JProgressBar progressBar;

	public TestRangeDialog(JFrame frame) {
		super();
		presFac = PresentationFacade.getInstance();
		lang = presFac.getLogicFacade().getLanguageData();
		curTest = -1; // not testing
		createGUI();
	}

	/**
	 * Creates the test range GUI
	 */
	private void createGUI() {

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
		txfNumTest.setText(String.valueOf(GUIConfiguration.instance().getNumTest())); // default
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

		progressBar = new JProgressBar(SwingConstants.HORIZONTAL);

		lblCenter = new JLabel(lang.getString("dialog_test_tbl_log"), JLabel.CENTER);
		Font font = new Font(lblCenter.getFont().getName(), lblCenter.getFont()
				.getStyle(), lblCenter.getFont().getSize() + 4);
		lblCenter.setFont(font);

		txtarea = new JTextArea();
		scrollTextArea = new JScrollPane(txtarea);
		scrollTextArea.setAutoscrolls(true);

		// -----------------------------------------------------------

		testStart = new JButton(lang.getString("dialog_test_btn_start"));
		testStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (curTest >= 0) { // testing
					cancelTest();
					testStart.setText(lang.getString("dialog_test_btn_start"));
					cmbGameType.setEnabled(true);
					addLogMessage(lang.getString("dialog_test_msg_cancel"));
				} else {
					if (prepareTest()) {
						testStart.setText(lang.getString("dialog_test_btn_stop"));
						cmbGameType.setEnabled(false);
						// first game with first player at the first position
						startTest();
					}
				}
			}
		});

		testCancel = new JButton(lang.getString("dialog_test_btn_cancel"));
		testCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelTest();
				TestRangeDialog.this.dispose();
			}
		});

		pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlBottom.add(testStart);
		pnlBottom.add(testCancel);

		pnlCenter = new JPanel();
		pnlCenter.setBorder(BorderFactory.createEtchedBorder());
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.PAGE_AXIS));
		pnlCenter.add(progressBar);
		pnlCenter.add(lblCenter);
		pnlCenter.add(scrollTextArea);

		// add components
		this.add(pnlTop, BorderLayout.PAGE_START);
		this.add(pnlCenter, BorderLayout.CENTER);
		this.add(pnlBottom, BorderLayout.PAGE_END);

		// set dialog preferences
		setTitle(lang.getString("dialog_test_title"));
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
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

	/**
	 * According to the Checkbox's index, which selects a specific plugin, this
	 * dialog is painted.
	 * 
	 * @param selPlugin
	 */
	protected void drawSelectedPluginView(GUIPluginInstance selPlugin) {

		// remove old rows
		DefaultTableModel model = (DefaultTableModel) statTable.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		// -------------------------------------------------------------

		model.addColumn(lang.getString("dialog_test_stats_pos"));
		statTable.getColumnModel().getColumn(0).setPreferredWidth(15);
		model.addColumn(lang.getString("dialog_test_stats_name"));

		ScoreDefinition statColumns = selPlugin.getPlugin().getScoreDefinition();
		for (int i = 0; i < statColumns.size(); i++) {
			ScoreFragment column = statColumns.get(i);
			model.addColumn(column.getName());
			statTable.getColumnModel().getColumn(i + 2).setPreferredWidth(20);
		}

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
			Vector<String> rowData = new Vector<String>(); // default
			rowData.add(String.valueOf(i)); // set position#
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
		// pnlTop.invalidate();// TODO order?

		System.out.println("UPDATE: test range dialog");
	}

	/**
	 * Prepares the test range.
	 */
	private boolean prepareTest() {

		IGuiPlugin selPlugin = getSelectedPlugin().getPlugin();

		curTest = -1;
		try {
			numTest = new Integer(txfNumTest.getText());
		} catch (NumberFormatException e) {
			numTest = GUIConfiguration.instance().getNumTest();
			txfNumTest.setText(String.valueOf(numTest));
		}

		progressBar.setMaximum(numTest);
		progressBar.setValue(0);

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

		// display the clients' positions and names
		MyTableModel model = (MyTableModel) statTable.getModel();
		for (int i = 0; i < txfclient.length; i++) {
			model.setValueAt(new Integer(i + 1), i, 0);
			String name = new File(txfclient[i].getText()).getName();
			// without file ext and with a number
			name = HelperMethods.getFilenameWithoutFileExt(name) + " " + (i + 1);
			model.setValueAt(name, i, 1);
			for (int j = 0; j < selPlugin.getScoreDefinition().size(); j++) {
				model.setValueAt(new BigDecimal(0), i, 2 + j); // set default 0
			}
		}
		statTable.validate();

		// start server
		presFac.getLogicFacade().startServer(INTERN_PORT);

		// disable rendering
		selPlugin.setRenderContext(null, false);

		return true;
	}

	/**
	 * Starts a prepared test range.
	 * 
	 * @param ascending
	 */
	protected void startTest() {

		GUIPluginInstance selPlugin = getSelectedPlugin();

		curTest++;

		int playerCount = txfclient.length;

		// switch slot declaration
		final int offset;
		if (curTest % 2 == 0) {
			offset = 0;
		} else {
			offset = playerCount - 1;
		}

		// get player names
		final List<String> playerNames = new LinkedList<String>();
		List<SlotDescriptor> descriptors = new LinkedList<SlotDescriptor>();
		for (int i = 0; i < txfclient.length; i++) {
			String path = txfclient[Math.abs(offset - i)].getText();
			String clientName = HelperMethods.getFilenameWithoutFileExt(new File(path)
					.getName())
					+ " " + (i + 1);
			playerNames.add(clientName);
			descriptors.add(new SlotDescriptor(clientName, !ckbDebug.isSelected()));
		}

		IGamePreparation prep;
		try {
			prep = selPlugin.getPlugin().prepareGame(HOST_IP, INTERN_PORT,
					descriptors.toArray(new SlotDescriptor[0]));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, lang
					.getString("dialog_test_error_network_msg"), lang
					.getString("dialog_test_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			stopServer();
			return;
		}

		final ConnectingDialog connDial = new ConnectingDialog();

		// get observer
		obs = prep.getObserver();
		obs.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded(GameResult result) {
				addLogMessage("Game ended");// FIXME remove; only for test
				// purpose
				updateStatistics(offset, result);
				// add winner log message
				for (int i = 0; i < result.getScores().size(); i++) {
					if (result.getScores().get(i).equals("1")) {
						String clientName = playerNames.get(Math.abs(offset - i));
						addLogMessage(clientName + " "
								+ lang.getString("dialog_test_win"));
						break;
					}
				}
				// update progress bar
				progressBar.setValue(progressBar.getValue() + 1);

				// start new test if number of tests is not still reached
				if (curTest < numTest) {
					startTest();
				} else {
					stopServer();
					cmbGameType.setEnabled(true);
				}
			}
		});
		obs.addNewTurnListener(new INewTurnListener() {
			@Override
			public void newTurn(int playerid, String info) {
				String clientName = playerNames.get(Math.abs(offset - playerid));
				// add log
				if (!info.equals(""))
					addLogMessage(clientName + ": " + info);
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

		addLogMessage(">>> " + lang.getString("dialog_test_switch"));
		// add slots
		for (int i = 0; i < prep.getSlots().size(); i++) {
			ISlot slot = prep.getSlots().get(i);
			String path = txfclient[Math.abs(offset - i)].getText();
			KIs.add(new KIInformation(slot.asClient(), path));

			String clientName = playerNames.get(Math.abs(offset - i));
			addLogMessage(clientName + " " + lang.getString("dialog_test_switchpos")
					+ " " + (i + 1));
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
				JOptionPane
						.showMessageDialog(this, lang
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
		MyTableModel model = (MyTableModel) statTable.getModel();

		// display wins/losses etc.
		for (int i = 0; i < result.getScores().size(); i++) {
			int playerId = Math.abs(offset - i);

			List<BigDecimal> stats = result.getScores().get(playerId).getValues();
			for (int j = 0; j < stats.size(); j++) {
				BigDecimal newStat = stats.get(i);
				BigDecimal old = (BigDecimal) model.getValueAt(playerId, j + 2);
				
				if(old == null)
				{
					old = BigDecimal.ZERO;
				}

				Aggregation action = result.getDefinition().get(i).getAggregation();
				switch (action) {
				case SUM:
					newStat = newStat.add(old);
					break;
				case AVERAGE:
					newStat = newStat.add(old);
					newStat = newStat.divide(BigDecimal.valueOf(curTest + 1));
					break;
				default:
					throw new RuntimeException("Unknown aggregation type (" + action
							+ ")");
				}
				// set to model
				model.setValueAt(newStat, playerId, j + 2);
			}
		}
	}

	/**
	 * Cancels the active test range.
	 */
	private void cancelTest() {
		curTest = -1;
		if (null != obs)
			obs.cancel();
		stopServer();
		GUIConfiguration.instance().setNumberOfTests(numTest);
	}

	/**
	 * Loads a client, i.e. opens a file choose dialog
	 * 
	 * @param txf
	 */
	private void loadClient(JTextField txf) {
		JFileChooser chooser = new JFileChooser(GUIConfiguration.instance()
				.getTestDialogPath());
		chooser.setDialogTitle(lang.getString("dialog_test_dialog_title"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			txf.setText(f.getAbsolutePath());
			GUIConfiguration.instance().setTestDialogPath(f.getParent());
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

	/**
	 * Adds the given <code>msg</code> to the log text area.
	 * 
	 * @param msg
	 */
	private void addLogMessage(final String msg) {
		txtarea.append(msg + "\n");
		txtarea.setCaretPosition(txtarea.getText().length());
	}
}
