/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author ffi
 * 
 */
@SuppressWarnings("serial")
public class ActionBar extends JList
{

	public ActionBar()
	{
		DefaultListModel defModel = new DefaultListModel();

		setModel(defModel);
		setVisible(true);
	}

	public void addRow(String val)
	{
		((DefaultListModel) getModel()).addElement(val);
		scrollRectToVisible(new Rectangle(0, 0, getWidth(), getHeight()));
	}

	/**
	 * 
	 */
	public void removeAllRows()
	{
		((DefaultListModel) getModel()).removeAllElements();
	}

}
