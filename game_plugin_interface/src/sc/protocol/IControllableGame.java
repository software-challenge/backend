package sc.protocol;

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
}
