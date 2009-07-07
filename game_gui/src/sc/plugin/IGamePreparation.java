package sc.plugin;

import java.util.List;

public interface IGamePreparation {

	List<ISlot> getSlots();
	void start();
	void pause();
	void cancel();
}
