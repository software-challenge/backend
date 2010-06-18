package finals;

import java.awt.Font;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

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
	float scl = 1.2f;
	JPanel contestPanel;


	public void showAllNames(){
		for (MatchWidget w : this.matchWidgets) {
			w.setFirstNameVisibe(true);
			w.setSecondNameVisible(true);
		}
	}
	
	public void createStepWidgets(){
		//(order == 1 ? header : yStep);	
		for (int i = 0; i < matches.size(); i++) {
			Match m = matches.get(i);
			MatchWidget wid;
			wid = new MatchWidget(pan, contestPanel, scl, m,this,i);
			this.matchWidgets.add(wid);
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
		System.out.println("Step "+order+" is finished? "+finished);
		return finished;
	}
	
	public void setNamesVisible(boolean b){
		for(MatchWidget wid : matchWidgets){
			wid.setFirstNameVisibe(b);
			wid.setSecondNameVisible(b);
		}
	}
	
	
	public void stepForward() {
		for (int i = 0; i < matchWidgets.size(); i++) {
			MatchWidget m = matchWidgets.get(i);
			if (!m.getMatch().isFinished()) {
				main.runReplay("replays/"+m.getMatch().getNextReplayName(), false, m);
				return;
			} else {
				m.setSelected(false);
				if (i % 2 == 0) {
					if (order == 2){
						getFinals().matchWidgets.get(0).setSecondNameVisible(true);
						getFinals().matchWidgets.get(0).paint();
					}
					getNextStep().matchWidgets.get(i / 2).setFirstNameVisibe(true);
					getNextStep().matchWidgets.get(i / 2).paint();
				} else {
					if (order == 2){
						getFinals().matchWidgets.get(0).setFirstNameVisibe(true);
						getFinals().matchWidgets.get(0).paint();
					}
					getNextStep().matchWidgets.get(i / 2).setSecondNameVisible(true);
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
			drawHeadlineOnPanel(pan);
		}
	}

	public void stepBackward() {
		
		for (int i = matchWidgets.size() - 1; i >= 0; i--) {
			if (matchWidgets.get(i).getMatch().getCurrentStep() > 0) {
				matchWidgets.get(i).getMatch().undoLastStep();
				matchWidgets.get(i).setSelected(true);
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
						followingWid = getSmallFinals().matchWidgets.get(0);
						followingWid.setSecondNameVisible(false);
					}else{
						followingWid = getNextStep().matchWidgets.get(0);
						followingWid.setSecondNameVisible(false);
						followingWid = getSmallFinals().matchWidgets.get(0);
						followingWid.setFirstNameVisibe(false);
					}
				}
			}
		}
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

	/**
	 * Initialize the connections of each match-widget
	 */
	public void initConnections() {
		if(isSmallFinals()) return;
		Final_Step last = (isFinals() ? getPervStep().getPervStep() : getPervStep());
		if (last != null) {
			if(order <= 4 &! this.isSmallFinals()){
				for (int i = 0; i < matchWidgets.size(); i++) {
					matchWidgets.get(i).connectFirstPlayerWith(last.matchWidgets.get(i * 2));
					matchWidgets.get(i).connectSecondPlayerWith(
							last.matchWidgets.get(i * 2 +1 ));
				}
			}
			
		}
	}
	
	public boolean areSmallFinalsPlayed(){
		return steps.get(steps.size()-2).isFinished();
	}
	
	public boolean isSmallFinals(){
		//System.out.println("IsSmallFinals?");
		return (order == 3 ? true : false);
	}

	public boolean isFinals(){
		return (order == 4 ? true : false);
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
		return steps.get(steps.size()-2);
	}
	
	public Final_Step getFinals(){
		return steps.get(steps.size()-1);
	}
	
	public void publishToContestPanel(){
		drawHeadlineOnPanel(contestPanel);
		for(MatchWidget wid : matchWidgets){
			wid.publishToContestPanel();
		}	
	}
	
	public int getOffsetForPanel(JPanel p){
		return ((p.getWidth()-(5*scl(150)))/2);
	}
	
	private int scl(int val){
		return (int)(val*scl);
	}
	
	private void drawHeadlineOnPanel(JPanel p){
		if(p != null && p.getGraphics() != null){
			int offset = getOffsetForPanel(p);
			Graphics gc = p.getGraphics();
			int stepCount = order-1;
			Font oldFont = gc.getFont();
			Font newFont = new Font ("Arial", Font.BOLD, 14);
			gc.setFont(newFont);
			
			switch (order) {
			case 1:
				gc.drawString("Viertelfinale", (offset + (stepCount * scl(300))), 60);
				break;
			case 2:
				gc.drawString("Halbfinale", (offset + (stepCount * scl(300))), 60);
				break;
			case 3:
				gc.drawString("Großes und kleines Finale", (offset + (stepCount * scl(300))), 60);
				break;
			default:
			}
			gc.setFont(oldFont);
		}
	}
}
