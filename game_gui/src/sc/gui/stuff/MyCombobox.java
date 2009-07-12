package sc.gui.stuff;

import java.util.Vector;

import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class MyCombobox extends JComboBox {

	private int rowIndex;
	
	public MyCombobox(Vector<?> items) {
		super(items);
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
}
