package finals;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Match {
	
	public Match() {
		
	}
	
	public Match(Contestant first, Contestant second, List<Round> rounds) {
		super();
		this.first = first;
		this.second = second;
		this.rounds = rounds;
	}

	public Match(Contestant first, Contestant second) {
		super();
		this.first = first;
		this.second = second;
		this.rounds = new LinkedList<Round>();
	}

	Contestant first;
	Contestant second;
	List<Round> rounds;
	int firstCurrentScore = 0;
	int secondCurrentScore = 0;
	private int currentStep = 0;

	public void addRound(Round round) {
		this.rounds.add(round);
	}

	public void doNextStep() {
		if (currentStep < rounds.size()) {
			Round currentRound = rounds.get(currentStep);
			if (currentRound.winner.name.equals(first.name)) {
				firstCurrentScore++;
			} else {
				secondCurrentScore++;
			}
			currentStep++;
		}
	}

	public void undoLastStep() {
		if (currentStep > 0) {
			Round currentRound = rounds.get(currentStep-1);
			if (currentRound.winner.name.equals(first.name)) {
				firstCurrentScore--;
			} else {
				secondCurrentScore--;
			}
			currentStep--;
		}
	}

	public void undoAllSteps() {
		currentStep = 0;
		firstCurrentScore = 0;
		secondCurrentScore = 0;
	}

	public void doAllSteps() {
		undoAllSteps();
		for (Round round : rounds) {
			if (round.winner.equals(first)) {
				firstCurrentScore++;
			} else {
				secondCurrentScore++;
			}
		}
	}

	public Contestant getFirst() {
		return first;
	}

	public Contestant getSecond() {
		return second;
	}

	public int getFirstCurrentScore() {
		return firstCurrentScore;
	}

	public int getSecondCurrentScore() {
		return secondCurrentScore;
	}

	public List<Round> getRounds() {
		return rounds;
	}

	public boolean isFinished() {
		return currentStep == rounds.size();
	}

	public int getCurrentStep() {
		return this.currentStep;
	}
	
}
