package finals;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

public class ResultsFrame extends JFrame{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

JTable ranking;
JPanel container;
List<Contestant> currentRanking;
final int WIDTH_RANK = 30;
final int WIDTH_NAME = 200;
final int WIDTH_HOME = 80;
public ResultsFrame() {
	this.setSize(510,550);
	this.setVisible(true);
	this.setLayout(new GridLayout());
	this.setTitle("Ergebnisse: Finale");
	container = new JPanel();
	container.setVisible(true);
	this.add(container);
	this.setVisible(false);
}

public void createRanking(List<Contestant> standings){
	this.currentRanking = standings;
	//container.add(new JButton("test"));
	this.repaint();
}

@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(currentRanking != null) printTable();
		
	}

public void printTable(){
	Graphics2D gc = (Graphics2D) container.getGraphics();
	Font oldFont = gc.getFont();
	Font newFont = new Font ("Dialog", Font.BOLD, 14);
	gc.setFont(newFont);
	gc.drawString("Platz", 60, 40);
	gc.drawString("Team", 100+WIDTH_RANK, 40);
	gc.drawString("Ort", 80+WIDTH_NAME+WIDTH_RANK,40);
	gc.setFont(oldFont)	;
	for (int i = 0; i < currentRanking.size(); i++) {
		gc.drawString((i+1 > 5 ? 5 : i+1)+"", 60, 40*(i+2));
		gc.drawString(currentRanking.get(i).name, 100+WIDTH_RANK, 40*(i+2));
		gc.drawString(currentRanking.get(i).home, 80+WIDTH_NAME+WIDTH_RANK,40*(i+2));
	}
}
}
