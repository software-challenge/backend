package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
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
  public void perform(GameState state, Player player) throws InvalidMoveException {
    // check if current field is a rabbit field
    if (state.getBoard().getTypeAt(player.getFieldIndex()) != FieldType.RABBIT) {
      throw new InvalidMoveException("Das Spielen einer Karte ist nur auf einem Hasenfeld erlaubt.");
    }
    switch (type) {
      case EAT_SALAD:
        player.eatSalad();
        if (state.isFirst(player)) {
          player.changeCarrotsAvailableBy(10);
        } else {
          player.changeCarrotsAvailableBy(30);
        }
        break;
      case FALL_BACK:
        // TODO check whether this is possible
        if (state.isFirst(player)) {
          player.setFieldNumber(state.getOpponent(player).getFieldIndex() - 1);
        }
        break;
      case HURRY_AHEAD:
        // TODO check whether this is possible
        if (!state.isFirst(player)) {
          player.setFieldNumber(state.getOpponent(player).getFieldIndex() + 1);
        }
        break;
      case TAKE_OR_DROP_CARROTS:
        player.changeCarrotsAvailableBy(this.getValue());
        break;
    }
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
}
