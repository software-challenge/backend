/**
 * 
 */
package sc.plugin2010.renderer.twodimensional;

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
	}

}
