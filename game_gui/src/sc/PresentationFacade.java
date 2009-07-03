package sc;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import sc.contextframe.ContextDisplay;

/**
 * The Software Challenge's implementation of {@link IPresentationFacade}.
 * 
 * @author chw
 * @since SC'09
 */
public class PresentationFacade implements IPresentationFacade {

	@Override
	public JComponent getContextDisplay() {
		// TODO Auto-generated method stub
		return new ContextDisplay();
	}

	@Override
	public JMenuBar getMenuBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getClientIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JComponent getStatusBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
