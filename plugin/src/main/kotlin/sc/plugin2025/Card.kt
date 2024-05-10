package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

/** Mögliche Aktionen, die durch das Ausspielen einer Karte ausgelöst werden können. */
@XStreamAlias(value = "card")
enum class Card(val moves: Boolean) {
    /** Nehme Karotten auf, oder leg sie ab. */
    TAKE_OR_DROP_CARROTS(false),
    /** Iß sofort einen Salat. */
    EAT_SALAD(false),
    /** Falle eine Position zurück. */
    FALL_BACK(true),
    /** Rücke eine Position vor. */
    HURRY_AHEAD(true),
    /** Karottenvorrat mit dem Gegner tauschen. */
    SWAP_CARROTS(false),
}

sealed class CardAction: HuIMove {
    abstract val card: Card
    override fun perform(state: GameState): IMoveMistake? {
        if(state.currentField != Field.HARE)
            return MoveMistake.CANNOT_PLAY_CARD
        if(state.currentPlayer.hasCard(card))
            return MoveMistake.CARD_NOT_OWNED
        return null
    }
    data class PlayCard(override val card: Card): CardAction() {
        override fun perform(state: GameState): IMoveMistake? {
            return super.perform(state) ?: run {
                when(card) {
                    Card.EAT_SALAD -> {
                        if(state.currentPlayer.salads == 0)
                            return MoveMistake.CANNOT_EAT_SALAD
                        state.eatSalad()
                        null
                    }
                    Card.FALL_BACK -> {
                        if(!state.isAhead())
                            return MoveMistake.CANNOT_PLAY_FALL_BACK
                        state.moveToField(state.otherPlayer.position - 1)
                    }
                    Card.HURRY_AHEAD -> {
                        if(state.isAhead())
                            return MoveMistake.CANNOT_PLAY_HURRY_AHEAD
                        state.moveToField(state.otherPlayer.position + 1)
                    }
                    Card.SWAP_CARROTS -> {
                        val car = state.currentPlayer.carrots
                        state.currentPlayer.carrots = state.otherPlayer.carrots
                        state.otherPlayer.carrots = car
                        null
                    }
                    Card.TAKE_OR_DROP_CARROTS -> throw NoWhenBranchMatchedException()
                }
            }
        }
    }
    class CarrotCard(val value: Int): CardAction() {
        override val card = Card.TAKE_OR_DROP_CARROTS
        override fun perform(state: GameState): IMoveMistake? {
            return super.perform(state)
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
