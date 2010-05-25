package finals;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.plaf.FontUIResource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Final_Step {
	
	public Final_Step(MainFrame main, List<Match> matches,
			List<Final_Step> steps) {
		this(main, matches, steps, false, 0);
	}

	
	public Final_Step(MainFrame main, List<Match> matches,
			List<Final_Step> steps, boolean showTeamsFromBeginning, int order) {
		this.smallFinals = (order == 4 ? true : false); 
		this.order = order;
		this.steps = steps;
		this.matches = matches;
		this.matchWidgets = new LinkedList<MatchWidget>();
		this.pan = main.pan;
		this.contestPanel = main.contestPanel;
		this.main = main;
		
	}

	List<MatchWidget> matchWidgets;
	List<Match> matches;
	List<Final_Step> steps;
	MainFrame main;
	boolean smallFinals = false;
	int order;
	JPanel pan;
	JPanel contestPanel;


	public void showAllNames(){
		for (MatchWidget w : this.matchWidgets) {
			w.setFirstNameVisibe(true);
			w.setSecondNameVisible(true);
		}
	}
	
	public void createStepWidgets(){
		int yPossible = 500 / (matches.size() + 1);
		int currentY = yPossible;
		int stepCount = 0;
		for (Final_Step finalStep : steps) {
			if (finalStep.equals(this))
				break;
			stepCount++;			
		}
		
		for (int i = 0; i < matches.size(); i++) {
			Match m = matches.get(i);
			MatchWidget wid;
			
			if(smallFinals){
			Point bigFinalsPosition = steps.get(steps.size()-2).matchWidgets.get(0).getPosition();
			wid = new MatchWidget(pan, contestPanel, 1f, new Point(bigFinalsPosition.x,bigFinalsPosition.y+150), m, this,matchWidgets.size());
			}else{
				wid = new MatchWidget(pan, contestPanel, 1f, new Point(
						((pan.getWidth()-750)/2) + (stepCount * 250) - 125, currentY), m, this, matchWidgets.size());
			}
				
			this.matchWidgets.add(wid);
			currentY += yPossible;
		}
	}
	public boolean isFinished() {
		boolean finished = true;
		for (MatchWidget m : matchWidgets) {
			if (!m.getMatch().isFinished()) {
				finished = false;
				break;
			}

		}
		return finished;
	}

	public void stepForward() {
		Final_Step smallFinals = steps.get(steps.size()-1);
		if(isFinals() && !smallFinals.isFinished()){
			smallFinals.showAllNames();
			smallFinals.stepForward();
			return;
		}else if (isFinals() && smallFinals.isFinished() && smallFinals.matchWidgets.get(0).isSelected()) {
			smallFinals.matchWidgets.get(0).setSelected(false);
			smallFinals.matchWidgets.get(0).paint();
		}
		for (int i = 0; i < matchWidgets.size(); i++) {
			MatchWidget m = matchWidgets.get(i);
			if (!m.getMatch().isFinished()) {
				m.setSelected(true);
				main.runReplay("replays/"+m.getMatch().getNextReplayName());
				m.getMatch().doNextStep();
				m.setRepaint(true);
				return;
			} else {
				m.setSelected(false);
				if (i % 2 == 0) {
					if (getNextStep() == steps.get(steps.size()-2)){
						steps.get(steps.size()-1).matchWidgets.get(0).setSecondNameVisible(true);
						steps.get(steps.size()-1).matchWidgets.get(0).paint();
					}
					getNextStep().matchWidgets.get(i / 2).setFirstNameVisibe(true);
					getNextStep().matchWidgets.get(i / 2).paint();
				} else {
					getNextStep().matchWidgets.get(i / 2).setSecondNameVisible(true);
					if (getNextStep() == steps.get(steps.size()-2)){
						steps.get(steps.size()-1).matchWidgets.get(0).setFirstNameVisibe(true);
						steps.get(steps.size()-1).matchWidgets.get(0).paint();
					}
					getNextStep().matchWidgets.get(i / 2).paint();
				}

			}
		}
	}

	public void repaint() {
		for (MatchWidget match : matchWidgets) {
			match.paint();
		}
		if(!smallFinals){
			Graphics gc = pan.getGraphics();
			int stepCount = order-1;
			Font oldFont = gc.getFont();
			Font newFont = new Font ("Dialog", Font.BOLD, 14);
			gc.setFont(newFont);
			switch (order) {
			case 1:
				gc.drawString("Viertelfinale", ((pan.getWidth()-750)/2) + (stepCount * 250)+3, 80);		
				break;
			case 2:
				gc.drawString("Halbfinale", ((pan.getWidth()-750)/2) + (stepCount * 250)+3, 80);
				break;
			case 3:
				gc.drawString("Großes und kleines Finale", ((pan.getWidth()-750)/2) + (stepCount * 250)+3, 80);
				break;
			default:
				//pan.getGraphics().drawString("Kleines Finale", ((pan.getWidth()-750)/2) + ((stepCount-1) * 250)+3, 220);
			}
			gc.setFont(oldFont);
		}
	}

	public void stepBackward() {
		if(isSmallFinals() && !getFinals().isInit()){
			getFinals().stepBackward();
			return;
		}
		
		for (int i = matchWidgets.size() - 1; i >= 0; i--) {
			if (matchWidgets.get(i).getMatch().getCurrentStep() > 0) {
				matchWidgets.get(i).setRepaint(true);
				matchWidgets.get(i).getMatch().undoLastStep();
				matchWidgets.get(i).setSelected(true);
				//matchWidgets.get(i).paint();
				break;
			}
		}
		for (int i = matchWidgets.size()-1; i >= 0; i--) {
			if(!matchWidgets.get(i).isFinished()){
				MatchWidget followingWid;
				if(order == 1){
					followingWid = getNextStep().matchWidgets.get(i/2);
					if (i % 2 == 0) {
						followingWid.setFirstNameVisibe(false);
					}else{
						followingWid.setSecondNameVisible(false);
					}
					followingWid.paint();
				}else if (order == 2) {
					if (i % 2 == 0) {
						followingWid = getNextStep().matchWidgets.get(0);
						followingWid.setFirstNameVisibe(false);
						//followingWid.paint();
						followingWid = getSmallFinals().matchWidgets.get(0);
						followingWid.setSecondNameVisible(false);
						//followingWid.paint();
					}else{
						followingWid = getNextStep().matchWidgets.get(0);
						followingWid.setSecondNameVisible(false);
						//followingWid.paint();
						followingWid = getSmallFinals().matchWidgets.get(0);
						followingWid.setFirstNameVisibe(false);
						//followingWid.paint();
					}
				}
			}
		}
		//repaint();
	}

	public void doAllMatches() {
		for (MatchWidget m : matchWidgets) {
			m.doAll(order == 1);
		}
	}

	public void undoAllMatches() {
		for (MatchWidget m : matchWidgets) {
			m.undoAll(order == 1);
		}
	}

	public Final_Step getPervStep() {
		Final_Step prev = null;
		if (!this.equals(steps.get(0))) {
			for (int i = 0; i < steps.size(); i++) {
				if (steps.get(i).equals(this))
					prev = steps.get(i - 1);
			}
		}
		return prev;
	}

	public void initConnections() {
		if(isSmallFinals()) return;
		Final_Step last = getPervStep();
		if (last != null) {
			for (int i = 0; i < matchWidgets.size(); i++) {
				matchWidgets.get(i).connectFirstPlayerWith(last.matchWidgets.get(i * 2));
				matchWidgets.get(i).connectSecondPlayerWith(
						last.matchWidgets.get(i * 2 +1 ));
			}
		}
	}
	
	public boolean areSmallFinalsPlayed(){
		return steps.get(steps.size()-1).isFinished();
	}
	
	public boolean isSmallFinals(){
		return getCount()==(steps.size()-1);
	}

	public boolean isFinals(){
		int count = getCount();		
		if(steps.size()-2 == count){
			return true;
		}
		return false;
	}
	
	public int getCount(){
		int count = 0;
		for (int i = 0; i < steps.size(); i++) {
			if(steps.get(i)==this){
				count = i;
			}
		}
		return count;
	}
	
	public Final_Step getNextStep() {
		Final_Step next = this;
		for (int i = 0; i < steps.size() - 1; i++) {
			if (steps.get(i).equals(this)) {
				next = steps.get(i + 1);
			}
		}
		return next;
	}
	

	public boolean isInit(){
		boolean isInitial = true;
		for(MatchWidget wid : matchWidgets) if(wid.getMatch().getCurrentStep()!=0) isInitial = false;
		return isInitial;
	}
	
	public Final_Step getSmallFinals(){
		return steps.get(steps.size()-1);
	}
	
	public Final_Step getFinals(){
		return steps.get(steps.size()-2);
	}
	
	public void publishToContestPanel(){
		if(contestPanel != null && contestPanel.getGraphics() != null){
			Graphics gc = contestPanel.getGraphics();
			int stepCount = order-1;
			Font oldFont = gc.getFont();
			Font newFont = new Font ("Dialog", Font.BOLD, 14);
			gc.setFont(newFont);
			
			switch (order) {
			case 1:
				gc.drawString("Viertelfinale", ((contestPanel.getWidth()-750)/2) + (stepCount * 250)+3, 80);			
				break;
			case 2:
				gc.drawString("Halbfinale", ((contestPanel.getWidth()-750)/2) + (stepCount * 250)+3, 80);
				break;
			case 3:
				gc.drawString("Großes und kleines Finale", ((contestPanel.getWidth()-750)/2) + (stepCount * 250)+3, 80);
				break;
			default:
			}
			gc.setFont(oldFont);
		}
		for(MatchWidget wid : matchWidgets){
			wid.publishToContestPanel();
		}
		
	}
	
}
