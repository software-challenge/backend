package sc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.LoggerFactory;

import sc.common.HelperMethods;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class GameControlBar extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -411501977458671630L;

	private static final int INITIAL_SPEED = 10;

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

	private static final boolean SHOW_SERVER_CONTROL_BUTTONS = false;

	public final JButton btn_play;
	public final JButton btn_pause;
	public final JButton btn_toBegin;
	public final JButton btn_toEnd;
	public final JButton btn_back;
	public final JButton btn_next;
	public final JButton btn_cancel;

	private final Properties lang;

	private final Timer stepTimer;

	public final JSlider stepSpeed;

	private final JButton stepStartButton;

	private final JButton stepStopButton;

	public GameControlBar() {
		setBorder(BorderFactory.createEtchedBorder());

		lang = PresentationFacade.getInstance().getLogicFacade()
				.getLanguageData();
		btn_toBegin = new JButton(ICON_TO_START);
		btn_toBegin.setToolTipText(lang.getProperty("context_to_begin"));
		btn_back = new JButton(ICON_BACK);
		btn_back.setToolTipText(lang.getProperty("context_back"));
		btn_play = new JButton(ICON_START);
		btn_play.setToolTipText(lang.getProperty("context_start"));
		btn_next = new JButton(ICON_NEXT);
		btn_next.setToolTipText(lang.getProperty("context_next"));
		btn_toEnd = new JButton(ICON_TO_END);
		btn_toEnd.setToolTipText(lang.getProperty("context_to_end"));
		btn_cancel = new JButton(ICON_CANCEL);
		btn_cancel.setToolTipText(lang.getProperty("context_cancel"));
		btn_pause = new JButton(ICON_PAUSE);
		btn_pause.setToolTipText(lang.getProperty("context_pause"));
		btn_pause.setVisible(false);

		stepTimer = new Timer(1000, this);

		JPanel stepperBar = new JPanel();
		stepSpeed = new JSlider(0, 100, INITIAL_SPEED);
		stepSpeed.setMajorTickSpacing(50);
		stepSpeed.setMinorTickSpacing(25);
		stepSpeed.setPaintTicks(true);

		Dictionary<Integer, JLabel> sliderLabels = new Hashtable<Integer, JLabel>();
		sliderLabels.put(0, new JLabel(HelperMethods
				.millisecondsToString(HelperMethods.roundInteger(
						getLogarithmicSliderValue(0), 2))));
		sliderLabels.put(50, new JLabel(HelperMethods
				.millisecondsToString(HelperMethods.roundInteger(
						getLogarithmicSliderValue(50), 2))));
		sliderLabels.put(100, new JLabel(HelperMethods
				.millisecondsToString(HelperMethods.roundInteger(
						getLogarithmicSliderValue(100), 2))));
		stepSpeed.setLabelTable(sliderLabels);
		stepSpeed.setPaintLabels(true);

		stepStartButton = new JButton(ICON_START);
		stepStopButton = new JButton(ICON_PAUSE);

		stepperBar.add(stepStartButton);
		stepperBar.add(stepStopButton);
		stepperBar.add(stepSpeed);

		JPanel regularButtonBar = new JPanel();
		regularButtonBar.add(btn_toBegin);
		regularButtonBar.add(btn_back);
		regularButtonBar.add(btn_next);
		regularButtonBar.add(btn_toEnd);

		if (SHOW_SERVER_CONTROL_BUTTONS) {
			regularButtonBar.add(btn_pause);
			regularButtonBar.add(btn_play);
		}

		JPanel cancelButtonBar = new JPanel();
		cancelButtonBar.add(btn_cancel);

		FormLayout layout = new FormLayout(
				"pref, left:100dlu:grow, pref, center:100dlu:grow, pref, right:100dlu:grow, pref",
				"pref, min, pref");
		setLayout(layout);

		CellConstraints cc = new CellConstraints();
		add(stepperBar, cc.xy(2, 2, "left, center"));
		add(regularButtonBar, cc.xy(4, 2, "center, center"));
		add(cancelButtonBar, cc.xy(6, 2, "right, center"));

		bindHandlers();
	}

	private void bindHandlers() {
		stepSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				int delay = getLogarithmicSliderValue();
				LoggerFactory.getLogger(GameControlBar.class).debug(
						"Setting Speed to {}", delay);
				stepTimer.setDelay((int) delay);
				stepTimer.setInitialDelay((int) delay);
			}
		});

		stepSpeed.setValue(INITIAL_SPEED);

		ActionListener stepToggler = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setStepping(!stepTimer.isRunning());
			}
		};

		stepStartButton.addActionListener(stepToggler);
		stepStopButton.addActionListener(stepToggler);

		setStepping(false);
	}

	public void setPaused(boolean paused) {
		if (SHOW_SERVER_CONTROL_BUTTONS) {
			this.btn_pause.setVisible(!paused);
			this.btn_play.setVisible(paused);
		}
	}

	private int getLogarithmicSliderValue() {
		return getLogarithmicSliderValue(stepSpeed.getValue());
	}

	private int getLogarithmicSliderValue(long value) {
		return (int) getLogarithmicSliderValue(value, stepSpeed.getMinimum(),
				stepSpeed.getMaximum(), 200, 5000);
	}

	private double getLogarithmicSliderValue(long value, long srcMin,
			long srcMax, long destMin, long destMax) {
		double min = Math.log(destMin);
		double max = Math.log(destMax);

		double scale = (max - min) / (srcMax - srcMin);
		return Math.exp(min + scale * (value - srcMin));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		btn_next.doClick();
	}

	public void disable() {
		btn_toBegin.setEnabled(false);
		btn_back.setEnabled(false);
		btn_pause.setEnabled(false);
		btn_play.setEnabled(false);
		btn_next.setEnabled(false);
		btn_toEnd.setEnabled(false);

		setActive(false);
		setStepping(false);
	}

	/**
	 * 
	 * @param active
	 *            True, if there is a loaded game
	 */
	protected void setActive(boolean active) {
		btn_cancel.setEnabled(active);
		stepSpeed.setEnabled(active);
		stepStartButton.setEnabled(active);
		stepStopButton.setEnabled(active);

		if (!active) {
			setStepping(false);
		}
	}

	protected void setStepping(boolean active) {
		if (!active) {
			stepTimer.stop();
		} else {
			stepTimer.start();
		}

		stepStartButton.setVisible(!active);
		stepStopButton.setVisible(active);
	}
	
	public void setStepSpeed(int speed) {
		stepSpeed.setValue(speed);
	}
}
