package sc.plugin2016;

import sc.plugin2016.util.Constants;

public class Board {

	public Field[][] fields;

	/**
	 * 
	 */
	public Board() {
		this.init();
	}

	/**
	 * 
	 * @param init
	 */
	public Board(Boolean init) {
		if (init)
			this.init();
		else
			this.makeClearBoard();
	}

	/**
	 * 
	 */
	private void init() {
		fields = new Field[Constants.SIZE][Constants.SIZE];
		fields[0][0] = new Field(FieldType.SWAMP);
		fields[0][Constants.SIZE - 1] = new Field(FieldType.SWAMP);
		fields[Constants.SIZE - 1][0] = new Field(FieldType.SWAMP);
		fields[Constants.SIZE - 1][Constants.SIZE - 1] = new Field(FieldType.SWAMP);
		for (int x = 1; x < Constants.SIZE - 1; x++) {
			fields[x][0] = new Field(FieldType.RED);
			fields[x][Constants.SIZE - 1] = new Field(FieldType.RED);
		}
		for (int y = 1; y < Constants.SIZE - 1; y++) {
			fields[0][y] = new Field(FieldType.BLUE);
			fields[Constants.SIZE - 1][y] = new Field(FieldType.BLUE);
		}
		//TODO füge Sümpfe ein
		for (int x = 1; x < Constants.SIZE - 1; x++) {
			for (int y = 1; y < Constants.SIZE - 1; y++) {
				fields[x][y] = new Field(FieldType.NORMAL);
			}
		}
	}

	private void makeClearBoard() {
		fields = new Field[Constants.SIZE][Constants.SIZE];
	}

	public Field getField(int x, int y) {
		return fields[x][y];
	}

	public Player getOwner(int x, int y) {
		return fields[x][y].getOwner();
	}

	public boolean equals(Object o) {
		if (o instanceof Board) {
			Board b = (Board) o;
			for (int x = 0; x < Constants.SIZE; x++) {
				for (int y = 0; y < Constants.SIZE; y++) {
					if(!this.fields[x][y].equals(b.fields[x][y])) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public Object clone() {
		Board clone = new Board(false);
		for (int x = 0; x < Constants.SIZE; x++) {
			for (int y = 0; y < Constants.SIZE; y++) {
				clone.fields[x][y] = this.fields[x][y].clone();
			}
		}
		return clone;
	}
}
