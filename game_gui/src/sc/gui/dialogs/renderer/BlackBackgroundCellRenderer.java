package sc.gui.dialogs.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class BlackBackgroundCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	private static final long serialVersionUID = -7046161718002270036L;
	private static final Color lightGray = new Color(225, 225, 225);

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

		if (isSelected) {
			setForeground(Color.BLACK); // visible color
		} else {
			setForeground(table.getForeground());
		}
		setBackground(lightGray);
		
		return c;
	}

}
