package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidMoveException;

/**
 * A card that is played.
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
   * KOnstruktor für eine Karte
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
   * @param value Wert einer Karte nur für TAKE_OR_DROP_CARROTS genutzt
   * @param order Index in der Aktionsliste des Zuges
   */
  public Card(CardType type, int value, int order) {
    this.order = order;
    this.value = 0; // default value
    setValue(value);
    this.type = type;
  }


  @Override
  public void perform(GameState state) throws InvalidMoveException {
    switch (type) { // TODO exception in else cases
      case EAT_SALAD:
        if (GameRuleLogic.isValidToPlayEatSalad(state)) {
          state.getCurrentPlayer().eatSalad();
          if (state.isFirst(state.getCurrentPlayer())) {
            state.getCurrentPlayer().changeCarrotsAvailableBy(10);
          } else {
            state.getCurrentPlayer().changeCarrotsAvailableBy(30);
          }
        } else {
          throw new InvalidMoveException("Das Ausspielen der EAT_SALAD Karte ist nicht möglich.");
        }
        break;
      case FALL_BACK:
        if (GameRuleLogic.isValidToPlayFallBack(state)) {
          state.getCurrentPlayer().setFieldNumber(state.getOpponent(state.getCurrentPlayer()).getFieldIndex() - 1);
        } else {
          throw new InvalidMoveException("Das Ausspielen der FALL_BACK Karte ist nicht möglich.");
        }
        break;
      case HURRY_AHEAD:
        if (GameRuleLogic.isValidToPlayHurryAhead(state)) {
          state.getCurrentPlayer().setFieldNumber(state.getOpponent(state.getCurrentPlayer()).getFieldIndex() + 1);
        } else {
          throw new InvalidMoveException("Das Ausspielen der FALL_BACK Karte ist nicht möglich.");
        }
        break;
      case TAKE_OR_DROP_CARROTS:
        if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(state, this.getValue())) {
          state.getCurrentPlayer().changeCarrotsAvailableBy(this.getValue());
        } else {
          throw new InvalidMoveException("Das Ausspielen der TAKE_OR_DROP_CARROTS Karte ist nicht möglich.");
        }
        break;
    }
    // remove playes card
    state.getCurrentPlayer().setCards(state.getCurrentPlayer().getCardsWithout(this.type));
  }

  public CardType getType() {
    return type;
  }

  public void setType(CardType type) {
    this.type = type;
  }

  public int getValue() {
    return value;
  }

  /**
   * Setzt den Wert nur auf 20, 0 oder -20 andere Werte werden nicht akzeptiert
   * @param value Wert
   */
  public void setValue(int value) {
    if (value == 0 || value == -20 || value == 20) {
      this.value = value;
    }
  }

  @Override
  public Card clone() {
    return new Card(this.type, this.value, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Card) {
      return (this.value == ((Card) o).value) && (this.type == ((Card) o).type);
    }
    return false;
  }
}
