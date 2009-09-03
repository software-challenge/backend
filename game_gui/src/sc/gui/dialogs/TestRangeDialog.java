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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import sc.common.HelperMethods;
import sc.common.UnsupportedFileExtensionException;
import sc.gui.PresentationFacade;
import sc.gui.dialogs.renderer.CenteredTableCellRenderer;
import sc.gui.stuff.KIInformation;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IGuiPlugin;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.save.GUIConfiguration;
import sc.plugin.GUIPluginInstance;
import sc.shared.GameResult;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

@SuppressWarnings("serial")
public class TestRangeDialog extends JDialog {

	private static final String DEFAULT_HOST = "localhost";
	private JPanel pnlTop;
	private JTable statTable;
	private JPanel pnlBottom;
	private JButton testStart;
	private JButton testCancel;
	private final Properties lang;
	private final PresentationFacade presFac;
	private JComboBox cmbGameType;
	private List<GUIPluginInstance> plugins;
	private JTextField[] txfclient;
	private JLabel[] lblclient;
	private JButton[] btnclient;
	private JPanel[] pnlclient;
	/**
	 * 0 indicates "no testing"<br>
	 * 1..* indicates the current running test, i.e. "testing"
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

	public TestRangeDialog() {
		super();
		presFac = PresentationFacade.getInstance();
		lang = presFac.getLogicFacade().getLanguageData();
		initCurTest(); // not testing
		createGUI();
	}

	/**
	 * Creates the test range GUI
	 */
	private void createGUI() {

		this.setLayout(new BorderLayout());

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		final Vector<String> items = presFac.getLogicFacade().getPluginNames(
				plugins);
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
		// only numbers and at least number 0
		txfNumTest.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a)
					throws BadLocationException {

				String wholeText = getText(0, getLength()) + str;

				try {
					int value = new Integer(wholeText);
					if (value < 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}

				super.insertString(offs, str, a);
			}
		});
		// must be set after setDocument()
		txfNumTest.setText(String.valueOf(GUIConfiguration.instance()
				.getNumTest())); // default

		JLabel lblNumTest = new JLabel(lang
				.getProperty("dialog_test_lbl_numtest"));
		lblNumTest.setLabelFor(lblNumTest);

		ckbDebug = new JCheckBox(lang.getProperty("dialog_create_pref_debug"));
		ckbDebug.setToolTipText(lang
				.getProperty("dialog_create_pref_debug_hint"));

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
		statTable.getTableHeader().setResizingAllowed(false);
		// -----------------------------------------------------------
		pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.PAGE_AXIS));
		// -----------------------------------------------------------
		GUIPluginInstance selPlugin = getSelectedPlugin();
		drawSelectedPluginView(selPlugin);

		// -----------------------------------------------------------

		progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
		progressBar.setStringPainted(true); // draw procent

		lblCenter = new JLabel(lang.getProperty("dialog_test_tbl_log"),
				JLabel.CENTER);
		Font font = new Font(lblCenter.getFont().getName(), lblCenter.getFont()
				.getStyle(), lblCenter.getFont().getSize() + 4);
		lblCenter.setFont(font);

		txtarea = new JTextArea();
		scrollTextArea = new JScrollPane(txtarea);
		scrollTextArea.setAutoscrolls(true);

		// -----------------------------------------------------------

		testStart = new JButton(lang.getProperty("dialog_test_btn_start"));
		testStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (curTest > 0) { // testing
					cancelTest();
				} else {
					if (prepareTest()) {
						testStart.setText(lang
								.getProperty("dialog_test_btn_stop"));
						cmbGameType.setEnabled(false);
						// first game with first player at the first position
						startTest();
					}
				}
			}
		});

		testCancel = new JButton(lang.getProperty("dialog_test_btn_cancel"));
		testCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelTestAndSave();
				TestRangeDialog.this.setVisible(false);
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
		this.setTitle(lang.getProperty("dialog_test_title"));
		this.setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(500, 500));
		this.setMinimumSize(getPreferredSize());
		this.pack();
		this.setLocationRelativeTo(null);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelTestAndSave();
				super.windowClosing(e);
			}
		});
	}

	/**
	 * Cancels the test if active and saves the number of tests
	 */
	protected void cancelTestAndSave() {
		cancelTest();
		try {
			GUIConfiguration.instance().setNumberOfTests(
					new Integer(txfNumTest.getText()));
		} catch (NumberFormatException ex) {
			// just don't save the invalid value
		}
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

		// add columns
		model.addColumn(lang.getProperty("dialog_test_stats_pos"));
		model.addColumn(lang.getProperty("dialog_test_stats_name"));
		ScoreDefinition statColumns = selPlugin.getPlugin()
				.getScoreDefinition();
		for (int i = 0; i < statColumns.size(); i++) {
			ScoreFragment column = statColumns.get(i);
			model.addColumn(column.getName());
		}

		/*
		 * set minimum and maximum width for each column to enable correct
		 * resizablity
		 */
		statTable.getColumnModel().getColumn(0).setCellRenderer(
				new CenteredTableCellRenderer());
		statTable.getColumnModel().getColumn(0).setMinWidth(0);
		statTable.getColumnModel().getColumn(0).setMaxWidth(100);
		for (int i = 0; i < statColumns.size(); i++) {
			int index = i + 2;
			statTable.getColumnModel().getColumn(index).setMinWidth(0);
			statTable.getColumnModel().getColumn(index).setMaxWidth(100);
		}

		// set width of columns
		statTable.getColumnModel().getColumn(0).setPreferredWidth(50);
		for (int i = 0; i < statColumns.size(); i++) {
			statTable.getColumnModel().getColumn(i + 2).setPreferredWidth(80);
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

			String playerNumber = String.valueOf(i + 1);
			lblclient[i] = new JLabel(lang.getProperty("dialog_test_lbl_ki")
					+ " " + playerNumber);
			lblclient[i].setLabelFor(txfclient[i]);

			btnclient[i] = new JButton(lang.getProperty("dialog_test_btn_file"));
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
			rowData.add(playerNumber); // set position#
			model.addRow(rowData);
		}

		// show table without extra space
		statTable.setPreferredScrollableViewportSize(statTable
				.getPreferredSize());

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

		initCurTest();
		// on-demand number checking -> here: no checking required
		numTest = new Integer(txfNumTest.getText());

		progressBar.setMaximum(numTest);
		progressBar.setValue(0);

		for (JTextField element : txfclient) {
			File file = new File(element.getText());
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, lang
						.getProperty("dialog_test_error_path_msg"), lang
						.getProperty("dialog_test_error_path_title"),
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
			name = HelperMethods.getFilenameWithoutFileExt(name) + " "
					+ (i + 1);
			model.setValueAt(name, i, 1);
			for (int j = 0; j < selPlugin.getScoreDefinition().size(); j++) {
				model.setValueAt(BigDecimal.ZERO, i, 2 + j); // set default 0
			}
		}
		statTable.validate();

		// start server
		try {
			presFac.getLogicFacade().startServer(
					SharedConfiguration.DEFAULT_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_create_error_port_blocked_msg"), lang
					.getProperty("dialog_create_error_port_blocked_title"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), lang
					.getProperty("dialog_error_title"),
					JOptionPane.ERROR_MESSAGE);
		}

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

		final ConnectingDialog connectionDialog = new ConnectingDialog();
		
		try
		{
			curTest++;
	
			final int rotation = getRotation(txfclient.length);
	
			final List<SlotDescriptor> slotDescriptors = prepareSlots(
					preparePlayerNames(), rotation);
			final IGamePreparation prep = prepareGame(getSelectedPlugin(),
					slotDescriptors);
	
			createObserveration(rotation, slotDescriptors, prep, connectionDialog);
	
			// only display message after the first round
			if (curTest > 1) {
				addLogMessage(">>> " + lang.getProperty("dialog_test_switch"));
			}
	
			List<KIInformation> KIs = prepareClientProcesses(slotDescriptors, prep,
					rotation);
	
			runClientProcesses(KIs);
			
			// show connecting dialog
			if (connectionDialog.showDialog() == JOptionPane.CANCEL_OPTION) {
				cancelTest();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			cancelTest();
		}

	}

	private int getRotation(int playerCount) {
		return (curTest + 1) % playerCount;
	}

	private List<KIInformation> prepareClientProcesses(
			final List<SlotDescriptor> descriptors,
			final IGamePreparation prep, final int rotation) {
		List<ISlot> slots = new LinkedList<ISlot>(prep.getSlots());
		List<String> paths = prepareProcessPaths(rotation);

		List<KIInformation> KIs = new ArrayList<KIInformation>();
		for (int i = 0; i < slots.size(); i++) {
			ISlot slot = slots.get(i);
			String path = paths.get(i);

			KIs.add(new KIInformation(slot.asClient(), path));

			addLogMessage(descriptors.get(i).getDisplayName() + " "
					+ lang.getProperty("dialog_test_switchpos") + " " + (i + 1));
		}
		return KIs;
	}

	private List<String> prepareProcessPaths(int rotation) {
		List<String> paths = new LinkedList<String>();

		for (int i = 0; i < txfclient.length; i++) {
			paths.add(txfclient[i].getText());
		}

		Collections.rotate(paths, rotation);

		return paths;
	}

	private void runClientProcesses(List<KIInformation> KIs) throws IOException {
		// start KI (intern) clients
		for (int i = 0; i < KIs.size(); i++) {
			KIInformation kinfo = KIs.get(i);

			String file = kinfo.getPath();
			String[] params = kinfo.getParameters();

			try {
				HelperMethods.exec(file, params);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, lang
						.getProperty("dialog_test_error_client_msg"), lang
						.getProperty("dialog_test_error_client_title"),
						JOptionPane.ERROR_MESSAGE);
				throw new IOException(e);
			} catch (UnsupportedFileExtensionException e) {
				JOptionPane.showMessageDialog(this, lang
						.getProperty("dialog_error_fileext_msg"), lang
						.getProperty("dialog_error_fileext_msg"),
						JOptionPane.ERROR_MESSAGE);
				throw new IOException(e);
			}
		}
	}

	private void createObserveration(final int rotation,
			final List<SlotDescriptor> slotDescriptors,
			final IGamePreparation prep, final ConnectingDialog connectionDialog) {
		// get observer
		obs = prep.getObserver();
		obs.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void gameEnded(GameResult result) {
				addLogMessage(lang.getProperty("dialog_test_end") + " "
						+ curTest + "/" + numTest);
				// purpose
				updateStatistics(rotation, result);
				// update progress bar
				progressBar.setValue(progressBar.getValue() + 1);
				// create replay file if this game ended with a failure
				if (!result.isRegular()) {
					String replayFilename = HelperMethods
							.generateReplayFilename(slotDescriptors);
					try {
						obs.saveReplayToFile(replayFilename);
						addLogMessage(lang
								.getProperty("dialog_test_log_replay"));
					} catch (IOException e) {
						e.printStackTrace();
						addLogMessage(lang
								.getProperty("dialog_test_log_replay_error"));
					}
				}

				// start new test if number of tests is not still reached
				if (curTest < numTest) {
					// FIXME: Recursive Execution of Tests might be REALLY bad for big N
					startTest();
				} else {
					stopServer();
					cmbGameType.setEnabled(true);
					testStart
							.setText(lang.getProperty("dialog_test_btn_start"));
					TestRangeDialog.this.repaint();
				}
			}
		});
		obs.addNewTurnListener(new INewTurnListener() {
			@Override
			public void newTurn(int playerid, String info) {
				String clientName = slotDescriptors.get(playerid)
						.getDisplayName();
				// add log
				if (!info.equals("")) {
					addLogMessage(clientName + ": " + info);
				}
			}
		});
		obs.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				connectionDialog.close();
				obs.start();
			}
		});
	}

	private List<SlotDescriptor> prepareSlots(final List<String> playerNames,
			int rotation) {
		final List<SlotDescriptor> descriptors = new LinkedList<SlotDescriptor>();

		for (String playerName : playerNames) {
			descriptors.add(new SlotDescriptor(playerName, !ckbDebug
					.isSelected()));
		}

		Collections.rotate(descriptors, rotation);

		return descriptors;
	}

	private List<String> preparePlayerNames() {
		// get player names
		final List<String> playerNames = new LinkedList<String>();

		for (int i = 0; i < txfclient.length; i++) {
			String path = txfclient[i].getText();
			String clientName = HelperMethods
					.getFilenameWithoutFileExt(new File(path).getName())
					+ " " + (i + 1);
			playerNames.add(clientName);
		}

		return playerNames;
	}

	private IGamePreparation prepareGame(GUIPluginInstance selPlugin,
			final List<SlotDescriptor> descriptors) throws IOException {
		IGamePreparation prep;
		try {
			prep = selPlugin.getPlugin()
					.prepareGame(
							DEFAULT_HOST,
							SharedConfiguration.DEFAULT_PORT,
							descriptors.toArray(new SlotDescriptor[descriptors
									.size()]));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_test_error_network_msg"), lang
					.getProperty("dialog_test_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			throw new IOException(e);
		}
		return prep;
	}

	/**
	 * Updates the statistics table
	 * 
	 * @param rotation
	 * @param result
	 */
	protected void updateStatistics(final int rotation, final GameResult result) {
		MyTableModel model = (MyTableModel) statTable.getModel();

		// display wins/losses etc.
		for (int i = 0; i < result.getScores().size(); i++) {
			int statRow = Math.abs(rotation - i);

			List<BigDecimal> stats = result.getScores().get(i).getValues();
			for (int j = 0; j < stats.size(); j++) {
				BigDecimal newStat = stats.get(j);
				BigDecimal old = (BigDecimal) model.getValueAt(statRow, j + 2);

				ScoreAggregation action = result.getDefinition().get(j)
						.getAggregation();
				switch (action) {
				case SUM:

					newStat = old.add(newStat);
					break;
				case AVERAGE:
					// restore old absolute value
					old = old.multiply(BigDecimal.valueOf(curTest - 1));
					// add newStat to absolute value
					newStat = old.add(newStat);
					// divide with curTest (rounded down)
					newStat = newStat.divideToIntegralValue(BigDecimal
							.valueOf(curTest));
					break;
				default:
					throw new RuntimeException("Unknown aggregation type ("
							+ action + ")");
				}
				// set to model
				model.setValueAt(newStat, statRow, j + 2);
			}
		}
	}

	/**
	 * Cancels the active test range.
	 */
	private void cancelTest() {
		initCurTest();
		if (null != obs)
			obs.cancel();
		stopServer();
		testStart.setText(lang.getProperty("dialog_test_btn_start"));
		cmbGameType.setEnabled(true);
		addLogMessage(lang.getProperty("dialog_test_msg_cancel"));
	}

	/**
	 * Initializes curTest to 0.
	 */
	private void initCurTest() {
		curTest = 0;
	}

	/**
	 * Loads a client, i.e. opens a file choose dialog
	 * 
	 * @param txf
	 */
	private void loadClient(JTextField txf) {
		JFileChooser chooser = new JFileChooser(GUIConfiguration.instance()
				.getTestDialogPath());
		chooser.setDialogTitle(lang.getProperty("dialog_test_dialog_title"));
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
