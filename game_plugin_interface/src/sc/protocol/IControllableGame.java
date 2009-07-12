package sc.protocol;

public interface IControllableGame extends IPollsUpdates
{

	public abstract void next();

	public abstract void previous();

	public abstract void pause();

	public abstract void unpause();

}
