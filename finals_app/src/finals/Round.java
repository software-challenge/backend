package finals;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Round {
	public Round(String filename, Contestant winner, int ratingFirst,
			int ratingSecond) {
		super();
		this.filename = filename;
		this.winner = winner;
		this.ratingFirst = ratingFirst;
		this.ratingSecond = ratingSecond;
	}
	
	public Round() {
	}

	String filename;
	Contestant winner;
	int ratingFirst;
	int ratingSecond;

}
