package finals;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class InfoFrame extends JFrame{
public InfoFrame(MainFrame parent){
	this.setTitle("Info: Finals Anwendung");
	GridLayout lay = new GridLayout(4,1);
	this.setLayout(lay);
	this.setVisible(true);
	this.setSize(500,150);
	Font fnt= parent.getFont();
	Font fntFat = new Font(fnt.getName(),Font.BOLD,fnt.getSize());
	Font fntBigFat = new Font(fnt.getName(),Font.BOLD,18);
	Font fntSmallItalic = new Font(fnt.getName(),Font.ITALIC,10);
	
	JLabel head = new JLabel("Software Challenge - Finals Anwendung, "+parent.config.getVersion());
	head.setFont(fntBigFat);
	this.add(head);
	
	JLabel developer = new JLabel("Entwickler:");
	developer.setFont(fntFat);
	this.add(developer);
	
	String[] devList = parent.config.developers;
	for (int i = 0; i < devList.length; i++) {
		JLabel curr = new JLabel(devList[i]);
		curr.setFont(fnt);
		this.add(curr);
	}
	
	JLabel copyright = new JLabel("© CAU Kiel, Institut für Informatik, 2010");
	copyright.setFont(fntSmallItalic);
	this.add(copyright);
	
}
}
