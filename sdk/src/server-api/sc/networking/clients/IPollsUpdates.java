package sc.networking.clients;


public interface IPollsUpdates {
	void addListener(IUpdateListener u);

	void removeListener(IUpdateListener u);
}
