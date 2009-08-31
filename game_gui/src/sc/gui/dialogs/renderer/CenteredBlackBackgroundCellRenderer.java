package sc.gui.dialogs.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

public class CenteredBlackBackgroundCellRenderer extends BlackBackgroundCellRenderer {

	private static final long serialVersionUID = 7276570948183124841L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		
		setHorizontalAlignment(JLabel.CENTER);
		
		return c;
	}
}
