package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

/** Mögliche Aktionen, die durch das Ausspielen einer Karte ausgelöst werden können. */
@XStreamAlias(value = "card")
enum class Card {
    /** Nehme Karotten auf, oder leg sie ab. */
    TAKE_OR_DROP_CARROTS,
    /** Iß sofort einen Salat. */
    EAT_SALAD,
    /** Falle eine Position zurück. */
    FALL_BACK,
    /** Rücke eine Position vor. */
    HURRY_AHEAD
}

sealed class CardAction: HuIMove {
    abstract val card: Card
    data class PlayCard(override val card: Card): CardAction() {
        override fun perform(state: GameState): IMoveMistake? {
            TODO("Not yet implemented")
        }
    }
    class CarrotCard(val value: Int): CardAction() {
        override val card = Card.TAKE_OR_DROP_CARROTS
        override fun perform(state: GameState): IMoveMistake? {
            TODO("Not yet implemented")
        }
    }
    
    
    /*
  @Override
  public void perform(GameState state) throws InvalidMoveException {
    state.getCurrentPlayer().setMustPlayCard(false); // player played a card
    switch (type) { // when entering a HARE field with fall_back or hurry ahead, player has to play another card
      case EAT_SALAD:
        if (GameRuleLogic.isValidToPlayEatSalad(state)) {
          state.getCurrentPlayer().eatSalad();
          if (state.isFirst(state.getCurrentPlayer())) {
            state.getCurrentPlayer().changeCarrotsBy(10);
          } else {
            state.getCurrentPlayer().changeCarrotsBy(30);
          }
        } else {
          throw new InvalidMoveException("Das Ausspielen der EAT_SALAD Karte ist nicht möglich.");
        }
        break;
      case FALL_BACK:
        if (GameRuleLogic.isValidToPlayFallBack(state)) {
          state.getCurrentPlayer().setFieldIndex(state.getOtherPlayer().getFieldIndex() - 1);
          if (state.fieldOfCurrentPlayer() == Field.HARE) {
            state.getCurrentPlayer().setMustPlayCard(true);
          }
        } else {
          throw new InvalidMoveException("Das Ausspielen der FALL_BACK Karte ist nicht möglich.");
        }
        break;
      case HURRY_AHEAD:
        if (GameRuleLogic.isValidToPlayHurryAhead(state)) {
          state.getCurrentPlayer().setFieldIndex(state.getOtherPlayer().getFieldIndex() + 1);
          if (state.fieldOfCurrentPlayer() == Field.HARE) {
            state.getCurrentPlayer().setMustPlayCard(true);
          }
        } else {
          throw new InvalidMoveException("Das Ausspielen der HURRY_AHEAD Karte ist nicht möglich.");
        }
        break;
      case TAKE_OR_DROP_CARROTS:
        if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(state, this.getValue())) {
          state.getCurrentPlayer().changeCarrotsBy(this.getValue());
        } else {
          throw new InvalidMoveException("Das Ausspielen der TAKE_OR_DROP_CARROTS Karte ist nicht möglich.");
        }
        break;
    }
    state.setLastAction(this);
    // remove player card
    state.getCurrentPlayer().setCards(state.getCurrentPlayer().getCardsWithout(this.type));
  }
  */
}
