package finals;

import java.awt.Panel;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Final_Step {

	public Final_Step(Panel pan, Panel contestPanel, List<Match> matches,
			List<Contestant> contestants, Ranking ranking,
			List<Final_Step> steps) {
		this(pan, contestPanel, matches, contestants, ranking, steps, false);
	}

	public Final_Step(Panel pan, Panel contestPanel, List<Match> matches,
			List<Contestant> contestants, Ranking ranking,
			List<Final_Step> steps, boolean showTeamsFromBeginning) {
		super();
		this.steps = steps;
		this.matches = new LinkedList<MatchWidget>();
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
			MatchWidget wid = new MatchWidget(pan, contestPanel, 1f, new Point(
					50 + stepCount * 250, currentY), m);
			this.matches.add(wid);
			currentY += yPossible;
		}
		this.contestants = contestants;
		this.ranking = ranking;
		if (showTeamsFromBeginning) {
			for (MatchWidget w : this.matches) {
				w.setFirstNameVisibe(true);
				w.setSecondNameVisible(true);
			}
		}
	}

	List<MatchWidget> matches;
	List<Contestant> contestants;
	Ranking ranking;
	List<Final_Step> steps;

	public boolean isFinished() {
		boolean finished = true;
		for (MatchWidget m : matches) {
			if (!m.getMatch().isFinished()) {
				finished = false;
				break;
			}

		}
		return finished;
	}

	public void stepForward() {

		for (int i = 0; i < matches.size(); i++) {
			MatchWidget m = matches.get(i);
			if (!m.getMatch().isFinished()) {
				System.out.println(m);
				m.setSelected(true);
				m.getMatch().doNextStep();
				return;
			} else {
				m.setSelected(false);
				if (i % 2 == 0) {
					getNextStep().matches.get(i / 2).setFirstNameVisibe(true);
					getNextStep().matches.get(i / 2).paint();
				} else {
					getNextStep().matches.get(i / 2).setSecondNameVisible(true);
					getNextStep().matches.get(i / 2).paint();
				}

			}
		}
	}

	public void repaint() {
		for (MatchWidget match : matches) {
			match.paint();
		}
	}

	public void stepBackward() {
		for (int i = matches.size() - 1; i >= 0; i--) {
			if (matches.get(i).getMatch().getCurrentStep() > 0) {
				matches.get(i).getMatch().undoLastStep();
				return;
			}
		}
		repaint();
	}

	public void doAllMatches() {
		for (MatchWidget m : matches) {
			m.getMatch().doAllSteps();
		}
	}

	public void undoAllMatches() {
		for (MatchWidget m : matches) {
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
		Final_Step last = getPervStep();
		if (last != null) {
			for (int i = 0; i < matches.size(); i++) {
				matches.get(i).connectFirstPlayerWith(last.matches.get(i * 2));
				matches.get(i).connectSecondPlayerWith(
						last.matches.get(i * 2 + 1));
			}
		}
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
}
