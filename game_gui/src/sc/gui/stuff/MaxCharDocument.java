package sc.gui.stuff;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MaxCharDocument extends PlainDocument {

	private static final long serialVersionUID = 2424097754246336303L;
	private final int MAX_CHARS;

	public MaxCharDocument(final int max_characters) {
		super();
		this.MAX_CHARS = max_characters;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if (str == null || offs + str.length() > MAX_CHARS) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}
		super.insertString(offs, str, a);
	}
}
