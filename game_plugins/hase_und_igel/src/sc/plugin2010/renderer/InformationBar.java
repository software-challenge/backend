/**
 * 
 */
package sc.plugin2010.renderer;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author ffi
 * 
 */
public class InformationBar extends JList
{
	public InformationBar(int width, int height)
	{
		DefaultListModel defModel = new DefaultListModel();

		defModel.add(0, "String");

		setModel(defModel);

		setSize(width, height);
		setVisible(true);
	}
}
