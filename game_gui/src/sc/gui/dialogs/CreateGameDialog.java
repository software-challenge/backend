package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class CreateGameDialog extends JDialog {

	public CreateGameDialog(JFrame frame) {
		super();
		createGUI(frame);
	}

	private void createGUI(JFrame frame) {

		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.PAGE_AXIS));
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.PAGE_AXIS));

		this.add(left);
		// this.add(Box.createVerticalGlue());
		this.add(right);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSize(2, 3);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setControlButtonsAreShown(false);

		// Buttons
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JButton addHumanButton = new JButton("Add human player");
		addHumanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/* okButton */
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/* cancelButton */
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		JPanel buttonBar = new JPanel();
		buttonBar.add(okButton);
		buttonBar.add(cancelButton);

		// set dialog preferences
		this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(frame);
		this.setModal(true);
		this.setSize(800, 480);
	}
}
