package finals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;

public class MatchWidget {

	public MatchWidget(Panel pan, Panel cpan, float scale, Point position,
			Match match) {
		this.pan = pan;
		this.contestPan = cpan;
		this.scale = scale;
		this.position = position;
		this.match = match;
		this.width = scl(150);
		this.heigth = scl(30);
		this.paint();
	}

	private Panel pan;
	private Panel contestPan;
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
		this.selected = selected;
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
		this.firstPlayerConnection = w.getResultConnectionPoint();
	}

	public void connectSecondPlayerWith(MatchWidget w) {
		this.secondPlayerConnection = w.getResultConnectionPoint();
	}

	private void paintOnGC(Graphics2D gc) {
		gc.clearRect(position.x - scl(4), position.y, position.x + width,
				position.y + heigth);
		if (firstPlayerConnection != null) {
			drawNiceLine(gc, firstPlayerConnection,
					getPlayerOneConnectionPoint());
		}
		if (secondPlayerConnection != null) {
			drawNiceLine(gc, secondPlayerConnection,
					getPlayerTwoConnectionPoint());
		}
		// gc.setStroke((new BasicStroke(3f)));
		String firstName = (firstNameVisibe ? match.first.name : "???");
		String secondName = (secondNameVisible ? match.second.name : "???");
		gc.drawString(firstName, position.x + scl(5), position.y + scl(25));
		gc.drawLine(position.x + scl(5), position.y + scl(30), position.x
				+ scl(105), position.y + scl(30));
		gc.drawString(secondName, position.x + scl(5), position.y + scl(60));
		gc.drawLine(position.x + scl(5), position.y + scl(65), position.x
				+ scl(105), position.y + scl(65));
		gc.drawLine(position.x + scl(105), position.y + scl(30), position.x
				+ scl(105), position.y + scl(65));

		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getFirstCurrentScore() : ""), position.x + scl(111),
				position.y + scl(40));
		gc.drawString((match.getCurrentStep() > 0 ? ""
				+ match.getSecondCurrentScore() : ""), position.x + scl(111),
				position.y + scl(62));
		if (selected) {
			gc.setStroke((new BasicStroke(scl(5))));
			gc.setColor(new Color(255, 154, 150, 100));
			gc.drawRect(position.x, position.y + scl(7), scl(125), scl(70));
			gc.setStroke((new BasicStroke(scl(3))));
			gc.drawRect(position.x, position.y + scl(7), scl(125), scl(70));
		}
	}

	public void paint() {
		paintOnGC((Graphics2D) pan.getGraphics());
		if(contestPan != null && contestPan.getGraphics() != null){
			paintOnGC((Graphics2D) contestPan.getGraphics());
		}
		

	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
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

}
