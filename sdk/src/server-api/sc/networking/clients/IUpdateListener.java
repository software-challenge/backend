package sc.networking.clients;

public interface IUpdateListener {
	void onUpdate(Object sender);
	void onError(String errorMessage);
}
