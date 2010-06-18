package finals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.JPanel;

public class MatchWidget {

	public MatchWidget(JPanel pan, JPanel cpan, float scale,
			Match match, Final_Step step, int order) {
		this.pan = pan;
		this.contestPan = cpan;
		this.scale = scale;
		//this.position = position;
		this.match = match;
		this.width = scl(150);
		this.heigth = scl(85);
		this.step = step;
		this.order = order;
		this.position = getPosition(pan);
		this.paint();
	}

	private JPanel pan;
	private JPanel contestPan;
	private Point position;
	private Match match;
	private boolean selected = false;
	private boolean firstNameVisibe = false;
	private boolean secondNameVisible = false;
	private MatchWidget firstPlayerConnection;
	private MatchWidget secondPlayerConnection;
	private int width;
	private int heigth;
	private float scale;
	private Final_Step step;
	private int order;

	/**
	 * Calculate the position of the widget, according the given panel
	 */
	public Point getPosition(JPanel pan){
		Point position;
		int header = scl(80);
		int yStep = (pan.getHeight()-header) / step.matches.size();
		int y = this.order*yStep+header;
		int column = step.order;
		
		if(step.isFinals()){
			column = 3;
		}
		
		if(column > 1){
			Final_Step prev = step.steps.get(column-2);
			int dist = (prev.matchWidgets.get(1).getPosition(pan).y-(prev.matchWidgets.get(0).getPosition(pan).y+heigth))/2;
			y += dist;
			y += heigth/2;
			y += prev.matchWidgets.get(0).getPosition(pan).y-header;
		}
		position = new Point((column-1)*2*this.width, y); 
		
		// Indent the widgets x coordinate
		position.translate(step.getOffsetForPanel(pan),0);
		
		// 
		if(step.isSmallFinals()) position.translate(0,2*heigth);
		return position;
	}
	
	public Point getPlayerOneConnectionPoint(JPanel pan) {
		Point position = getPosition(pan);
		return new Point(position.x + scl(5), position.y + scl(30));
	}

	public Point getPlayerTwoConnectionPoint(JPanel pan) {
		Point position = getPosition(pan);
		return new Point(position.x + scl(5), position.y + scl(65));
	}

	public Point getResultConnectionPoint(JPanel pan) {
		Point position = getPosition(pan);
		return new Point(position.x + scl(145), position.y + scl(47));
	}



	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public boolean isFirstNameVisibe() {
		return firstNameVisibe;
	}

	public void setFirstNameVisibe(boolean firstNameVisibe) {
		this.firstNameVisibe = firstNameVisibe;
	}

	public boolean isSecondNameVisible() {
		return secondNameVisible;
	}

	public void setSecondNameVisible(boolean secondNameVisible) {
		this.secondNameVisible = secondNameVisible;
	}

	public void connectFirstPlayerWith(MatchWidget w) {
		firstPlayerConnection = w;
	}

	public void connectSecondPlayerWith(MatchWidget w) {
		secondPlayerConnection = w;
	}
	
	public boolean isSelected(){
		return selected;
	}

	public void setSelected(boolean b){
		this.selected = b;
	}
	/**
	 * Decode the name of the Math according 
	 * @param step
	 * 			widgets step
	 * @param match
	 * 			the order of the match
	 * @return
	 * 			widgets name
	 */
	private String decode(int step, int match){
		String dec;
		switch (match) {
		case 0:
			dec = "A";
			break;
		case 1:
			dec = "B";
			break;
		case 2:
			dec = "C";
			break;
		case 3:
			dec = "D";
			break;
		default:
			dec = "E";
			break;
		}
		return dec +=step;
	}
	
	/**
	 * Paint the whole contents of the widget to a given Graphics2D object!
	 * @param gc
	 * @param pan
	 */
	private void paintOnGC(Graphics2D gc, JPanel pan) {
		Point position = getPosition(pan);
		
		// Clear widgets area
		gc.setBackground(new Color(255,255,255));
		
		// Scale Font
		gc.setFont(new Font(step.main.config.TEXT_STYLE, gc.getFont().getStyle(),scl(gc.getFont().getSize())));
		
		// Draw incoming connections
		if (firstPlayerConnection != null) {
			drawNiceLine(pan, gc, firstPlayerConnection.getResultConnectionPoint(pan),
					getPlayerOneConnectionPoint(pan));
		}
		if (secondPlayerConnection != null) {
			drawNiceLine(pan, gc, secondPlayerConnection.getResultConnectionPoint(pan),
					getPlayerTwoConnectionPoint(pan));
		}
	
		
		
		// Choose the names displayed
		String firstName, secondName;
		if(firstNameVisibe){
			firstName = match.getFirst().name;
		}else{
			if(!step.isSmallFinals()){
				firstName = "Sieger: "+decode(step.order-1, order*2);
			}else{
				firstName = "Verlierer: "+decode(step.order-2, order*2);
			}
		}
		
		if(secondNameVisible){
			secondName = match.getSecond().name;
		}else{
			if(!step.isSmallFinals()){
				secondName = "Sieger: "+decode(step.order-1, order*2+1);
			}else{
				secondName = "Verlierer: "+decode(step.order-2, order*2+1);
			}
		}
		
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
		Font oldFont = gc.getFont();
		Font newFont = new Font(oldFont.getName(), Font.BOLD, oldFont.getSize());
		if(isFirstNameVisibe() && (isSelected() || match.isFinished()) ) gc.setFont(newFont);
		gc.drawString(firstName, position.x + scl(5), position.y + scl(25));
		gc.setColor(secondColor);
		gc.drawString(secondName, position.x + scl(5), position.y+ scl(80));
		gc.setColor(old);
		gc.setFont(oldFont);
		gc.drawString(decode(step.order, order), position.x + scl(126), position.y+ scl(52));

		
		// Draw current standings
		gc.setColor(firstColor);
		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getFirstCurrentScore() : ""), position.x + scl(151),
				position.y + scl(40));
		gc.setColor(secondColor);
		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getSecondCurrentScore() : ""), position.x + scl(151),
				position.y + scl(62));
		gc.setColor(old);

		
		// Draw widgets lines
		gc.drawLine(position.x + scl(5), position.y + scl(30), position.x
				+ scl(145), position.y + scl(30));
		gc.drawLine(position.x + scl(5), position.y + scl(65), position.x
				+ scl(145), position.y + scl(65));
		gc.drawLine(position.x + scl(145), position.y + scl(30), position.x
				+ scl(145), position.y + scl(65));

		if (match.getCurrentStep() == 0) selected = false;
		
		// Draw selection frame
		if (selected) {
			gc.setStroke((new BasicStroke(scl(5))));
			gc.setColor(new Color(255, 154, 150, 100));
			gc.drawRect(position.x, position.y + scl(7), scl(185), scl(80));
			gc.setStroke((new BasicStroke(scl(3))));
			gc.drawRect(position.x, position.y + scl(7), scl(185), scl(80));
		}
	}

	public void paint() {
			paintOnGC((Graphics2D) pan.getGraphics(),pan);
	}
	
	/**
	 * Paint the widget with the current values to the contest window
	 */
	public void publishToContestPanel(){
		if(contestPan != null && contestPan.getGraphics() != null){
		  paintOnGC((Graphics2D) contestPan.getGraphics(),contestPan);
		}
	}
	

	public void setPosition(Point position) {
		this.position = position;
	}

	private void drawNiceLine(JPanel pan, Graphics2D gc, Point o, Point d) {
		gc.setStroke((new BasicStroke(1f)));
		Point od = (Point) o.clone();
		Point dd = (Point) d.clone();
		int xMid = od.x + ((dd.x - od.x) / 2);
		gc.drawLine(od.x, od.y, xMid, od.y);
		gc.drawLine(xMid, od.y, xMid, dd.y);
		gc.drawLine(xMid, dd.y, dd.x, dd.y);
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
	}
	
	public void doAll(boolean isFirstStep){
		undoAll(isFirstStep);
		firstNameVisibe = true;
		secondNameVisible = true;
		while(!match.isFinished()){
			match.doNextStep();
		}
	}
	
	
	public boolean isFinished(){
		return match.isFinished();
	}

}
