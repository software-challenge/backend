package sc.guiplugin.interfaces;

public interface IObservation {
	
	void start();

	void pause();

	void unpause();

	void cancel();
	
	void saveReplayToFile();
}
