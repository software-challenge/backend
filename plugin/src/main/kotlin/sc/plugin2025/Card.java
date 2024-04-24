package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2025.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * Eine Karte die auf einem Hasenfeld gespielt werden kann.
 */
@XStreamAlias(value = "card")
public class Card extends Action {

  @XStreamAsAttribute
  private CardType type;

  /**
   * Nur für TAKE_OR_DROP_CARROTS genutzt. Muss 20, 0 oder -20 sein.
   */
  @XStreamAsAttribute
  private int value;

  /**
   * Default Konstruktor. Setzt value auf 0 und order auf 0.
   * @param type Art der Karte
   */
  public Card(CardType type) {
    this.order = 0;
    this.value = 0;
    this.type = type;
  }

  /**
   * Konstruktor für eine Karte
   * @param type Art der Karte
   * @param order Index in der Aktionsliste des Zuges
   */
  public Card(CardType type, int order) {
    this.order = order;
    this.value = 0;
    this.type = type;
  }

  /**
   *
   * @param type Art der Karte
   * @param value Wert einer Karte nur für TAKE_OR_DROP_CARROTS genutzt (-20,0,20)
   * @param order Index in der Aktionsliste des Zuges
   */
  public Card(CardType type, int value, int order) {
    this.order = order;
    this.value = 0; // default value
    this.value = value;
    this.type = type;
  }


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

  public CardType getType() {
    return type;
  }

  public int getValue() {
    return value;
  }

  @Override
  public Card clone() {
    return new Card(this.type, this.value, this.order);
  }

  @Override
  public boolean equals(Object o) {
      return o instanceof Card && (this.value == ((Card) o).value) && (this.type == ((Card) o).type);
  }

  @Override
  public String toString() {
    return "Card "
            + this.getType()
            + ((this.getType() == CardType.TAKE_OR_DROP_CARROTS)?(" " + this.value):"")
            + " order " + this.order;
  }
}
