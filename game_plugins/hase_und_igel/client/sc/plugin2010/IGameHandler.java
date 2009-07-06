package sc.plugin2010;

public interface IGameHandler
{
	void onUpdate(BoardUpdated bu);
	void onUpdate(PlayerUpdated pu);
	Move onAction();
}
