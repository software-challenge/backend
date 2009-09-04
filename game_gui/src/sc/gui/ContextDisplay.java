package sc.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.logic.save.GUIConfiguration;

@SuppressWarnings("serial")
public class ContextDisplay extends JPanel implements INewTurnListener {

	private static final String PATH_ICON_CANCEL = "/resource/stop.png";
	private static final String PATH_ICON_START = "/resource/player_play.png";
	private static final String PATH_ICON_PAUSE = "/resource/player_pause.png";
	private static final String PATH_ICON_TO_START = "/resource/go-first.png";
	private static final String PATH_ICON_TO_END = "/resource/go-last.png";
	private static final String PATH_ICON_BACK = "/resource/go-previous.png";
	private static final String PATH_ICON_NEXT = "/resource/go-next.png";

	private static final ImageIcon ICON_TO_START = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_TO_START));
	private static final ImageIcon ICON_TO_END = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_TO_END));
	private static final ImageIcon ICON_CANCEL = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_CANCEL));
	private static final ImageIcon ICON_START = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_START));
	private static final ImageIcon ICON_PAUSE = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_PAUSE));
	private static final ImageIcon ICON_BACK = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_BACK));
	private static final ImageIcon ICON_NEXT = new ImageIcon(
			ContextDisplay.class.getResource(PATH_ICON_NEXT));

	private final PresentationFacade presFac;
	private final Properties lang;
	private Timer timer;
	private final TimerTask nextTask;

	private JPanel gameField;
	private JPanel buttonBar;
	private JButton btn_pauseAndPlay;
	private JButton btn_toBegin;
	private JButton btn_toEnd;
	private JButton btn_back;
	private JButton btn_next;
	private JButton btn_cancel;
	private boolean started;
	private JScrollBar speedBar;

	/**
	 * Constructor
	 */
	public ContextDisplay() {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		this.timer = new Timer();
		this.nextTask = new TimerTask() {
			@Override
			public void run() {
				// presFac.getLogicFacade().getObservation().next();
				btn_next.doClick();
			}
		};
		createGUI();
	}

	/**
	 * Creates the <code>ContextDisplay</code>.
	 */
	private void createGUI() {
		this.setLayout(new BorderLayout());

		buttonBar = new JPanel();
		buttonBar.setBorder(BorderFactory.createEtchedBorder());

		btn_toBegin = new JButton(ICON_TO_START);
		btn_toBegin.setToolTipText(lang.getProperty("context_to_begin"));
		bindShortcut(btn_toBegin, KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK);
		btn_back = new JButton(ICON_BACK);
		btn_back.setToolTipText(lang.getProperty("context_back"));
		bindShortcut(btn_back, KeyEvent.VK_LEFT, 0);
		btn_pauseAndPlay = new JButton(ICON_START);
		btn_pauseAndPlay.setToolTipText(lang.getProperty("context_start"));
		btn_next = new JButton(ICON_NEXT);
		btn_next.setToolTipText(lang.getProperty("context_next"));
		bindShortcut(btn_next, KeyEvent.VK_RIGHT, 0);
		btn_toEnd = new JButton(ICON_TO_END);
		btn_toEnd.setToolTipText(lang.getProperty("context_to_end"));
		bindShortcut(btn_toEnd, KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK);
		btn_cancel = new JButton(ICON_CANCEL);
		btn_cancel.setToolTipText(lang.getProperty("context_cancel"));

		disableAllButtons(); // by default

		// set scrollbar to maximum by default
		int knobWidth = 30;
		speedBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, knobWidth, 0,
				100 + knobWidth);
		speedBar.setPreferredSize(new Dimension(200, 30));
		speedBar.setValue(GUIConfiguration.instance().getSpeedValue());
		speedBar.setEnabled(false); // false by default
		speedBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				stopTimer();
				GUIConfiguration.instance().setSpeedValue(e.getValue());

				System.out.println("speedbar: " + e.getValue());
				System.out.println("getVisibleAmount: "
						+ speedBar.getVisibleAmount());

				IObservation obs = presFac.getLogicFacade().getObservation();
				// may not start a paused or not created game
				if (!obs.isPaused()) {
					if (e.getValue() < speedBar.getMaximum()
							+ speedBar.getVisibleAmount()) {
						startTimer();
					} else {
						obs.unpause();
					}
				}
			}
		});
		speedBar.setVisible(false);// TODO

		btn_toBegin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().goToFirst();
			}
		});

		btn_toEnd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().goToLast();
			}
		});

		btn_back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().back();
			}
		});

		btn_pauseAndPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!started) {
					started = true;
					presFac.getLogicFacade().getObservation().start();
				} else if (presFac.getLogicFacade().getObservation().isPaused()) {
					presFac.getLogicFacade().getObservation().unpause();
				} else {
					presFac.getLogicFacade().getObservation().pause();
					stopTimer();
				}
			}
		});

		btn_next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().next();
			}
		});

		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presFac.getLogicFacade().getObservation().isFinished()
						|| JOptionPane.showConfirmDialog(null, lang
								.getProperty("context_dialog_cancel_msg"), lang
								.getProperty("context_dialog_cancel_title"),
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					presFac.getLogicFacade().getObservation().cancel();
					// FIXME call gameEnded, stopTimer()
					started = false;
					disableAllButtons();
					enableSpeedBar(false);

					btn_pauseAndPlay.setToolTipText(lang
							.getProperty("context_start"));
					btn_pauseAndPlay.setIcon(ICON_START);
					((ContextDisplay) presFac.getContextDisplay())
							.recreateGameField();
					// update status bar
					((StatusBar) presFac.getStatusBar()).setStatus(lang
							.getProperty("statusbar_status_nogame"));
				}
			}
		});

		JPanel regularButtonBar = new JPanel();
		regularButtonBar.add(btn_toBegin);
		regularButtonBar.add(btn_back);
		regularButtonBar.add(btn_pauseAndPlay);
		regularButtonBar.add(btn_next);
		regularButtonBar.add(btn_toEnd);
		regularButtonBar.add(speedBar);

		JPanel cancelButtonBar = new JPanel();
		cancelButtonBar.add(btn_cancel);

		buttonBar.setLayout(new BorderLayout(30, 30));
		buttonBar.add(regularButtonBar, BorderLayout.CENTER);
		buttonBar.add(cancelButtonBar, BorderLayout.LINE_END);

		recreateGameField();
	}

	/**
	 * Binds the standard action of the given <code>button</code> to the given
	 * <code>key</code>.
	 * 
	 * @param component
	 * @param key
	 * @param modifiers
	 */
	private void bindShortcut(final JButton button, int key, int modifiers) {
		Object actionMapKey = "shortcut1";
		button.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(key, modifiers), actionMapKey);
		button.getActionMap().put(actionMapKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				button.doClick();
			}
		});
	}

	/**
	 * Disables all 6 buttons in the control bar at the bottom.
	 */
	private void disableAllButtons() {
		btn_toBegin.setEnabled(false);
		btn_back.setEnabled(false);
		btn_pauseAndPlay.setEnabled(false);
		btn_next.setEnabled(false);
		btn_toEnd.setEnabled(false);
		btn_cancel.setEnabled(false);
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
		IObservation obs = presFac.getLogicFacade().getObservation();
		if (obs != null) {
			btn_cancel.setEnabled(!ended);
			syncButtonStates(obs);
			buttonBar.setVisible(true);
		}
	}

	@Override
	public void newTurn(int id, String info) {
		IObservation obs = presFac.getLogicFacade().getObservation();
		if (obs != null) {
			syncButtonStates(obs);
		}
	}

	private void syncButtonStates(IObservation obs) {
		btn_toBegin.setEnabled(!obs.isAtStart());
		btn_toEnd.setEnabled(!obs.isAtEnd());
		btn_back.setEnabled(obs.hasPrevious());
		btn_next.setEnabled(obs.hasNext());
		btn_pauseAndPlay.setEnabled(obs.canTogglePause());
		syncPauseAndPlayState(obs.isPaused());
	}

	/**
	 * Updates the tool tip and the icon.
	 * 
	 * @param paused
	 */
	private void syncPauseAndPlayState(boolean paused) {
		btn_pauseAndPlay.setToolTipText(paused ? lang
				.getProperty("context_start") : lang
				.getProperty("context_pause"));
		btn_pauseAndPlay.setIcon(paused ? ICON_START : ICON_PAUSE);
	}

	/**
	 * Enables the speed bar.
	 * 
	 * @param value
	 */
	public void enableSpeedBar(boolean value) {
		speedBar.setEnabled(value);
	}

	public void startTimer() {
		if (speedBar.isEnabled()) {
			// change timer interval/period
			long period = Math.round(5000 - (float) speedBar.getValue() * 5000
					/ speedBar.getMaximum());

			timer = new Timer();
			timer.schedule(nextTask, 0, period);
			System.out.println("Timer started (" + period + ")");
		}
	}

	public void stopTimer() {
		timer.cancel();
		System.out.println("timer canceled.");
	}
}
