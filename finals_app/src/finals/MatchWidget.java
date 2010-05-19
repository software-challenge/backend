package finals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.JPanel;

public class MatchWidget {

	public MatchWidget(JPanel pan, JPanel cpan, float scale, Point position,
			Match match, Final_Step step, int order) {
		this.pan = pan;
		this.contestPan = cpan;
		this.scale = scale;
		this.position = position;
		this.match = match;
		this.width = scl(150);
		this.heigth = scl(30);
		this.step = step;
		this.paint();
		this.order = order;
	}

	private JPanel pan;
	private JPanel contestPan;
	private Point position;
	private Match match;
	private boolean selected = false;
	private boolean firstNameVisibe = false;
	private boolean secondNameVisible = false;
	private Point firstPlayerConnection;
	private Point secondPlayerConnection;
	private int width;
	private int heigth;
	private float scale;
	private boolean repaint = true;
	private Final_Step step;
	private int order;

	public Point getPlayerOneConnectionPoint() {
		return new Point(position.x + scl(5), position.y + scl(30));
	}

	public Point getPlayerTwoConnectionPoint() {
		return new Point(position.x + scl(5), position.y + scl(65));
	}

	public Point getResultConnectionPoint() {
		return new Point(position.x + scl(105), position.y + scl(47));
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if(selected != this.selected) repaint = true;
		this.selected = selected;
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		if(match != this.match) repaint = true;
		this.match = match;
	}

	public boolean isFirstNameVisibe() {
		return firstNameVisibe;
	}

	public void setFirstNameVisibe(boolean firstNameVisibe) {
		if(firstNameVisibe != this.firstNameVisibe) repaint = true;
		this.firstNameVisibe = firstNameVisibe;
	}

	public boolean isSecondNameVisible() {
		return secondNameVisible;
	}

	public void setSecondNameVisible(boolean secondNameVisible) {
		if(secondNameVisible != this.secondNameVisible) repaint = true;
		this.secondNameVisible = secondNameVisible;
	}

	public void connectFirstPlayerWith(MatchWidget w) {
		repaint = true;
		this.firstPlayerConnection = w.getResultConnectionPoint();
	}

	public void connectSecondPlayerWith(MatchWidget w) {
		repaint = true;
		this.secondPlayerConnection = w.getResultConnectionPoint();
	}

	private void paintOnGC(Graphics2D gc) {
		// Clear widgets area
		gc.clearRect(position.x - scl(4), position.y, position.x + width,
			position.y + heigth);
		
		// Draw incoming connections
		if (firstPlayerConnection != null) {
			drawNiceLine(gc, firstPlayerConnection,
					getPlayerOneConnectionPoint());
		}
		if (secondPlayerConnection != null) {
			drawNiceLine(gc, secondPlayerConnection,
					getPlayerTwoConnectionPoint());
		}
	
		
		
		// Choose the names displayed
		String firstName, secondName;
		if(firstNameVisibe){
			firstName = match.getFirst().name;
		}else{
			if(!step.isSmallFinals()){
				firstName = "Sieger, Runde "+(step.order-1)+"Begegnung "+(1+order*2);
			}else{
				firstName = "Verlierer, Runde"+ (step.order-1)+"Begegnung "+(1+order*2);
			}
		}
		
		if(secondNameVisible){
			secondName = match.getSecond().name;
		}else{
			if(!step.isSmallFinals()){
				secondName = "Sieger, Runde "+(step.order-1)+"Begegnung "+(2+order*2);
			}else{
				secondName = "Verlierer, Runde"+ (step.order-1)+"Begegnung "+(2+order*2);
			}
		}
		
		//String firstName = (firstNameVisibe ? match.first.name : "???");
		//String secondName = (secondNameVisible ? match.second.name : "???");		
		
		// Draw player names
		Color firstColor = gc.getColor();
		Color secondColor = gc.getColor();
		Color old = gc.getColor();
		if(match.isFinished()){
			if(match.isWinner(match.first)){
				firstColor = new Color(0,130,0);
			}else{
				firstColor = new Color(255,0,0);
			}
			
			if(match.isWinner(match.second)){
				secondColor = new Color(0,130,0);
			}else{
				secondColor = new Color(255,0,0);
			}
			
		}
		gc.setColor(firstColor);
		gc.drawString(firstName, position.x + scl(5), position.y + scl(25));
		gc.setColor(secondColor);
		gc.drawString(secondName, position.x + scl(5), position.y+ scl(80));
		gc.setColor(old);

		// Draw current standings
		gc.setColor(firstColor);
		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getFirstCurrentScore() : ""), position.x + scl(111),
				position.y + scl(40));
		gc.setColor(secondColor);
		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getSecondCurrentScore() : ""), position.x + scl(111),
				position.y + scl(62));
		gc.setColor(old);

		
		// Draw widgets lines
		gc.drawLine(position.x + scl(5), position.y + scl(30), position.x
				+ scl(105), position.y + scl(30));
		gc.drawLine(position.x + scl(5), position.y + scl(65), position.x
				+ scl(105), position.y + scl(65));
		gc.drawLine(position.x + scl(105), position.y + scl(30), position.x
				+ scl(105), position.y + scl(65));

		if (match.getCurrentStep() == 0) selected = false;
		
		// Draw selection frame
		if (selected) {
			gc.setStroke((new BasicStroke(scl(5))));
			gc.setColor(new Color(255, 154, 150, 100));
			gc.drawRect(position.x, position.y + scl(7), scl(125), scl(80));
			gc.setStroke((new BasicStroke(scl(3))));
			gc.drawRect(position.x, position.y + scl(7), scl(125), scl(80));
		}
	}

	public void paint() {
		//if(repaint){
			paintOnGC((Graphics2D) pan.getGraphics());
			if(contestPan != null && contestPan.getGraphics() != null){
				paintOnGC((Graphics2D) contestPan.getGraphics());
			}
			repaint = false;
		//}
		
		

	}
	
	public void setRepaint(boolean b){
		this.repaint = b;
	}
	
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		repaint = true;
		this.position = position;
	}

	private void drawNiceLine(Graphics2D gc, Point o, Point d) {
		gc.setStroke((new BasicStroke(1f)));
		int xMid = o.x + ((d.x - o.x) / 2);
		gc.drawLine(o.x, o.y, xMid, o.y);
		gc.drawLine(xMid, o.y, xMid, d.y);
		gc.drawLine(xMid, d.y, d.x, d.y);
	}

	private int scl(int value) {
		return (int) (scale * value);
	}
	
	public void undoAll(boolean isFirstStep){
		match.undoAllSteps();
		selected = false;
		if(!isFirstStep){
			firstNameVisibe = false;
			secondNameVisible = false;
		}
		repaint = true;
	}
	
	public void doAll(boolean isFirstStep){
		undoAll(isFirstStep);
		firstNameVisibe = true;
		secondNameVisible = true;
		while(!match.isFinished()){
			match.doNextStep();
		}
		repaint = true;
	}
	
	
	public boolean isFinished(){
		return match.isFinished();
	}

}
