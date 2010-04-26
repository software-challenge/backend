package finals;

public class Round {
	public Round(String filename, Contestant winner, int ratingFirst,
			int ratingSecond) {
		super();
		this.filename = filename;
		this.winner = winner;
		this.ratingFirst = ratingFirst;
		this.ratingSecond = ratingSecond;
	}

	String filename;
	Contestant winner;
	int ratingFirst;
	int ratingSecond;

}
