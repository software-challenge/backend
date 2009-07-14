package sc.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ContextDisplay extends JPanel {

	private final PresentationFacade presFac;
	private final ResourceBundle lang;
	private JPanel gameField;
	private JPanel buttonBar;
	private JButton btn_spGame;
	private JButton btn_back;
	private JButton btn_next;
	private JButton btn_cancel;
	private boolean started;
	private boolean playing;

	public ContextDisplay() {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		createGUI();
	}

	private void createGUI() {
		this.setLayout(new BorderLayout());

		gameField = new JPanel();
		gameField.setBorder(BorderFactory.createEtchedBorder());
		buttonBar = new JPanel();
		buttonBar.setBorder(BorderFactory.createEtchedBorder());

		btn_back = new JButton(lang.getString("context_back"));
		btn_cancel = new JButton(lang.getString("context_cancel"));
		btn_spGame = new JButton(lang.getString("context_start"));
		btn_next = new JButton(lang.getString("context_next"));

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
				}
			}
		});

		btn_spGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!started) {
					presFac.getLogicFacade().getObservation().start();
					btn_spGame.setText(lang.getString("context_pause"));
					btn_cancel.setEnabled(true);
					started = true;
					playing = true;
				} else if (playing) {
					presFac.getLogicFacade().getObservation().pause();
					btn_spGame.setText(lang.getString("context_unpause"));
					btn_back.setEnabled(true);
					playing = false;
				} else {
					presFac.getLogicFacade().getObservation().unpause();
					btn_spGame.setText(lang.getString("context_pause"));
					btn_back.setEnabled(false);
					playing = true;
				}
			}
		});

		btn_cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presFac.getLogicFacade().getObservation().cancel();
				started = false;
				btn_cancel.setEnabled(false);
				btn_next.setEnabled(false);
				btn_spGame.setText(lang.getString("context_start"));
			}
		});

		btn_next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presFac.getLogicFacade().getObservation().hasNext()) {
					presFac.getLogicFacade().getObservation().next();
					btn_back.setEnabled(true);
				}
			}
		});

		// buttonBar.add(new JSeparator(JSeparator.HORIZONTAL));
		buttonBar.add(btn_back);
		buttonBar.add(btn_spGame);
		buttonBar.add(btn_cancel);
		buttonBar.add(btn_next);

		this.add(gameField, BorderLayout.CENTER);
		this.add(buttonBar, BorderLayout.PAGE_END);
	}

	/**
	 * Returns the panel where the game field is drawn to.
	 * 
	 * @return
	 */
	public JPanel getGameField() {
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
			} else {
				btn_spGame.setEnabled(true);
			}
			buttonBar.setVisible(true);
		}
	}

}
