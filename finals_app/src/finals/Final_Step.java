package finals;

import java.awt.Panel;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

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
			wid = new MatchWidget(pan, contestPanel, 1f, new Point(bigFinalsPosition.x,bigFinalsPosition.y+150), m);
			}else{
				wid = new MatchWidget(pan, contestPanel, 1f, new Point(
						stepCount * 250, currentY), m);
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
				System.out.println(m);
				m.setSelected(true);
				main.runReplay("replays/"+m.getMatch().getNextReplayName());
				m.getMatch().doNextStep();
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
	}

	public void stepBackward() {
		for (int i = matchWidgets.size() - 1; i >= 0; i--) {
			if (matchWidgets.get(i).getMatch().getCurrentStep() > 0) {
				matchWidgets.get(i).getMatch().undoLastStep();
				matchWidgets.get(i).setSelected(true);
				matchWidgets.get(i).paint();
				break;
			}
		}
		for (int i = matchWidgets.size()-1; i >= 0; i--) {
			if(matchWidgets.get(i).getMatch().getCurrentStep() < 6){
				if(getNextStep() != null && i == 1){
					System.out.println(i/2);
					if (i % 2 == 0) {
						//getNextStep().matchWidgets.get(i/2).setSecondNameVisible(false);
						getNextStep().matchWidgets.get(i/2).setFirstNameVisibe(false);
					}else{
						//getNextStep().matchWidgets.get(i/2).setFirstNameVisibe(false);
						getNextStep().matchWidgets.get(i/2).setSecondNameVisible(false);
					}
					getNextStep().matchWidgets.get(i/2).paint();
				}
			}
		}
		repaint();
	}

	public void doAllMatches() {
		for (MatchWidget m : matchWidgets) {
			m.getMatch().doAllSteps();
		}
	}

	public void undoAllMatches() {
		for (MatchWidget m : matchWidgets) {
			m.getMatch().undoAllSteps();
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
	
}
