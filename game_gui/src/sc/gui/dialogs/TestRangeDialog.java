package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private JPanel pnlBottom;
	private JButton testStart;
	private JButton testCancel;
	private JButton btnclient1;
	private JButton btnclient2;
	private final ResourceBundle lang;
	private final PresentationFacade presFac;
	private JComboBox cmbGameType;

	public TestRangeDialog(JFrame frame) {
		super();
		this.presFac = PresentationFacade.getInstance();
		this.lang = presFac.getLogicFacade().getLanguageData();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		final ResourceBundle lang = PresentationFacade.getInstance().getLogicFacade()
				.getLanguageData();

		this.setLayout(new BorderLayout());
		
		cmbGameType = new JComboBox();
		//TODO

		txfclient1 = new JTextField(10);
		lblclient1 = new JLabel(lang.getString("dialog_test_lbl_ki1"));
		lblclient1.setLabelFor(txfclient1);
		btnclient1 = new JButton();
		btnclient1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadClient(txfclient1);
			}
		});

		pnlclient1 = new JPanel();
		pnlclient1.add(lblclient1);
		pnlclient1.add(txfclient1);

		txfclient2 = new JTextField(10);
		lblclient2 = new JLabel(lang.getString("dialog_test_lbl_ki2"));
		lblclient2.setLabelFor(txfclient2);
		btnclient2 = new JButton();
		btnclient2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadClient(txfclient2);
			}
		});

		pnlclient2 = new JPanel();
		pnlclient2.add(lblclient2);
		pnlclient2.add(txfclient2);

		DefaultTableModel statModel = new DefaultTableModel();
		statModel.addColumn(lang.getString("dialog_test_stats_pos"));
		statModel.addColumn(lang.getString("dialog_test_stats_wins"));
		statModel.addColumn(lang.getString("dialog_test_stats_losses"));
		// statModel.addColumn(lang.getString("dialog_test_stats_path"));
		statTable = new JTable(statModel);

		pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.PAGE_AXIS));
		pnlTop.add(cmbGameType);
		pnlTop.add(pnlclient1);
		pnlTop.add(pnlclient2);
		pnlTop.add(new JScrollPane(statTable));

		DefaultTableModel logModel = new DefaultTableModel();
		logTable = new JTable(logModel);
		// TODO

		testStart = new JButton(lang.getString("dialog_test_btn_start"));
		testStart.addActionListener(new ActionListener() {
			private boolean testing = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (testing) {
					stopTest();
					testStart.setText(lang.getString("dialog_test_btn_start"));
					testStart.setEnabled(true);
				} else {
					startTest();
					testStart.setText(lang.getString("dialog_test_btn_stop"));
					testStart.setEnabled(false);
				}
			}
		});

		testCancel = new JButton(lang.getString("dialog_test_btn_cancel"));
		testCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopTest();
				TestRangeDialog.this.dispose();
			}
		});

		pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlBottom.add(testStart);
		pnlBottom.add(testCancel);

		// add components
		this.add(pnlTop, BorderLayout.PAGE_START);
		this.add(new JScrollPane(logTable), BorderLayout.CENTER);
		this.add(pnlBottom, BorderLayout.PAGE_END);

		// set dialog preferences
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private void startTest() {
		File f1 = new File(txfclient1.getText());
		File f2 = new File(txfclient2.getText());
		if (!f1.exists() || !f2.exists()) {
			JOptionPane.showMessageDialog(this,
							lang.getString("dialog_test_error_file_msg"), lang
									.getString("dialog_test_error_file_title"),
							JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		// TODO
	}

	private void stopTest() {
		// TODO
	}

	private void loadClient(JTextField txf) {

	}

}
