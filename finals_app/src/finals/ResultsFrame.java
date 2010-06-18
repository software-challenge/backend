package finals;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ResultsFrame extends JFrame{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

JPanel container;
List<Contestant> currentRanking;
int WIDTH_RANK ;
int HEIGTH_RANK;
int HEIGTH_NAME;
int HEIGTH_HOME;
int HEIGTH_TROPHY;
int WIDTH_NAME;
int WIDTH_HOME;
int WIDTH_TROPHY;
int FONT_SIZE;
Color COLOR_FIRST = new Color(218,165,032);
Color COLOR_SECOND = new Color(161,161,161);
Color COLOR_THIRD = new Color(139,69,19);
Color COLOR_OTHERS = new Color(0,0,0);
Font FONT_NORMAL;
Font FONT_BOLD;
String TEXT_STYLE;

public ResultsFrame(MainFrame main, int width, int heigth) {
	this.setSize(510,550);
	this.setLayout(new GridLayout());
	this.setTitle("Ergebnisse: Finale");
	container = new JPanel();
	container.setVisible(true);
	this.add(container);
	this.setVisible(false);
	TEXT_STYLE = main.config.TEXT_STYLE;
	FinalsConfiguration config = main.config;
	this.setBackground(config.BACKGROUND);
}

public void createRanking(List<Contestant> standings){
	this.currentRanking = standings;
	this.repaint();
}

@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(currentRanking != null) printTable();
		
	}

public void printTable(){
	scaleValues();
	int yOffset = (getHeight() - (HEIGTH_HOME*currentRanking.size()+1))/2;
	int xOffset = (getWidth() - (WIDTH_HOME+WIDTH_NAME+WIDTH_RANK+WIDTH_TROPHY))/2;
	Graphics g =  container.getGraphics();
	Graphics2D gc = (Graphics2D) container.getGraphics();
	gc.setFont(FONT_BOLD);
	gc.drawString("Platz", WIDTH_TROPHY+xOffset, yOffset);
	gc.drawString("Team", WIDTH_TROPHY+WIDTH_RANK+xOffset, yOffset);
	gc.drawString("Ort", WIDTH_TROPHY+WIDTH_NAME+WIDTH_RANK+xOffset,yOffset);
	gc.setFont(FONT_NORMAL)	;
	for (int i = 0; i < currentRanking.size(); i++) {
		int currY = yOffset+HEIGTH_NAME*(i+1);
		Image img;
		if(i==0){
			img = Toolkit.getDefaultToolkit().getImage("img/gold.png");
			g.drawImage(img, xOffset, currY-16,this);
			gc.setFont(FONT_BOLD);
			gc.setColor(COLOR_FIRST);
		}else if(i==1){
			img = Toolkit.getDefaultToolkit().getImage("img/silver.png");
			g.drawImage(img, xOffset, currY-16,this);
			gc.setFont(FONT_BOLD);
			gc.setColor(COLOR_SECOND);
		}else if(i==2){
			img = Toolkit.getDefaultToolkit().getImage("img/bronze.png");
			g.drawImage(img, xOffset, currY-16,this);
			gc.setFont(FONT_BOLD);
			gc.setColor(COLOR_THIRD);
		}else{
			gc.setColor(COLOR_OTHERS);
			gc.setFont(FONT_NORMAL);
		}
		
		gc.drawString((i+1 > 5 ? 5 : i+1)+"", WIDTH_TROPHY+xOffset, currY);
		gc.drawString(currentRanking.get(i).name, WIDTH_TROPHY+xOffset+WIDTH_RANK, currY);
		gc.drawString(currentRanking.get(i).home, WIDTH_TROPHY+xOffset+WIDTH_NAME+WIDTH_RANK,currY);
	}
}

private void scaleValues(){
	if(isVisible()){
		float xScale = this.getWidth()/800f;
		float yScale = this.getHeight()/500f;
		HEIGTH_HOME = HEIGTH_NAME = HEIGTH_RANK = HEIGTH_TROPHY = (int) (yScale*30);
		WIDTH_HOME = (int) (xScale*80);
		WIDTH_NAME = (int) (xScale*200);
		WIDTH_RANK = (int) (xScale*50);
		WIDTH_TROPHY = (int) (xScale*30);
		FONT_SIZE = (int) (14*(xScale < yScale ? xScale : yScale));
		FONT_BOLD = new Font(TEXT_STYLE,Font.BOLD,FONT_SIZE);
		FONT_NORMAL = new Font("Arial",Font.PLAIN,FONT_SIZE);
		System.out.println(FONT_SIZE + "xScale: "+xScale + " yScale: "+yScale);
	}
}
}
