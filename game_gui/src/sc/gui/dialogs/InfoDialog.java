package sc.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import sc.gui.PresentationFacade;
import sc.server.Configuration;

@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	private Properties lang;

	public InfoDialog() {
		super();
		this.lang = PresentationFacade.getInstance().getLogicFacade().getLanguageData();
		createGUI();
	}

	private void createGUI() {

		this.setLayout(new BorderLayout());

		JPanel scPanel = new JPanel();
		scPanel.setBorder(BorderFactory.createEtchedBorder());

		JPanel devPanel = new JPanel();
		devPanel.setBorder(BorderFactory.createEtchedBorder());
		devPanel.setLayout(new BorderLayout());

		this.add(scPanel, BorderLayout.CENTER);
		this.add(devPanel, BorderLayout.PAGE_END);

		// ----------------------------
		ImageIcon image = new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon()));
		
		JLabel lblImage = new JLabel(image, JLabel.CENTER);
		scPanel.add(lblImage);
		scPanel.add(new JLabel("Version: " + Configuration.get("code-version", String.class, "Unbekannt"), JLabel.CENTER));

		JLabel developer = new JLabel(lang.getProperty("dialog_info_developers"), JLabel.CENTER);
		developer.setFont(developer.getFont().deriveFont(Font.BOLD).deriveFont(16f));
		
		DefaultTableModel model = new MyTableModel();
		model.addColumn(lang.getProperty("dialog_info_table_name"));
		model.addColumn(lang.getProperty("dialog_info_table_description"));
		
		JTable tbl_developers = new JTable(model);
		tbl_developers.setRowHeight(30);
		tbl_developers.getColumnModel().getColumn(0).setMaxWidth(200);
		tbl_developers.getColumnModel().getColumn(0).setPreferredWidth(120);
		tbl_developers.getTableHeader().setReorderingAllowed(false);
		tbl_developers.getTableHeader().setResizingAllowed(false);
		
		model.addRow(new String[]{"Christian Wulf", lang.getProperty("dialog_info_chw")});
		model.addRow(new String[]{"Florian Fittkau", lang.getProperty("dialog_info_ffi")});
		model.addRow(new String[]{"Marcel Jackwerth", lang.getProperty("dialog_info_mja")});
		model.addRow(new String[]{"Raphael Randschau", lang.getProperty("dialog_info_rra")});
		model.addRow(new String[]{"Manfred Schimmler", lang.getProperty("dialog_info_masch")});
		model.addRow(new String[]{"Christoph Starke", lang.getProperty("dialog_info_chst")});

		// fit scrollpane's size to the table's size
		JScrollPane scroll = new JScrollPane(tbl_developers);
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width,
				(tbl_developers.getRowCount() + 1) * tbl_developers.getRowHeight()));
		
		devPanel.add(developer, BorderLayout.PAGE_START);
		devPanel.add(scroll, BorderLayout.CENTER);

		// set dialog preferences
		setIconImage(new ImageIcon(getClass().getResource(
				PresentationFacade.getInstance().getClientIcon())).getImage());
		this.setPreferredSize(new Dimension(600,360));
		this.setResizable(false);
		this.setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * Non-editable table model.
	 * 
	 * @author chw
	 * 
	 */
	private class MyTableModel extends DefaultTableModel {

		@Override
		public boolean isCellEditable(int row, int col) {
			return false;
		}

	}
}
