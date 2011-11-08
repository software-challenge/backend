package sc.gui.dialogs.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class CenteredTableCellRenderer extends DefaultTableCellRenderer implements
		TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7375210077755610734L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
				column);
		
		setHorizontalAlignment(JLabel.CENTER);
		
		return c;
	}
	
}
