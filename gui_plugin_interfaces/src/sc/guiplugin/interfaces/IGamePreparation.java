package sc.guiplugin.interfaces;

import java.util.List;

public interface IGamePreparation {

	List<ISlot> getSlots();
	
	IObservation observe();
}
