package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import sc.IGUIApplication;
import sc.common.HelperMethods;
import sc.common.UnsupportedFileExtensionException;
import sc.gui.ContextDisplay;
import sc.gui.PresentationFacade;
import sc.gui.SCMenuBar;
import sc.gui.StatusBar;
import sc.gui.dialogs.renderer.BigFontTableCellRenderer;
import sc.gui.dialogs.renderer.CenteredBlackBackgroundCellRenderer;
import sc.gui.dialogs.renderer.FilenameBlackBGCellRenderer;
import sc.gui.dialogs.renderer.MyComboBoxRenderer;
import sc.gui.stuff.KIInformation;
import sc.gui.stuff.MaxCharDocument;
import sc.guiplugin.interfaces.IGamePreparation;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.ISlot;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.logic.save.GUIConfiguration;
import sc.logic.save.Player;
import sc.plugin.GUIPluginInstance;
import sc.server.Configuration;
import sc.shared.GameResult;
import sc.shared.SlotDescriptor;

/**
 * 
 * @author chw
 * 
 */
@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	private static final String HOST_IP = "localhost";
	private static final int MAX_CHARS = 35;
	private static final float FONT_SIZE = 16;
	private static final Font font = new Font("Arial", Font.PLAIN, (int) FONT_SIZE);
	private static final int MAX_WIDTH = 800;

	private final PresentationFacade presFac;
	private final Properties lang;
	private final IGUIApplication root;

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
	private JTextField txfPort;
	private JLabel lblPort;
	private MyTableModel playersModel;
	
	private JPanel pnlLoadGame;
	private JPanel pnlLoadGameChoose;
	private JCheckBox chkLoadGame;
	private JTextField txfLoadGame;
	private JButton btnLoadGame;

	/**
	 * Constructor
	 * 
	 * @param root
	 * 
	 * @param frame
	 */
	public CreateGameDialog(final IGUIApplication root) {
		super();

		presFac = PresentationFacade.getInstance();
		lang = presFac.getLogicFacade().getLanguageData();
		this.root = root;
		createGUI();
	}

	/**
	 * Creates the dialog for creating a custom game.
	 */
	private void createGUI() {

		plugins = presFac.getLogicFacade().getAvailablePluginsSorted();
		final Vector<String> pluginNames = presFac.getLogicFacade().getPluginNames(
				plugins);

		// ---------------------------------------------------

		setLayout(new BorderLayout());

		pnlTable = new JPanel();
		pnlBottom = new JPanel();
		pnlBottom.setBorder(BorderFactory.createEtchedBorder());
		pnlBottom.setLayout(new BoxLayout(pnlBottom, BoxLayout.PAGE_AXIS));

		// ---------------------------------------------------

		pnlPref = new JPanel();
		pnlButtons = new JPanel();
		pnlLoadGame = new JPanel();
		pnlBottom.add(pnlPref);
		pnlBottom.add(pnlLoadGame);
		pnlBottom.add(pnlButtons);

		// ---------------------------------------------------
		
		pnlLoadGame.setLayout(new FlowLayout());
		
		pnlLoadGameChoose = new JPanel();
		
		chkLoadGame = new JCheckBox(lang.getProperty("dialog_create_load_game_check"));
		chkLoadGame.setFont(chkLoadGame.getFont().deriveFont(FONT_SIZE));
		chkLoadGame.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				//pnlLoadGameChoose.setVisible(chkLoadGame.isSelected());
				boolean en = chkLoadGame.isSelected();
				btnLoadGame.setEnabled(en);
				txfLoadGame.setEnabled(en);
			}
		});
		
		btnLoadGame = new JButton(lang.getProperty("dialog_create_load_game_choose"));
		btnLoadGame.setEnabled(false);
		btnLoadGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser gameFileChooser = new JFileChooser();
				switch (gameFileChooser.showOpenDialog(null)) {
				case JFileChooser.APPROVE_OPTION:
					txfLoadGame.setText(gameFileChooser.getSelectedFile().getAbsolutePath());
					break;
				case JFileChooser.CANCEL_OPTION:
					// Do nothing?
					break;
				}
			}
		});
		
		txfLoadGame = new JTextField();
		txfLoadGame.setEnabled(false);
		txfLoadGame.setColumns(MAX_CHARS);
		
		pnlLoadGame.add(chkLoadGame);
		pnlLoadGame.add(pnlLoadGameChoose);
		pnlLoadGameChoose.add(txfLoadGame);
		pnlLoadGameChoose.add(btnLoadGame);
		
		
		// ---------------------------------------------------

		// TODO for future
		ckbDim = new JCheckBox(lang.getProperty("dialog_create_pref_dim"));
		ckbDim.setFont(ckbDim.getFont().deriveFont(FONT_SIZE));
		ckbDim.setToolTipText("");

		ckbDebug = new JCheckBox(lang.getProperty("dialog_create_pref_debug"));
		ckbDebug.setFont(ckbDebug.getFont().deriveFont(FONT_SIZE));
		ckbDebug.setToolTipText(lang.getProperty("dialog_create_pref_debug_hint"));
		ckbDebug.setSelected(GUIConfiguration.instance().getConfigCreateGameDialog()
				.isTimeLimit());

		txfPort = new JTextField(5);
		txfPort.setFont(txfPort.getFont().deriveFont(FONT_SIZE));
		txfPort.setText(String.valueOf(GUIConfiguration.instance()
				.getConfigCreateGameDialog().getPort()));

		lblPort = new JLabel(lang.getProperty("dialog_create_pref_port"));
		lblPort.setFont(lblPort.getFont().deriveFont(FONT_SIZE));
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
		combPlugins.setFont(combPlugins.getFont().deriveFont(FONT_SIZE));
		combPlugins.setSelectedItem(GUIConfiguration.instance()
				.getConfigCreateGameDialog().getGameType());
		pnlLeft.add(combPlugins);

		// ---------------------------------------------------

		// add columns
		playersModel = new MyTableModel();
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_pos"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_name"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_plytype"));
		playersModel.addColumn(lang.getProperty("dialog_create_tbl_filename"));

		tblPlayers = new JTable(playersModel);
		tblPlayers.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		tblPlayers.setRowHeight(40);

		// set bigger width of table
		Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int newWidth = (int) Math.round(0.7 * screen.width);
		if (newWidth > MAX_WIDTH) {
			newWidth = MAX_WIDTH;
		}
		tblPlayers.setPreferredScrollableViewportSize(new Dimension(newWidth, tblPlayers
				.getPreferredScrollableViewportSize().height));

		// set single selection on one cell
		tblPlayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// don't let the user change the columns' order or width
		tblPlayers.getTableHeader().setReorderingAllowed(false);
		tblPlayers.getTableHeader().setResizingAllowed(false);
		// add rows (default)
		addRows(tblPlayers);

		// -----------------------------------------------------------

		// combobox content
		final Vector<String> cmbItems = new Vector<String>();
		cmbItems.add(lang.getProperty("dialog_create_plyType_human"));
		cmbItems.add(lang.getProperty("dialog_create_plyType_ki_intern"));
		cmbItems.add(lang.getProperty("dialog_create_plyType_ki_extern"));

		// especially set big font
		setTableHeaderFontSize(tblPlayers, font.getSize2D());
		setTableCellEditing(tblPlayers);
		setTableColumnRendering(tblPlayers, font, cmbItems);

		// only a max. of characters
		JTextField tfName = new JTextField();
		tfName.setDocument(new MaxCharDocument(MAX_CHARS));
		tfName.setFont(font);

		// set attributes of each column
		tblPlayers.getColumnModel().getColumn(0).setMinWidth(0);
		tblPlayers.getColumnModel().getColumn(0).setMaxWidth(100);
		tblPlayers.getColumnModel().getColumn(0).setPreferredWidth(70);
		tblPlayers.getColumnModel().getColumn(1).setCellEditor(
				new DefaultCellEditor(tfName));
		tblPlayers.getColumnModel().getColumn(2).setCellEditor(
				new MyComboBoxEditor(cmbItems));

		// fit the height of the scroll pane to the size of the table's rows
		JScrollPane scroll = new JScrollPane(tblPlayers);
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width,
				(tblPlayers.getRowCount() + 1) * tblPlayers.getRowHeight() + 2));

		pnlTable.add(scroll);

		// ---------------------------------------------------

		/* okButton */
		final JButton okButton = new JButton(lang.getProperty("dialog_create_create"));
		okButton.setFont(okButton.getFont().deriveFont(FONT_SIZE));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkLoadGame.isSelected()) {
					Configuration.set("loadGameFile", txfLoadGame.getText());
				} else {
					Configuration.set("loadGameFile", null);
				}
				
				createGame(playersModel);
			}
		});

		/* cancelButton */
		JButton cancelButton = new JButton(lang.getProperty("dialog_create_cancel"));
		cancelButton.setFont(cancelButton.getFont().deriveFont(FONT_SIZE));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		pnlRight.add(okButton);
		pnlRight.add(cancelButton);

		// ---------------------------------------------------

		// add components
		this.add(pnlTable, BorderLayout.CENTER);
		this.add(pnlBottom, BorderLayout.PAGE_END);
		// set dialog preferences
		setTitle(lang.getProperty("dialog_create_title"));
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});
	}

	/**
	 * Saves the dialog settings and disposes itself.
	 */
	private void closeDialog() {
		GUIConfiguration.instance().getConfigCreateGameDialog().setGameType(
				(String) combPlugins.getSelectedItem());
		GUIConfiguration.instance().getConfigCreateGameDialog().setPort(
				Integer.parseInt(txfPort.getText()));
		GUIConfiguration.instance().getConfigCreateGameDialog().setTimeLimit(
				ckbDebug.isSelected());

		final List<Player> players = GUIConfiguration.instance()
				.getConfigCreateGameDialog().getPlayers();
		players.clear();
		for (int i = 0; i < playersModel.getRowCount(); i++) {
			final Player player = new Player();
			player.name = (String) playersModel.getValueAt(i, 1);
			player.playerType = (String) playersModel.getValueAt(i, 2);
			player.filename = (String) playersModel.getValueAt(i, 3);
			players.add(player);
		}

		System.out.println("Dialog settings saved.");
		// dispose dialog
		dispose();
	}

	/**
	 * 
	 * @param table
	 */
	private void setTableCellEditing(final JTable table) {
		// big size for editing
		JTextField tf_BigSize = new JTextField();
		tf_BigSize.setFont(font);

		table.setCellEditor(new DefaultCellEditor(tf_BigSize));
	}

	/**
	 * Sets the cell font size of the given <code>table</code> to
	 * <code>fontSize</code>.
	 * 
	 * @param table
	 * @param font
	 * @param items
	 */
	private void setTableColumnRendering(final JTable table, final Font font,
			final Vector<String> items) {
		table.getColumnModel().getColumn(0).setCellRenderer(
				new CenteredBlackBackgroundCellRenderer(font));
		table.getColumnModel().getColumn(1).setCellRenderer(
				new BigFontTableCellRenderer(font));
		table.getColumnModel().getColumn(2).setCellRenderer(
				new MyComboBoxRenderer(items, font));
		table.getColumnModel().getColumn(3).setCellRenderer(
				new FilenameBlackBGCellRenderer(font));
	}

	/**
	 * Sets the header font size of the given <code>table</code> to
	 * <code>fontSize</code>.
	 * 
	 * @param table
	 * @param fontSize
	 */
	private void setTableHeaderFontSize(final JTable table, final float fontSize) {
		JTableHeader header = table.getTableHeader();

		final Font newFont = header.getFont().deriveFont(fontSize);
		final TableCellRenderer headerRenderer = header.getDefaultRenderer();

		header.setDefaultRenderer(new TableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {

				Component comp = headerRenderer.getTableCellRendererComponent(table,
						value, isSelected, hasFocus, row, column);
				comp.setFont(newFont); // set size
				return comp;
			}
		});

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

		if (presFac.getLogicFacade().isGameActive())
			contextPanel.cancelCurrentGame();

		// start server
		try {
			presFac.getLogicFacade().startServer(port);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, lang
					.getProperty("dialog_create_error_port_blocked_msg"), lang
					.getProperty("dialog_create_error_port_blocked_title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), lang
					.getProperty("dialog_error_title"), JOptionPane.ERROR_MESSAGE);
		}

		// set render context
		boolean threeDimensional = false; // TODO for future
		selPlugin.getPlugin().setRenderContext(contextPanel.recreateGameField(),
				threeDimensional);

		final List<SlotDescriptor> descriptors = new ArrayList<SlotDescriptor>(model
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
		
		final ConnectingDialog connDial = new ConnectingDialog();

		// set observation
		IObservation observer = prep.getObserver();
		prepareObserver(observer, contextPanel, connDial, descriptors);
		presFac.getLogicFacade().setObservation(observer);

		final List<KIInformation> KIs = new ArrayList<KIInformation>();
		prepareSlots(KIs, prep, model, observer);
		executeClients(KIs, observer);

		// show connecting dialog
		if (connDial.showDialog() == JOptionPane.CANCEL_OPTION) {
			observer.cancel();
			cancelGameCreation(observer);
		} else {
			// add game specific info item in menu bar
			((SCMenuBar) presFac.getMenuBar()).setGameSpecificInfo(selPlugin
					.getDescription().name(), selPlugin.getVersion(), selPlugin
					.getPlugin().getPluginImage(), selPlugin.getPlugin().getPluginIcon(),
					selPlugin.getPlugin().getPluginInfoText(), selPlugin.getDescription()
							.author(), selPlugin.getPlugin().getPluginYear());
			// update status bar
			StatusBar statusBar = ((StatusBar) presFac.getStatusBar());
			statusBar.setStatus(lang.getProperty("statusbar_status_currentgame") + " "
					+ selPlugin.getDescription().name());
			// enable speed bar
			// contextPanel.enableSpeedBar(true); //TODO
			// set minimum size
			root.setMinimumGameSize(selPlugin.getPlugin().getMinimumSize());
			// close dialog
			closeDialog();
		}

	}

	/**
	 * Executes the given <code>KIs</code>
	 * 
	 * @param is
	 */
	private void executeClients(final List<KIInformation> KIs, final IObservation observer) {
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
	}

	/**
	 * Prepares the slots.
	 * 
	 * @param is
	 * @param prep
	 * @param model
	 */
	private void prepareSlots(final List<KIInformation> KIs, final IGamePreparation prep,
			final DefaultTableModel model, final IObservation observer) {
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
					JOptionPane.showMessageDialog(null, lang
							.getProperty("dialog_create_error_path_msg"), lang
							.getProperty("dialog_create_error_path_title"),
							JOptionPane.ERROR_MESSAGE);
					cancelGameCreation(observer);
					return;
				}
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
	}

	/**
	 * Prepares the given <code>observer</code>.
	 * 
	 * @param observer
	 */
	private void prepareObserver(final IObservation observer,
			final ContextDisplay contextPanel, final ConnectingDialog connDial,
			final List<SlotDescriptor> descriptors) {
		observer.addReadyListener(new IReadyListener() {
			@Override
			public void ready() {
				connDial.close();
				contextPanel.updateButtonBar(false);
				presFac.getLogicFacade().setGameActive(true);
			}
		});

		observer.addGameEndedListener(new IGameEndedListener() {
			@Override
			public void onGameEnded(GameResult result, String gameResultString) {
				System.out.println("Game ended.");

				presFac.getLogicFacade().stopServer();
				presFac.getLogicFacade().setGameActive(false);

				contextPanel.updateButtonBar(true);

				// generate replay filename
				String replayFilename = HelperMethods.generateReplayFilename(descriptors);
				// save replay
				try {
					observer.saveReplayToFile(replayFilename);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, lang
							.getProperty("dialog_create_error_replay_msg"), lang
							.getProperty("dialog_create_error_replay_title"),
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

		observer.addNewTurnListener(contextPanel);
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

		List<Player> players = null;
		final String gameTypeName = GUIConfiguration.instance()
				.getConfigCreateGameDialog().getGameType();
		/*
		 * check if the selected plugin is the one which information are stored
		 * of.
		 */
		if (selPlugin.getDescription().name().equals(gameTypeName)) {
			players = GUIConfiguration.instance().getConfigCreateGameDialog()
					.getPlayers();
		}

		for (int i = 0; i < selPlugin.getPlugin().getMinimalPlayerCount(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(new Integer(i + 1));
			if (players != null) {
				Player player = players.get(i);
				rowData.add(player.name);
				rowData.add(player.playerType);
				rowData.add(player.filename);
			} else {
				rowData.add(lang.getProperty("dialog_create_player") + " " + (i + 1));
				rowData.add(lang.getProperty("dialog_create_plyType_human")); // default
				rowData.add("-");
			}
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

			switch (chooser.showOpenDialog(null)) {
			case JFileChooser.APPROVE_OPTION:
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
			return (0 != col) && (col != 3);
		}

	}

	public class MyComboBoxEditor extends DefaultCellEditor implements ItemListener {
		public MyComboBoxEditor(Vector<String> items) {
			super(new JComboBox(items));

			JComboBox cbox = (JComboBox) getComponent();
			cbox.setEditable(false);
			cbox.addItemListener(this);
			cbox.setFont(font);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			JComboBox cbox = (JComboBox) getComponent();
			/**
			 * Only react on opening (i.e. having the focus), not additionally
			 * on closing the roll menu
			 */
			int row = tblPlayers.rowAtPoint(cbox.getLocation());
			if (e.getStateChange() == ItemEvent.SELECTED && cbox.hasFocus()) {
				// int row =
				// tblPlayers.convertRowIndexToModel(tblPlayers.getSelectedRow
				// ());
				updatePlayerTable(cbox, tblPlayers.convertRowIndexToModel(row));
			}
		}
	}
}
