package sc.gui.dialogs.renderer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BigFontTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -6344447207084441870L;
	private final Font font;
	
	public BigFontTableCellRenderer(Font font) {
		super();
		this.font = font;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);

		setFont(font);
		
		return c;
	}
}
