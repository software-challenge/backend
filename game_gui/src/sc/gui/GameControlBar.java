package sc.gui;

import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GameControlBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -411501977458671630L;

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

	public final JButton btn_play;
	public final JButton btn_pause;
	public final JButton btn_toBegin;
	public final JButton btn_toEnd;
	public final JButton btn_back;
	public final JButton btn_next;
	public final JButton btn_cancel;

	private final Properties lang;

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

		JPanel regularButtonBar = new JPanel();
		regularButtonBar.add(btn_toBegin);
		regularButtonBar.add(btn_back);
		regularButtonBar.add(btn_pause);
		regularButtonBar.add(btn_play);
		regularButtonBar.add(btn_next);
		regularButtonBar.add(btn_toEnd);

		JPanel cancelButtonBar = new JPanel();
		cancelButtonBar.add(btn_cancel);

		setLayout(new BorderLayout(30, 30));
		add(regularButtonBar, BorderLayout.CENTER);
		add(cancelButtonBar, BorderLayout.LINE_END);
	}

	public void setPaused(boolean paused) {
		this.btn_pause.setVisible(!paused);
		this.btn_play.setVisible(paused);
	}
}
