package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.api.plugins.IPlayer;
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
import sc.shared.PlayerScore;
import sc.shared.ScoreAggregation;
import sc.shared.ScoreDefinition;
import sc.shared.ScoreFragment;
import sc.shared.SharedConfiguration;
import sc.shared.SlotDescriptor;

@SuppressWarnings("serial")
public class TestRangeDialog extends JDialog {

  private static final Logger logger = LoggerFactory.getLogger(TestRangeDialog.class);

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
	private JTextField[] txfparams;
	private JLabel[] lblclient;
	private JButton[] btnclient;
	private JPanel[] pnlclient;
	private volatile int curTest = 0;
	private int numTest;
	private JPanel pnlPref;
	private JCheckBox ckbDebug;
	private JTextField txfNumTest;
	private JTextArea txtarea;
	private JPanel pnlCenter;
	private JLabel lblCenter;
	private IObservation obs;
	private JScrollPane scrollTextArea;
	private JProgressBar progressBar;
	private JPanel pnlBottomRight;
	private JCheckBox cb_showLog;
	private JPanel pnl_saveReplay;
	private JPanel pnlBottomTop;
	private int freePort;
	private boolean testStarted;
  private final CyclicBarrier gameEndReached = new CyclicBarrier(2);

	private List<List<BigDecimal>> absoluteValues;

	public TestRangeDialog() {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = this.presFac.getLogicFacade().getLanguageData();
		this.testStarted = false;
		createGUI();
	}

	/**
	 * Creates the test range GUI
	 */
	private void createGUI() {

		setLayout(new BorderLayout());

		this.plugins = this.presFac.getLogicFacade().getAvailablePluginsSorted();
		final Vector<String> items = this.presFac.getLogicFacade().getPluginNames(
				this.plugins);
		this.cmbGameType = new JComboBox(items);
		this.cmbGameType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					GUIPluginInstance selPlugin = getSelectedPlugin();
					drawSelectedPluginView(selPlugin);
				}
			}
		});

		this.txfNumTest = new JTextField(5);
		// only numbers and at least number 0
		this.txfNumTest.setDocument(new PlainDocument() {
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
		this.txfNumTest.setText(String.valueOf(GUIConfiguration.instance()
				.getNumTest())); // default

		final JLabel lblNumTest = new JLabel(this.lang
				.getProperty("dialog_test_lbl_numtest"));
		lblNumTest.setLabelFor(lblNumTest);

		this.ckbDebug = new JCheckBox(this.lang.getProperty("dialog_create_pref_debug"));
		this.ckbDebug.setToolTipText(this.lang
				.getProperty("dialog_create_pref_debug_hint"));

		this.pnlPref = new JPanel();
		this.pnlPref.add(this.cmbGameType);
		this.pnlPref.add(lblNumTest);
		this.pnlPref.add(this.txfNumTest);
		this.pnlPref.add(this.ckbDebug);
		// -----------------------------------------------------------
		this.statTable = new JTable(new MyTableModel());
		this.statTable.setRowHeight(25);
		// set single selection on one cell
		this.statTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.statTable.setCellSelectionEnabled(false);
		// don't let the user change the columns' order or width
		this.statTable.getTableHeader().setReorderingAllowed(false);
		this.statTable.getTableHeader().setResizingAllowed(true);
		// -----------------------------------------------------------
		this.pnlTop = new JPanel();
		this.pnlTop.setLayout(new BoxLayout(this.pnlTop, BoxLayout.PAGE_AXIS));
		// -----------------------------------------------------------
		GUIPluginInstance selPlugin = getSelectedPlugin();
		drawSelectedPluginView(selPlugin);

		// -----------------------------------------------------------

		this.progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
		this.progressBar.setStringPainted(true); // draw percent

		this.lblCenter = new JLabel(this.lang.getProperty("dialog_test_tbl_log"),
				JLabel.CENTER);
		Font font = new Font(this.lblCenter.getFont().getName(), this.lblCenter.getFont()
				.getStyle(), this.lblCenter.getFont().getSize() + 4);
		this.lblCenter.setFont(font);

		this.txtarea = new JTextArea();
		this.scrollTextArea = new JScrollPane(this.txtarea);
		this.scrollTextArea.setAutoscrolls(true);

		// -----------------------------------------------------------

		this.cb_showLog = new JCheckBox(this.lang.getProperty("dialog_test_cb_showLog"));
		this.cb_showLog.setSelected(GUIConfiguration.instance().showTestLog());
		this.cb_showLog.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIConfiguration.instance().setShowTestLog(
						TestRangeDialog.this.cb_showLog.isSelected());
			}
		});
		JPanel pnl_showLogLeft = new JPanel(new GridLayout(1, 0));
		pnl_showLogLeft.add(this.cb_showLog);

		createButtonsAtBottom();

		createSaveReplayCheckboxGroup();
		// -------------------------------------------
		this.pnlBottomTop = new JPanel(new GridLayout());
		this.pnlBottomTop.add(pnl_showLogLeft);
		this.pnlBottomTop.add(this.pnl_saveReplay);

		this.pnlBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.pnlBottomRight.add(this.testStart);
		this.pnlBottomRight.add(this.testCancel);

		this.pnlBottom = new JPanel();
		setVerticalFlowLayout(this.pnlBottom);
		this.pnlBottom.add(this.pnlBottomTop);
		this.pnlBottom.add(new JSeparator());
		this.pnlBottom.add(this.pnlBottomRight);
		// -------------------------------------------
		this.pnlCenter = new JPanel();
		this.pnlCenter.setBorder(BorderFactory.createEtchedBorder());
		this.pnlCenter.setLayout(new BoxLayout(this.pnlCenter, BoxLayout.PAGE_AXIS));
		this.pnlCenter.add(this.progressBar);
		this.pnlCenter.add(this.lblCenter);
		this.pnlCenter.add(this.scrollTextArea);

		// add components
		this.add(this.pnlTop, BorderLayout.PAGE_START);
		this.add(this.pnlCenter, BorderLayout.CENTER);
		this.add(this.pnlBottom, BorderLayout.PAGE_END);

		// set dialog preferences
		setTitle(this.lang.getProperty("dialog_test_title"));
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(650, 500));
		setMinimumSize(getPreferredSize());
		pack();
		setLocationRelativeTo(null);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelTestAndSave();
			}
		});
	}

	private void createSaveReplayCheckboxGroup() {
		this.pnl_saveReplay = new JPanel(new GridLayout());
		//setVerticalFlowLayout(pnl_saveReplay);

		JPanel pnl_saveReplayLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lbl_saveReplay = new JLabel(this.lang
				.getProperty("dialog_test_lbl_saveReplay"));
		pnl_saveReplayLeft.add(lbl_saveReplay);

		JPanel pnl_ckbGroup = new JPanel(new GridLayout(3, 0));
		pnl_ckbGroup.setBorder(BorderFactory.createLineBorder(Color.black));

		final JCheckBox ckb_errorGames = new JCheckBox(this.lang
				.getProperty("dialog_test_ckb_error"));
		ckb_errorGames.setSelected(GUIConfiguration.instance().saveErrorGames());
		ckb_errorGames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIConfiguration.instance().setSaveErrorGames(
						ckb_errorGames.isSelected());
			}
		});
		final JCheckBox ckb_lostGames = new JCheckBox(this.lang
				.getProperty("dialog_test_ckb_lost"));
		ckb_lostGames.setSelected(GUIConfiguration.instance().saveLostGames());
		ckb_lostGames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIConfiguration.instance().setSaveLostGames(
						ckb_lostGames.isSelected());
			}
		});
		final JCheckBox ckb_wonGames = new JCheckBox(this.lang
				.getProperty("dialog_test_ckb_won"));
		ckb_wonGames.setSelected(GUIConfiguration.instance().saveWonGames());
		ckb_wonGames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GUIConfiguration.instance().setSaveWonGames(
						ckb_wonGames.isSelected());
			}
		});

		pnl_ckbGroup.add(ckb_errorGames);
		pnl_ckbGroup.add(ckb_lostGames);
		pnl_ckbGroup.add(ckb_wonGames);

		this.pnl_saveReplay.add(pnl_saveReplayLeft);
		this.pnl_saveReplay.add(pnl_ckbGroup);
	}

	private void setVerticalFlowLayout(Container target) {
		target.setLayout(new BoxLayout(target, BoxLayout.PAGE_AXIS));
	}

	/**
	 * Creates the test and cancel button.
	 */
	private void createButtonsAtBottom() {
		this.testStart = new JButton(this.lang.getProperty("dialog_test_btn_start"));
		this.testStart.addActionListener(new ActionListener() {
			@Override
      public void actionPerformed(ActionEvent event) {
        startTestLoop();
      }
    });

    this.testCancel = new JButton(this.lang.getProperty("dialog_test_btn_cancel"));
    this.testCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        cancelTestAndSave();
        TestRangeDialog.this.setVisible(false);
        TestRangeDialog.this.dispose();
      }
    });
  }

  /**
   * Initializes and starts a test range. Runs in its own thread.
   */
  private void startTestLoop() {
    Thread testLoop = new Thread(new Runnable() {
      @Override
      public void run() {
        if (TestRangeDialog.this.testStarted) { // testing
          cancelTest(TestRangeDialog.this.lang.getProperty("dialog_test_msg_cancel"));
        } else {
          if (prepareTest()) {
            while (TestRangeDialog.this.testStarted && TestRangeDialog.this.curTest < TestRangeDialog.this.numTest) {
              updateGUI(false);
              startNewTest();
              try {
                logger.debug("testloop await game end reached {}, id: {}", Thread.currentThread().getName(),
                    Thread.currentThread().getId());
                TestRangeDialog.this.gameEndReached.await(30, TimeUnit.SECONDS);
                logger.debug("testloop await continue {}", this);
              } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                logger.error("Exception while waiting for game end", e);
                cancelTest("Waiting for game end was interrupted");
              }
              TestRangeDialog.this.gameEndReached.reset();
            }
            finishTest();
          }
        }
      }
    });
    testLoop.setName("testrange runner");
    testLoop.start();
  }

	/**
	 * Updates the start/stop button and the text area.
	 *
	 * @param endOfTest
	 *            true if it should reset the gui, otherwise false.
	 */
	protected void updateGUI(boolean endOfTest) {
		if (endOfTest) {
			this.testStart.setText(this.lang.getProperty("dialog_test_btn_restart"));
			this.cmbGameType.setEnabled(true);
		} else {
			this.testStart.setText(this.lang.getProperty("dialog_test_btn_stop"));
			this.cmbGameType.setEnabled(false);
			this.txtarea.setText("");
		}
		TestRangeDialog.this.validate();
	}

	/**
	 * Cancels the test if active and saves the number of tests.<br>
	 * <i>Invoked before closing the dialog.</i>
	 */
	protected void cancelTestAndSave() {
		cancelTest("");
		try {
			GUIConfiguration.instance().setNumberOfTests(
					new Integer(this.txfNumTest.getText()));
		} catch (NumberFormatException ex) {
			// don't save the invalid value
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
		DefaultTableModel model = (DefaultTableModel) this.statTable.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		// remove columns
		model.setColumnCount(0);

		int prefSize = 0;

		// add columns
		model.addColumn(this.lang.getProperty("dialog_test_stats_pos"));
		model.addColumn(this.lang.getProperty("dialog_test_stats_name"));
		ScoreDefinition statColumns = selPlugin.getPlugin()
				.getScoreDefinition();
		for (int i = 0; i < statColumns.size(); i++) {
			ScoreFragment column = statColumns.get(i);
			String columnTitle = column.getName();
			if (column.getAggregation() == ScoreAggregation.AVERAGE) {
				columnTitle = "\u00D8 " + columnTitle;
			}
			model.addColumn(columnTitle);
		}
		model.addColumn(this.lang.getProperty("dialog_test_stats_invalid"));
		this.statTable.getColumnModel().getColumn(this.statTable.getColumnCount() - 1).setMaxWidth(150);
		model.addColumn(this.lang.getProperty("dialog_test_stats_crashed"));
		this.statTable.getColumnModel().getColumn(this.statTable.getColumnCount() - 1).setMaxWidth(150);

		setTableHeaderRenderer(this.statTable);

		/*
		 * set minimum and maximum width for each column to enable correct
		 * resizablity
		 */
		this.statTable.getColumnModel().getColumn(0).setCellRenderer(
				new CenteredTableCellRenderer());
		this.statTable.getColumnModel().getColumn(0).setMinWidth(0);
		this.statTable.getColumnModel().getColumn(0).setMaxWidth(100);

		this.statTable.getColumnModel().getColumn(1).setMaxWidth(300);

		for (int i = 0; i < this.statTable.getColumnCount() - 2; i++) {
			int index = i + 2;
			this.statTable.getColumnModel().getColumn(index).setMinWidth(10);
			//statTable.getColumnModel().getColumn(index).setMaxWidth(100);
		}

		// set width of columns
		this.statTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		this.statTable.getColumnModel().getColumn(1).setPreferredWidth(170);
		prefSize += 200;
		for (int i = 0; i < this.statTable.getColumnCount() - 2; i++) {
			if (i < statColumns.size()) {
				ScoreFragment column = statColumns.get(i);
				this.statTable.getColumnModel().getColumn(i + 2).setPreferredWidth(column.getName().length() * 10);
				prefSize += column.getName().length() * 10;
			} else {
				this.statTable.getColumnModel().getColumn(i + 2).setPreferredWidth(80);
				prefSize += 80;
			}
		}

		// -------------------------------------------------------------

		setPlayerRows(selPlugin);

		// show table without extra space
		//statTable.setPreferredScrollableViewportSize(statTable
		//		.getPreferredSize());
		Dimension prefDim = this.statTable.getPreferredSize();
		prefDim.width = prefSize;
		this.statTable.setPreferredSize(prefDim);
		this.statTable.setPreferredScrollableViewportSize(prefDim);

		// display
		this.pnlTop.removeAll();
		this.pnlTop.add(this.pnlPref);
		for (int i = 0; i < this.txfclient.length; i++) {
			this.pnlTop.add(this.pnlclient[i]);
		}
		for (int i = 0; i < this.txfparams.length; i++) {
			this.pnlTop.add(this.pnlclient[i]);
		}
		JScrollPane statScrollPane = new JScrollPane(this.statTable);
		statScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		statScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.pnlTop.add(statScrollPane);
		this.pnlTop.validate();
		// pnlTop.invalidate();// TODO order?

		logger.debug("UPDATE: test range dialog");
	}

	/**
	 * Adds the necessary input components for each player.
	 *
	 * @param selPlugin
	 */
	private void setPlayerRows(GUIPluginInstance selPlugin) {
		int ki_count = selPlugin.getPlugin().getMinimalPlayerCount();

		this.txfclient = new JTextField[ki_count];
		this.txfparams = new JTextField[ki_count];
		this.lblclient = new JLabel[ki_count];
		this.btnclient = new JButton[ki_count];
		this.pnlclient = new JPanel[ki_count];

		// add new text fields, labels and rows
		for (int i = 0; i < ki_count; i++) {
			this.txfclient[i] = new JTextField(20);
			this.txfparams[i] = new JTextField(10);
			final JTextField txfClient = this.txfclient[i];

			String playerNumber = String.valueOf(i + 1);
			this.lblclient[i] = new JLabel(this.lang.getProperty("dialog_test_lbl_ki")
					+ " " + playerNumber);
			this.lblclient[i].setLabelFor(this.txfclient[i]);

			this.btnclient[i] = new JButton(this.lang.getProperty("dialog_test_btn_file"));
			this.btnclient[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadClient(txfClient);
				}
			});

			this.pnlclient[i] = new JPanel();
			this.pnlclient[i].add(this.lblclient[i]);
			this.pnlclient[i].add(this.txfclient[i]);
			this.pnlclient[i].add(this.btnclient[i]);
			this.pnlclient[i].add(new JLabel("Parameter:"));
			this.pnlclient[i].add(this.txfparams[i]);
			// ------------------------------------------------
			Vector<String> rowData = new Vector<String>(); // default
			rowData.add(playerNumber); // set position#
			((DefaultTableModel) this.statTable.getModel()).addRow(rowData);
		}
	}

	/**
	 * Sets the specific table header renderer.
	 *
	 * @param table
	 */
	private void setTableHeaderRenderer(JTable table) {
		final JTableHeader header = table.getTableHeader();
		final TableCellRenderer headerRenderer = header.getDefaultRenderer();

		header.setDefaultRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {

				JLabel c = (JLabel) headerRenderer
						.getTableCellRendererComponent(table, value,
								isSelected, hasFocus, row, column);

				if (column == 0) {
					c.setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					c.setHorizontalAlignment(SwingConstants.LEADING);
				}

				return c;
			}
		});
	}

	/**
	 * Prepares the test range.
	 */
	private boolean prepareTest() {

		this.testStarted = true;
		if (this.presFac.getLogicFacade().isGameActive()) {
			this.presFac.getContextDisplay().cancelCurrentGame();
		}

		IGuiPlugin selPlugin = getSelectedPlugin().getPlugin();

		// on-demand number checking -> here: no checking required
		this.numTest = new Integer(this.txfNumTest.getText());

		this.progressBar.setMaximum(this.numTest);
		this.progressBar.setValue(0);

		this.absoluteValues = new LinkedList<List<BigDecimal>>();

		for (JTextField element : this.txfclient) {
			this.absoluteValues.add(new LinkedList<BigDecimal>());
			File file = new File(element.getText());
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, this.lang
						.getProperty("dialog_test_error_path_msg"), this.lang
						.getProperty("dialog_test_error_path_title"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		// display the clients' positions and names
		MyTableModel model = (MyTableModel) this.statTable.getModel();
		for (int i = 0; i < this.txfclient.length; i++) {
			model.setValueAt(new Integer(i + 1), i, 0);
			String name = new File(this.txfclient[i].getText()).getName();
			// without file ext and with a number
			name = HelperMethods.getFilenameWithoutFileExt(name) + " "
					+ (i + 1);
			model.setValueAt(name, i, 1);
			for (int j = 0; j < selPlugin.getScoreDefinition().size() + 2; j++) {
				model.setValueAt(BigDecimal.ZERO, i, 2 + j); // set default 0
			}
		}
		this.statTable.validate();

		// start server
		boolean portInUse;
		final int START_PORT = SharedConfiguration.DEFAULT_PORT;
		this.freePort = START_PORT;
		do {
			portInUse = false;
			this.freePort++;
			try {
				this.presFac.getLogicFacade().startServer(this.freePort);
			} catch (BindException e) { // port in use
				if (this.freePort > START_PORT + 10) {
					if (JOptionPane
							.showConfirmDialog(
									null,
									this.lang
											.getProperty("dialog_test_error_retryPort_msg"),
									this.lang
											.getProperty("dialog_test_error_retryPort_title"),
									JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
						return false;
					}
				}
				portInUse = true;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), this.lang
						.getProperty("dialog_error_title"),
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} while (portInUse);

		// disable rendering
		selPlugin.setRenderContext(null);

		return true;
	}

	/**
	 * Starts a prepared test range.
	 *
	 * @param ascending
	 */
	protected void startNewTest() {

    logger.debug("FOCUS starting new test...");
    final ConnectingDialog connectionDialog = new ConnectingDialog(this);

    TestRangeDialog.this.curTest++;

    final int rotation = getRotation(TestRangeDialog.this.txfclient.length);

    logger.debug("Preparing slots for test {}", TestRangeDialog.this.curTest);
    final List<SlotDescriptor> slotDescriptors = prepareSlots(preparePlayerNames(), rotation);
    List<KIInformation> KIs = null;

    try {
      logger.debug("Preparing game");
      final IGamePreparation prep = prepareGame(getSelectedPlugin(), slotDescriptors);

      logger.debug("Preparing observer");
      addObsListeners(rotation, slotDescriptors, prep, connectionDialog);

      // only display message after the first round
      if (TestRangeDialog.this.curTest > 1) {
        addLogMessage(">>> " + TestRangeDialog.this.lang.getProperty("dialog_test_switch"));
      }

      logger.debug("Preparing clients");
      KIs = prepareClientProcesses(slotDescriptors, prep, rotation);
    } catch (IOException e) {
      e.printStackTrace();
      cancelTest(TestRangeDialog.this.lang.getProperty("dialog_test_msg_prepare"));
      return;
    }

    try {
      runClientProcesses(KIs);
    } catch (IOException e) {
      e.printStackTrace();
      cancelTest(TestRangeDialog.this.lang.getProperty("dialog_test_msg_run"));
      return;
    }

    logger.debug("FOCUS finished startNewTest");
    // show connecting dialog
    if (isActive()) {
      if (connectionDialog.showDialog() == JOptionPane.CANCEL_OPTION) {
        cancelTest(this.lang.getProperty("dialog_test_msg_cancel"));
      }
    }
	}

	private int getRotation(int playerCount) {
		return (this.curTest + 1) % playerCount;
	}

	private List<KIInformation> prepareClientProcesses(
			final List<SlotDescriptor> descriptors,
			final IGamePreparation prep, final int rotation) {
		final List<ISlot> slots = new ArrayList<ISlot>(prep.getSlots());
		final List<String> paths = prepareProcessPaths(rotation);

		final List<KIInformation> KIs = new ArrayList<KIInformation>();
		for (int i = 0; i < slots.size(); i++) {
			ISlot slot = slots.get(i);
			String path = paths.get(i);

			String[] slotParams = slot.asClient();
			// FIXME: refactoring: erst alle informationen sammel und dann nur
			// noch die infor d urchrotieren statt alles immer mit neuem
			// rotationsindex neu zu erstellen
			String paramLine = this.txfparams[(rotation + i) % slots.size()]
					.getText();
			paramLine = paramLine == null ? "" : paramLine;
			String[] lineParams = paramLine.split(" ");
			String[] params = new String[slotParams.length + lineParams.length];
			for (int j = 0; j < slotParams.length; j++) {
				params[j] = slotParams[j];
			}
			for (int j = 0; j < lineParams.length; j++) {
				params[j + slotParams.length] = lineParams[j];
			}

			KIs.add(new KIInformation(params, path));

			addLogMessage(descriptors.get(i).getDisplayName() + " "
					+ this.lang.getProperty("dialog_test_switchpos") + " " + (i + 1));
		}
		return KIs;
	}

	private List<String> prepareProcessPaths(int rotation) {
		final List<String> paths = new LinkedList<String>();

		for (int i = 0; i < this.txfclient.length; i++) {
			paths.add(this.txfclient[i].getText());
		}

		Collections.rotate(paths, rotation);

		return paths;
	}

	private void runClientProcesses(final List<KIInformation> KIs)
			throws IOException {
		if (KIs == null) {
			throw new IllegalArgumentException(
					"Parameter 'KIs' may not be null");
		}

		// start KI (intern) clients
		for (int i = 0; i < KIs.size(); i++) {
			KIInformation kinfo = KIs.get(i);

			String file = kinfo.getPath();
			String[] params = kinfo.getParameters();

			try {
				HelperMethods.exec(file, params);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, this.lang
						.getProperty("dialog_test_error_client_msg"), this.lang
						.getProperty("dialog_test_error_client_title"),
						JOptionPane.ERROR_MESSAGE);
				throw new IOException(e);
			} catch (UnsupportedFileExtensionException e) {
				JOptionPane.showMessageDialog(this, this.lang
						.getProperty("dialog_error_fileext_msg"), this.lang
						.getProperty("dialog_error_fileext_msg"),
						JOptionPane.ERROR_MESSAGE);
				throw new IOException(e);
			}
		}
	}

	private void addObsListeners(final int rotation,
			final List<SlotDescriptor> slotDescriptors,
			final IGamePreparation prep, final ConnectingDialog connectionDialog) {
		// get observer

		final GUIPluginInstance plugin = getSelectedPlugin();
		final List<SlotDescriptor> descriptors = slotDescriptors;
		this.obs = prep.getObserver();
		logger.debug("FOCUS got observer {}", this.obs);
		this.obs.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void onGameEnded(GameResult result, String gameResultString) {
        logger.debug("FOCUS onGameEnded called by thread " + Thread.currentThread().getId() + " "
            + Thread.currentThread().getName());
        logger.debug("game ended, result:\n{}", result);
        if (null != result) {

          addLogMessage(TestRangeDialog.this.lang.getProperty("dialog_test_end") + " " + TestRangeDialog.this.curTest + "/" + TestRangeDialog.this.numTest);
          addLogMessage(gameResultString); // game over information
          // purpose
          updateStatistics(rotation, result);
          // update progress bar
          TestRangeDialog.this.progressBar.setValue(TestRangeDialog.this.progressBar.getValue() + 1);

          // create replay file name
          String replayFilename = null;
          Collections.rotate(descriptors, -rotation); // Undo rotation
          boolean winner = false;
          for (IPlayer player : result.getWinners()) {
            if (player.getDisplayName().equals(descriptors.get(0).getDisplayName())) {
              winner = true;
            }
          }
          // Draw counts as win
          if (result.getWinners().size() == 0) {
            winner = true;
          }
          boolean saveReplay = false;
          if (!result.isRegular() && GUIConfiguration.instance().saveErrorGames()) {
            saveReplay = true;
          } else if (result.isRegular()) {
            if (GUIConfiguration.instance().saveWonGames() && winner) {
              saveReplay = true;
            }
            if (GUIConfiguration.instance().saveLostGames() && !winner) {
              saveReplay = true;
            }
          }
          if (saveReplay) {
            replayFilename = HelperMethods.generateReplayFilename(plugin, slotDescriptors);
            try {
              TestRangeDialog.this.obs.saveReplayToFile(replayFilename);
              addLogMessage(TestRangeDialog.this.lang.getProperty("dialog_test_log_replay"));
            } catch (IOException e) {
              e.printStackTrace();
              addLogMessage(TestRangeDialog.this.lang.getProperty("dialog_test_log_replay_error"));
            }
          }
        }

        logger.debug("FOCUS Observer await game end reached {} id: {}", Thread.currentThread().getName(),
            Thread.currentThread().getId());
        try {
          // we assume that this await is called by another thread than the
          // main one!
          TestRangeDialog.this.gameEndReached.await();
        } catch (InterruptedException | BrokenBarrierException e) {
          logger.error("Exception while in game end callback", e);
        }
			}
		});
		this.obs.addNewTurnListener(new INewTurnListener() {
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
		logger.debug("adding ready listener to {}", this.obs);
		this.obs.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				logger.debug("FOCUS got ready event");
        TestRangeDialog.this.obs.start();
				connectionDialog.close();
			}
		});
	}

	private List<SlotDescriptor> prepareSlots(final List<String> playerNames,
			int rotation) {
		final List<SlotDescriptor> descriptors = new LinkedList<>();

		for (String playerName : playerNames) {
			descriptors.add(new SlotDescriptor(playerName, !this.ckbDebug
					.isSelected()));
		}

		Collections.rotate(descriptors, rotation);

		return descriptors;
	}

	private List<String> preparePlayerNames() {
		// get player names
		final List<String> playerNames = new LinkedList<String>();

		for (int i = 0; i < this.txfclient.length; i++) {
			String path = this.txfclient[i].getText();
			String clientName = HelperMethods
					.getFilenameWithoutFileExt(new File(path).getName())
					+ " " + (i + 1);
			playerNames.add(clientName);
		}

		return playerNames;
	}

	/**
	 * Prepares a new game with the given parameters.
	 *
	 * @param selPlugin
	 * @param descriptors
	 * @return
	 * @throws IOException
	 *             (also displays an error message)
	 */
	private IGamePreparation prepareGame(GUIPluginInstance selPlugin,
			final List<SlotDescriptor> descriptors) throws IOException {
		try {
			IGamePreparation prep = selPlugin.getPlugin()
					.prepareBackgroundGame(
							DEFAULT_HOST,
							this.freePort,
							descriptors.toArray(new SlotDescriptor[descriptors
									.size()]));
			logger.debug("prepareGame for replays was called focus: {}", isFocused());
			return prep;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, this.lang
					.getProperty("dialog_test_error_network_msg"), this.lang
					.getProperty("dialog_test_error_network_title"),
					JOptionPane.ERROR_MESSAGE);
			throw e;
		}
	}

	/**
	 * Updates the statistics table
	 *
	 * @param rotation
	 * @param result
	 */
	protected void updateStatistics(final int rotation, final GameResult result) {
		MyTableModel model = (MyTableModel) this.statTable.getModel();

		// display wins/losses etc.
		for (int i = 0; i < result.getScores().size(); i++) {
			int statRow = Math.abs(rotation - i);
			PlayerScore curPlayer = result.getScores().get(i);

			List<BigDecimal> stats = curPlayer.getValues();
			for (int j = 0; j < stats.size(); j++) {
				List<BigDecimal> absVals = this.absoluteValues.get(statRow);
				BigDecimal newStat = stats.get(j);
				if (absVals.size() <= j) {
					absVals.add(new BigDecimal(0));
				}
				//BigDecimal old = (BigDecimal) model.getValueAt(statRow, j + 2);
				BigDecimal abs = absVals.get(j);

				ScoreAggregation action = result.getDefinition().get(j)
						.getAggregation();
				switch (action) {
				case SUM:
					newStat = abs.add(newStat);
					absVals.set(j, newStat);
					break;
				case AVERAGE:
					// restore old absolute value
					//old = old.multiply(BigDecimal.valueOf(curTest - 1));
					// add newStat to absolute value
					newStat = abs.add(newStat);
					absVals.set(j, newStat);
					// divide with curTest (rounded down)
					newStat = newStat.divideToIntegralValue(BigDecimal
							.valueOf(this.curTest));
					break;
				default:
					throw new RuntimeException("Unknown aggregation type ("
							+ action + ")");
				}
				// set to model
				model.setValueAt(newStat, statRow, j + 2);
			}

			// display invalid, crashed
			switch (curPlayer.getCause()) {
			case REGULAR:
				break;
			case SOFT_TIMEOUT:
			case RULE_VIOLATION:
				int invalidCol = model.getColumnCount() - 2;
				BigDecimal oldValue = (BigDecimal) model.getValueAt(statRow,
						invalidCol);
				model.setValueAt(oldValue.add(BigDecimal.ONE), statRow,
						invalidCol);
				break;
			case HARD_TIMEOUT:
			case LEFT:
				int crashedCol = model.getColumnCount() - 1;
				oldValue = (BigDecimal) model.getValueAt(statRow, crashedCol);
				model.setValueAt(oldValue.add(BigDecimal.ONE), statRow,
						crashedCol);
				break;
			case UNKNOWN:
				final String player = (String) model.getValueAt(statRow, 1);
				addLogMessage(player + " "
						+ this.lang.getProperty("dialog_test_gamecause_unknown"));
				break;
			default:
				throw new RuntimeException(
						"Unknown or unimplemented game cause.");
			}
		}
	}

	/**
	 * Cancels the active test range.
	 */
	private void cancelTest(final String err_msg) {
		if (null != this.obs && !this.obs.isFinished()) {
			this.obs.cancel();
		}
		finishTest();
		addLogMessage(err_msg);
	}

	/**
	 * Stops the server and reset the button's status.
	 */
	private void finishTest() {
		if (this.testStarted) {
			stopServer();
		}
		updateGUI(true);
		this.testStarted = false;
	}

	/**
	 * Loads a client, i.e. opens a file choose dialog
	 *
	 * @param txf
	 */
	private void loadClient(JTextField txf) {
		final JFileChooser chooser = new JFileChooser(GUIConfiguration
				.instance().getTestDialogPath());
		chooser.setDialogTitle(this.lang.getProperty("dialog_test_dialog_title"));
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			txf.setText(f.getAbsolutePath());
			GUIConfiguration.instance().setTestDialogPath(f.getParent());
		}
	}

	private GUIPluginInstance getSelectedPlugin() {
		return this.plugins.get(this.cmbGameType.getSelectedIndex());
	}

	/**
	 * Closes the server.
	 */
	private void stopServer() {
		this.presFac.getLogicFacade().stopServer();
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
	  logger.debug("Textarea log message: {}", msg);
		if (this.cb_showLog.isSelected()) {
			this.txtarea.append(msg + "\n");
			this.txtarea.setCaretPosition(this.txtarea.getText().length());
		}
	}
}
