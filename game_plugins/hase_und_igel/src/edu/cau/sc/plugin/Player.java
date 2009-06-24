package edu.cau.sc.plugin;

import java.util.HashSet;
import java.util.Set;

import sc.api.plugins.IPlayer;
import sc.api.plugins.IPlayerListener;


/**
 * implements a player class for the game Hase und Igel
 */
public class Player implements IPlayer
{
	// the count of carrots the player has
	private int		carrots;

	// the count of salads the player has
	private int		salads			= 3;

	// indicates whether the player has to pause for one round
	private boolean	pause			= false;

	// indicates the player no
	private int		playerno;

	private int		fieldPosition	= 0;
	
	private Set<IPlayerListener>	playerListeners		= new HashSet<IPlayerListener>();

	public Player(int playerno, int carrots)
	{
		this.playerno = playerno;
		this.carrots = carrots;
	}

	// ///////////////////////////////////////////////////////////////////
	// Getter and Setter Methods
	// ///////////////////////////////////////////////////////////////////

	public void setCarrots(int carrots)
	{
		this.carrots = carrots;
	}

	public int getCarrots()
	{
		return carrots;
	}

	public void setSalads(int salads)
	{
		this.salads = salads;
	}

	public int getSalads()
	{
		return salads;
	}

	public void setPause(boolean pause)
	{
		this.pause = pause;
	}

	public boolean isPause()
	{
		return pause;
	}

	public int getPlayerno()
	{
		return playerno;
	}

	public void setFieldPosition(int fieldPosition)
	{
		this.fieldPosition = fieldPosition;
	}

	public int getFieldPosition()
	{
		return fieldPosition;
	}

	@Override
	public void addPlayerListener(IPlayerListener listener)
	{
		this.playerListeners.add(listener);
	}

	@Override
	public void removePlayerListener(IPlayerListener listener)
	{
		this.playerListeners.remove(listener);
	}
}
