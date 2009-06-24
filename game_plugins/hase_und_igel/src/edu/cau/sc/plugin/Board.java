package edu.cau.sc.plugin;

import edu.cau.sc.plugin.Field.FieldTypes;

/**
 * The representation of a <code>Board</code> in Hase und Igel
 */
public class Board
{
	private final static int FIELD_SIZE = 5;
	
	private Field[] fields;

	public Board()
	{
		createStdBoard();
	}

	protected void setField(int index, Field field)
	{
		this.fields[index] = field;
	}

	public Field getField(int index)
	{
		return this.fields[index];
	}

	public void setFieldTaken(int index, boolean taken)
	{
		Field currentField = getField(index);
		
		setField(index, currentField);
	}

	// creates the standard board for Hase und Igel
	private void createStdBoard()
	{
		this.fields = new Field[FIELD_SIZE];
		this.fields[0] = new Field(FieldTypes.START);
		this.fields[1] = new Field(FieldTypes.SALAD);
		this.fields[2] = new Field(FieldTypes.SALAD);
		this.fields[2] = new Field(FieldTypes.SALAD);
		this.fields[2] = new Field(FieldTypes.GOAL);
	}
}
