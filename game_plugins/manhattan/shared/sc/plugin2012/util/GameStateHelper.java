package sc.plugin2012.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sc.plugin2012.Card;

import static sc.plugin2012.util.Constants.SLOTS_PER_CITY;
import static sc.plugin2012.util.Constants.CARDS_PER_SLOT;

public class GameStateHelper {

	public static List<Card> createCardStack() {

		List<Card> cardStack = new ArrayList<Card>(SLOTS_PER_CITY
				* CARDS_PER_SLOT);
		
		for(int slot = 0;slot<SLOTS_PER_CITY; slot++){
			for(int card = 0; card <CARDS_PER_SLOT; card++){
				cardStack.add(new Card(slot));
			}
		}

		Collections.shuffle(cardStack, new SecureRandom());
		return cardStack;

	}

}
