package sc.gui.dialogs.renderer;

import java.awt.Component;
import java.io.File;

import javax.swing.JTable;

import sc.common.HelperMethods;

/**
 * Displays only the filename of the whole file path stored in the model.
 * 
 * @author chw
 * 
 */
public class FilenameBlackBGCellRenderer extends BlackBackgroundCellRenderer {

	private static final long serialVersionUID = 7977514938815958506L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

		File f = new File((String) value);
		if (f.exists()) {
			String clientname = f.getName();
			clientname = HelperMethods.getFilenameWithoutFileExt(clientname);
			// set filename
			setText(clientname);
		}

		return c;
	}
}
