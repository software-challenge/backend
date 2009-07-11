package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import sc.gui.PresentationFacade;

@SuppressWarnings("serial")
public class TestRangeDialog extends JDialog {

	private JLabel lblclient2;
	private JTextField txfclient2;
	private JLabel lblclient1;
	private JTextField txfclient1;
	private JPanel pnlclient1;
	private JPanel pnlclient2;
	private JTable logTable;
	private JPanel pnlTop;
	private JTable statTable;

	public TestRangeDialog(JFrame frame) {
		super();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {
		
		ResourceBundle lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		
		this.setLayout(new BorderLayout());
		
		txfclient1 = new JTextField();
		lblclient1 = new JLabel();
		lblclient1.setLabelFor(txfclient1);
		pnlclient1 = new JPanel();
		pnlclient1.add(lblclient1);
		pnlclient1.add(txfclient1);

		txfclient2 = new JTextField();
		lblclient2 = new JLabel();
		lblclient2.setLabelFor(txfclient2);
		pnlclient2 = new JPanel();
		pnlclient2.add(lblclient2);
		pnlclient2.add(txfclient2);
		
		DefaultTableModel statModel = new DefaultTableModel();
		statModel.addColumn(lang.getString("dialog_test_stats_pos"));
		statModel.addColumn(lang.getString("dialog_test_stats_wins"));
		statModel.addColumn(lang.getString("dialog_test_stats_losses"));
		//statModel.addColumn(lang.getString("dialog_test_stats_path"));
		statTable = new JTable(statModel);
		
		pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.PAGE_AXIS));
		pnlTop.add(pnlclient1);
		pnlTop.add(pnlclient2);
		pnlTop.add(new JScrollPane(statTable));
		
		DefaultTableModel logModel = new DefaultTableModel();
		logModel.addColumn(lang.getString("dialog_test_tbl_slot"));
		logModel.addColumn(lang.getString("dialog_test_tbl_name"));
		logModel.addColumn(lang.getString("dialog_test_tbl_type"));
		logModel.addColumn(lang.getString("dialog_test_tbl_path"));
		logTable = new JTable(logModel);
		//TODO
		
		//add components
		this.add(pnlTop, BorderLayout.PAGE_START);
		this.add(new JSeparator(), BorderLayout.CENTER);
		this.add(new JScrollPane(logTable), BorderLayout.CENTER);
		
		// set dialog preferences
		this.setModal(true);
		this.setLocationRelativeTo(frame);
		this.setSize(800, 480);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

}
