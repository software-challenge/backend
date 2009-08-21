package sc.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.INewTurnListener;

@SuppressWarnings("serial")
public class ContextDisplay extends JPanel implements INewTurnListener {

	private static final String PATH_ICON_CANCEL = "/resource/cancel.png";
	private static final String PATH_ICON_START = "/resource/start.png";
	private static final String PATH_ICON_PAUSE = "/resource/pause.png";
	private static final String PATH_ICON_BACK = "/resource/back.png";
	private static final String PATH_ICON_NEXT = "/resource/next.png";

	private static final ImageIcon ICON_CANCEL = new ImageIcon(ContextDisplay.class
			.getResource(PATH_ICON_CANCEL));
	private static final ImageIcon ICON_START = new ImageIcon(ContextDisplay.class
			.getResource(PATH_ICON_START));
	private static final ImageIcon ICON_PAUSE = new ImageIcon(ContextDisplay.class
			.getResource(PATH_ICON_PAUSE));
	private static final ImageIcon ICON_BACK = new ImageIcon(ContextDisplay.class
			.getResource(PATH_ICON_BACK));
	private static final ImageIcon ICON_NEXT = new ImageIcon(ContextDisplay.class
			.getResource(PATH_ICON_NEXT));

	private final PresentationFacade presFac;
	private final Properties lang;
	private JPanel gameField;
	private JPanel buttonBar;
	private JButton btn_spGame;
	private JButton btn_back;
	private JButton btn_next;
	private JButton btn_cancel;
	private boolean started;

	public ContextDisplay() {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		createGUI();
	}

	private void createGUI() {
		this.setLayout(new BorderLayout());

		buttonBar = new JPanel();
		buttonBar.setBorder(BorderFactory.createEtchedBorder());

		// btn_back = new JButton(lang.getString("context_back"));
		// btn_cancel = new JButton(lang.getString("context_cancel"));
		// btn_spGame = new JButton(lang.getString("context_start"));
		// btn_next = new JButton(lang.getString("context_next"));

		btn_back = new JButton(ICON_BACK);
		btn_back.setToolTipText(lang.getProperty("context_back"));
		btn_cancel = new JButton(ICON_CANCEL);
		btn_cancel.setToolTipText(lang.getProperty("context_cancel"));
		btn_spGame = new JButton(ICON_START);
		btn_spGame.setToolTipText(lang.getProperty("context_start"));
		btn_next = new JButton(ICON_NEXT);
		btn_next.setToolTipText(lang.getProperty("context_next"));

		// disable by default
		btn_back.setEnabled(false);
		btn_cancel.setEnabled(false);
		btn_spGame.setEnabled(false);
		btn_next.setEnabled(false);

		btn_back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presFac.getLogicFacade().getObservation().hasPrevious()) {
					presFac.getLogicFacade().getObservation().back();
					btn_next.setEnabled(true);
					if (!presFac.getLogicFacade().getObservation().hasPrevious()) {
						btn_back.setEnabled(false);
					}
				}
			}
		});

		btn_spGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!started) {
					started = true;
					presFac.getLogicFacade().getObservation().start();
				} else if (presFac.getLogicFacade().getObservation().isPaused()) {
					presFac.getLogicFacade().getObservation().unpause();
				} else {
					presFac.getLogicFacade().getObservation().pause();
				}
			}
		});

		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().cancel();// FIXME MUST
																	// call
																	// gameEnded
				started = false;
				btn_back.setEnabled(false);
				btn_cancel.setEnabled(false);
				btn_spGame.setEnabled(false);
				btn_next.setEnabled(false);
				btn_spGame.setToolTipText(lang.getProperty("context_start"));
				btn_spGame.setIcon(ICON_START);
				((ContextDisplay) presFac.getContextDisplay()).recreateGameField();
				// update status bar
				((StatusBar) presFac.getStatusBar()).setStatus(lang
						.getProperty("statusbar_status_nogame"));
			}
		});

		btn_next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presFac.getLogicFacade().getObservation().hasNext()) {
					btn_next.setEnabled(false);
					presFac.getLogicFacade().getObservation().next();
				}
			}
		});

		buttonBar.setLayout(new BorderLayout(30, 30));

		JPanel regularButtonBar = new JPanel();
		regularButtonBar.add(btn_back);
		regularButtonBar.add(btn_spGame);
		regularButtonBar.add(btn_next);
		// regularButtonBar.add(new JSeparator(JSeparator.VERTICAL));

		JPanel cancelButtonBar = new JPanel();
		cancelButtonBar.add(btn_cancel);

		buttonBar.add(regularButtonBar, BorderLayout.CENTER);
		buttonBar.add(cancelButtonBar, BorderLayout.LINE_END);

		recreateGameField();
	}

	/**
	 * Recreates an empty game field panel and replaces it with the current game
	 * field. It also resets all other components on this panel in the old
	 * order.
	 * 
	 * @return the new game field panel
	 */
	public JPanel recreateGameField() {
		gameField = new JPanel();
		gameField.setBorder(BorderFactory.createEtchedBorder());

		this.removeAll();
		this.add(gameField, BorderLayout.CENTER);
		this.add(buttonBar, BorderLayout.PAGE_END);
		this.validate();

		return gameField;
	}

	/**
	 * Updates (enables/disables or shows/hides) the buttons, e.g. start game or
	 * next turn.
	 */
	public void updateButtonBar(boolean ended) {
		if (presFac.getLogicFacade().getObservation() != null) {
			if (ended) {
				btn_cancel.setEnabled(false);
				btn_spGame.setEnabled(false);
			} else {
				btn_cancel.setEnabled(true);
				btn_spGame.setEnabled(true);
				btn_back.setEnabled(false);
				btn_next.setEnabled(false);
			}
			buttonBar.setVisible(true);
		}
	}

	@Override
	public void newTurn(int id, String info) {
		IObservation obs = presFac.getLogicFacade().getObservation();
		if (obs != null) {
			btn_back.setEnabled(obs.hasPrevious());
			btn_next.setEnabled(obs.hasNext());
			btn_spGame.setToolTipText(obs.isPaused() ? lang.getProperty("context_start")
					: lang.getProperty("context_pause"));
			btn_spGame.setIcon(obs.isPaused() ? ICON_START : ICON_PAUSE);
		}
	}
}
