package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * die groese einer schafsherde, aufgeteilt in die anzahl der herden des ersten
 * und des zweiten spielers und die anzahl der schaeferhunde
 * 
 * @author tkra
 * 
 */
@XStreamAlias(value = "sit:sheepsize")
public final class SheepSize {

	private int player1, player2, dogs;

	/**
	 * setzt alle groessen zurueck
	 */
	public void reset() {
		player1 = 0;
		player2 = 0;
		dogs = 0;
	}

	/**
	 * addiert eine andere herdengruesse zu dieser
	 */
	public void add(SheepSize other) {
		player1 += other.player1;
		player2 += other.player2;
		dogs += other.dogs;
	}

	/**
	 * vergroesert diese herde in abhaengigkeit einer spielrfarbe
	 */
	public void add(PlayerColor c) {
		switch (c) {
		case PLAYER1:
			player1++;
			break;

		case PLAYER2:
			player2++;
			break;

		case NOPLAYER:
			dogs++;
			break;
		}
	}

	/**
	 * liefer die gesamtzahl der schafe und hunde in dieser herde
	 */
	public int getSize() {
		return player1 + player2 + dogs;
	}

	/**
	 * liefert die anzahl der schafe oder hunde in dieser herde in abhaengigkeit
	 * einer spielerfarbe
	 */
	public int getSize(PlayerColor c) {
		int res = 0;
		switch (c) {
		case PLAYER1:
			res = player1;
			break;

		case PLAYER2:
			res = player2;
			break;

		case NOPLAYER:
			res = dogs;
			break;
		}

		return res;
	}

	@Override
	public String toString() {
		return player1 + "/" + player2 + (dogs > 0 ? "/" + dogs : "");
	}

}