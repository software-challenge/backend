package sc.networking.clients;

import sc.shared.GameResult;

public interface IControllableGame extends IPollsUpdates
{
	public void next();

	public void previous();

	public void pause();

	public void unpause();

	public boolean hasNext();

	public boolean hasPrevious();

	public boolean isPaused();
	
	public void cancel();

	public Object getCurrentState();
	
	public Object getCurrentError();

	public boolean isAtEnd();

	public boolean isAtStart();

	public void goToFirst();

	public void goToLast();

	public boolean canTogglePause();
	
	public GameResult getResult();

	public boolean isGameOver();

	public boolean isReplay();
}
