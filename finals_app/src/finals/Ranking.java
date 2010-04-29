package finals;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Ranking {
	public Ranking(LinkedList<Contestant> standings) {
		super();
		this.standings = standings;
	}

	private List<Contestant> standings;

	public Contestant getContestrantAtRank(int rank) {
		Contestant cont = new Contestant("NONE", "NONE");
		if (rank < standings.size()) {
			cont = standings.get(rank);
		}
		return cont;
	}

	public int getRankForContestant(Contestant cont) {
		int rank = standings.size();
		for (int i = 0; i < standings.size(); i++) {
			if (cont.equals(standings.get(i))) {
				rank = i;
				break;
			}
		}
		return rank;
	}

	public List<Contestant> getRanking() {
		return this.standings;
	}

	public Contestant getWinner() {
		return getContestrantAtRank(1);
	}
	
	public void add(Contestant c, int i){
		for (int j = 0; j < standings.size(); j++) {
			if (c.rank <= standings.get(j).rank) {
				standings.add(c);
				return;
			}
		}
		standings.add(c);
	}
}
