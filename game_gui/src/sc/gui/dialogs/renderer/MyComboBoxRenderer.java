package sc.gui.dialogs.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {

	private static final long serialVersionUID = 5325899337721967794L;
	private final Font font;

	public MyComboBoxRenderer(Vector<String> items, Font font) {
		super(items);
		this.font = font;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			super.setForeground(Color.BLACK); // visible color
			super.setBackground(table.getBackground());	// no selection background
		} else {
			super.setForeground(table.getForeground());
			super.setBackground(table.getBackground());
		}
		
		setFont(font);
		
		// Select the current value
		setSelectedItem(value);
		return this;
	}
}
