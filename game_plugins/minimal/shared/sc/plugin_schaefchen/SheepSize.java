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

	private int player1, player2;

	/**
	 * setzt alle groessen zurueck
	 */
	public void reset() {
		player1 = 0;
		player2 = 0;
	}

	/**
	 * addiert eine andere herdengruesse zu dieser
	 */
	public void add(SheepSize other) {
		player1 += other.player1;
		player2 += other.player2;
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

		default:
			break;
		}
	}

	/**
	 * liefer die gesamtzahl der schafe in dieser herde
	 */
	public int getSize() {
		return player1 + player2;
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

		default:
			res = 0;
			break;
		}

		return res;
	}
}