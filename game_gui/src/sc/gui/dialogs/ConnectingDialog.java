package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import sc.gui.PresentationFacade;

@SuppressWarnings("serial")
public class ConnectingDialog extends JDialog implements ActionListener {

	private int returnValue = JOptionPane.DEFAULT_OPTION;
	private Properties lang;
	private boolean closed;

	public ConnectingDialog() {
		super();
		this.lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		this.closed = false;
		createGUI();
	}

	private void createGUI() {

		this.setLayout(new BorderLayout());

		JPanel pnlConnecting = new JPanel();
		JPanel pnlButtonBar = new JPanel();

		this.add(pnlConnecting, BorderLayout.CENTER);
		this.add(pnlButtonBar, BorderLayout.PAGE_END);

		// ------------------------------------------

		JLabel lblConnecting = new JLabel(lang.getProperty("dialog_connecting_msg"));
		pnlConnecting.add(lblConnecting);

		JButton btnCancel = new JButton(lang.getProperty("dialog_connecting_cancel"));// TODO
		btnCancel.addActionListener(this);
		pnlButtonBar.add(btnCancel);

		// ------------------------------------------

		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setResizable(false);
		this.setModal(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		returnValue = JOptionPane.CANCEL_OPTION;
		this.setVisible(false);
		this.close();
	}

	/**
	 * Shows the connection dialog.
	 * 
	 * @return
	 */
	public int showDialog() {
		if (!closed) {			
			this.setVisible(true);
		}
		return this.getReturnValue();
	}

	public int getReturnValue() {
		return returnValue;
	}

	public void close() {
		if (!closed) {
			dispose();
		}
		this.closed = true;
	}

}
