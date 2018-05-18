package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugin2018.util.Constants;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.ArrayList;

/**
 * Ein {@code GameState} beinhaltet alle Informationen, die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getRound()}), die
 * der Spielserver als Antwort von einem der beiden Spieler (
 * {@link #getCurrentPlayer() getCurrentPlayer()}) erwartet. Weiterhin gehoeren
 * die Informationen ueber die beiden Spieler und das Spielfeld zum Zustand.
 * Zuseatzlich wird ueber den zuletzt getaetigeten Spielzung und ggf. ueber das
 * Spielende informiert.
 * <p>
 * <p>
 * Der {@code GameState} ist damit das zentrale Objekt ueber das auf alle
 * wesentlichen Informationen des aktuellen Spiels zugegriffen werden kann.
 * <p>
 * <p>
 * Der Spielserver sendet an beide teilnehmenden Spieler nach jedem getaetigten
 * Zug eine neue Kopie des {@code GameState}, in dem der dann aktuelle Zustand
 * beschrieben wird. Informationen ueber den Spielverlauf sind nur bedingt ueber
 * den {@code GameState} erfragbar und muessen von einem Spielclient daher bei
 * Bedarf selbst mitgeschrieben werden.
 * <p>
 * <p>
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen abgefragt werden.
 *
 * @author Niklas, Sören
 */
@XStreamAlias(value = "state")
public class GameState implements Cloneable {

  @XStreamOmitField
  private static final Logger logger = LoggerFactory.getLogger(GameState.class);
  /** momentane Rundenzahl */
  @XStreamAsAttribute
  private int turn;

  /** Farbe des Startspielers */
  @XStreamAsAttribute
  private PlayerColor startPlayer;

  /** Farbe des aktuellen Spielers */
  @XStreamAsAttribute
  private PlayerColor currentPlayer;

  /** der rote Spieler */
  private Player red;
  /** der blaue Spieler */
  private Player blue;

  /** Das Spielbrett */
  private Board board;

  /** letzter getaetigter Zug */
  private Move lastMove;

  /**
   * Erzeugt einen neuen {@code GameState}, in dem alle Informationen so gesetzt sind,
   * wie sie zu Beginn eines Spiels, bevor die Spieler beigetreten sind, gueltig sind.
   * <p>
   * <p>
   * Dieser Konstruktor ist nur fuer den Server relevant und sollte vom
   * Client nicht aufgerufen werden!
   * <p>
   * Das Spielfeld wird zufällig aufgebaut.
   */
  public GameState() {
    this.turn = 0;
    this.currentPlayer = PlayerColor.RED;
    this.startPlayer = PlayerColor.RED;
    this.board = new Board();
    this.red = new Player(PlayerColor.RED);
    this.blue = new Player(PlayerColor.BLUE);
  }

  /**
   * Erzeugt einen neuen {@code GameState} mit denselben Eigenschaften von
   * stateToClone. Fuer eigene Implementierungen.<br>
   * Hinweis: Es ist effizienter, direkt das Ergebnis von {@link #clone()} zu verwenden
   */
  protected GameState(GameState stateToClone) {
    GameState clone = stateToClone.clone();
    setRedPlayer(clone.getPlayer(PlayerColor.RED));
    setBluePlayer(clone.getPlayer(PlayerColor.BLUE));
    setLastMove(clone.getLastMove());
    setBoard(clone.getBoard());
    setCurrentPlayer(clone.getCurrentPlayerColor());
  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   */
  @Override
  public GameState clone() {
    GameState clone;
    try {
      clone = (GameState) super.clone();
    } catch (CloneNotSupportedException e) {
      // impossible
      throw new RuntimeException("Cloning of GameState failed!", e);
    }
    if (red != null)
      clone.red = this.red.clone();
    if (blue != null)
      clone.blue = this.blue.clone();
    if (lastMove != null)
      clone.lastMove = this.lastMove.clone();
    if (board != null)
      clone.board = this.board.clone();
    if (currentPlayer != null)
      clone.currentPlayer = this.currentPlayer;
    clone.startPlayer = this.startPlayer;
    clone.turn = this.turn;
    return clone;
  }

  /**
   * Fuegt einem Spiel einen weiteren Spieler hinzu.
   * <p>
   * <p>
   * Diese Methode ist nur fuer den Spielserver relevant und sollte vom
   * Spielclient i.A. nicht aufgerufen werden!
   *
   * @param player Der hinzuzufuegende Spieler.
   */
  public void addPlayer(Player player) {
    if (player.getPlayerColor() == PlayerColor.RED) {
      red = player;
    } else if (player.getPlayerColor() == PlayerColor.BLUE) {
      blue = player;
    }
  }

  /**
   * Gibt das Spielfeld zurueck
   *
   * @return das Spielfeld
   */
  public Board getBoard() {
    return this.board;
  }

  /**
   * Liefert den Spieler als {@code Player}-Objekt, der als die entsprechende Farbe spielt
   *
   * @param color die Farbe des gefragten Spielers
   */
  public Player getPlayer(PlayerColor color) {
    return color == PlayerColor.RED ? red : blue;
  }

  /**
   * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan am Zug
   * ist.
   *
   * @return Der Spieler, der momentan am Zug ist.
   */
  public Player getCurrentPlayer() {
    return getPlayer(currentPlayer);
  }

  /**
   * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan am Zug
   * ist. Dies ist aequivalent zum Aufruf
   * {@code getCurrentPlayer().getPlayerColor()}, aber schneller.
   *
   * @return Die Farbe des Spielers, der momentan am Zug ist.
   */
  public PlayerColor getCurrentPlayerColor() {
    return currentPlayer;
  }

  /**
   * Nur für den Server relevant
   *
   * @param playerColor PlayerColor of new currentPlayer
   */
  protected void setCurrentPlayer(PlayerColor playerColor) {
    this.currentPlayer = playerColor;
  }

  /**
   * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan nicht am
   * Zug ist.
   *
   * @return Der Spieler, der momentan nicht am Zug ist.
   */
  public Player getOtherPlayer() {
    return getPlayer(getOtherPlayerColor());
  }

  /**
   * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan nicht am
   * Zug ist. Dies ist aequivalent zum Aufruf @
   * {@code getCurrentPlayerColor.opponent()} oder
   * {@code getOtherPlayer().getPlayerColor()}, aber etwas effizienter.
   *
   * @return Die Farbe des Spielers, der momentan nicht am Zug ist.
   */
  public PlayerColor getOtherPlayerColor() {
    return currentPlayer.opponent();
  }

  /** @deprecated ersetzt durch {@link #getPlayer(PlayerColor)} */
  public Player getRedPlayer() {
    return red;
  }

  /** @deprecated ersetzt durch {@link #getPlayer(PlayerColor)} */
  public Player getBluePlayer() {
    return blue;
  }

  /**
   * Nur für den Server relevant
   *
   * @param red roter Spieler
   */
  protected void setRedPlayer(Player red) {
    this.red = red;
  }

  /**
   * Nur für den Server relevant
   *
   * @param blue blauer Spieler
   */
  protected void setBluePlayer(Player blue) {
    this.blue = blue;
  }

  /** @return Der Spieler, der das Spiel begonnen hat. */
  public Player getStartPlayer() {
    return startPlayer == PlayerColor.RED ? red : blue;
  }

  /** @return Die Farbe des Spielers, der das Spiel begonnen hat. */
  public PlayerColor getStartPlayerColor() {
    return startPlayer;
  }

  /** wechselt den Spieler, der aktuell an der Reihe ist, anhand der Zugnummer({@link #turn}) */
  public void switchCurrentPlayer() {
    if (turn % 2 == 0) {
      currentPlayer = PlayerColor.RED;
    } else {
      currentPlayer = PlayerColor.BLUE;
    }
  }

  /**
   * Überprüft, ob ein Feld durch einen Spieler belegt ist, sodass niemand darauf ziehen kann.
   * (da Zielfeld von mehreren betretbar, bei Zielfeld immer false)
   *
   * @param index der Index des Feldes
   *
   * @return Gibt true zurück, falls sich ein Spieler auf dem Feld befindet und es nicht das Zielfeld ist
   */
  public final boolean isOccupied(final int index) {
    return (red.getFieldIndex() == index || blue.getFieldIndex() == index)
            && (index != Constants.LAST_FIELD_INDEX);
  }

  /**
   * Überprüft, ob der angegebene Spieler an erster Stelle ist. Wenn sich beide
   * Spieler im Ziel befinden wird zusätzlich überprüft, ob <code>player</code>
   * weniger Karotten besitzt als der Gegenspieler.
   *
   * @param player überprüfter Spieler
   *
   * @return true, falls Spieler an erster Stelle
   */
  public final boolean isFirst(final Player player) {
    Player o = this.getOpponent(player);
    boolean isFirst = o.getFieldIndex() <= player.getFieldIndex();
    if (player.inGoal() && o.getFieldIndex() == player.getFieldIndex())
      isFirst = isFirst
              && player.getCarrots() < o.getCarrots();
    return isFirst;
  }

  /**
   * Gibt den Feldtypen an einem bestimmten Index zurück. Liegt der
   * gewählte Index vor dem Startpunkt oder hinter dem Ziel, so wird
   * {@link FieldType#INVALID} zurückgegeben.
   *
   * @param index die Index auf der Rennstrecke
   *
   * @return Feldtyp an index
   */
  public final FieldType getTypeAt(final int index) {
    return board.getTypeAt(index);
  }

  /**
   * Findet das nächste Spielfeld vom Typ <code>type</code> beginnend bei
   * Feldnummer <code>index</code> auf diesem Spielbrett.
   *
   * @param type  Feldtyp
   * @param index Index
   *
   * @return Index des nächsten Feldes genannten Typs
   */
  public final int getNextFieldByType(FieldType type, int index) {
    return this.board.getNextFieldByType(type, index);
  }

  /**
   * Findet das vorherige Spielfeld vom Typ <code>type</code> beginnend an Index
   * <code>index</code> auf diesem Spielbrett.
   *
   * @param type  Feldtyp
   * @param index Index
   *
   * @return Index des vorherigen Feldes genannten Typs
   */
  public final int getPreviousFieldByType(FieldType type, int index) {
    return this.board.getPreviousFieldByType(type, index);
  }

  /**
   * liefert die aktuelle Zugzahl
   *
   * @return Nummer des aktuellen Zuges (Zaehlung beginnt mit 0)
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Setzt die aktuelle Zugzahl. Nur für den Server relevant
   *
   * @param turn neue Zugzahl
   */
  public void setTurn(int turn) throws InvalidGameStateException {
    int turnLimit = Constants.ROUND_LIMIT * 2;
    if (turn > turnLimit) {
      throw new InvalidGameStateException("Turn " + turn + " exceeded maxTurn " + turnLimit);
    }
    this.turn = turn;
  }

  /**
   * liefert die aktuelle Rundenzahl
   *
   * @return aktuelle Rundenzahl
   */
  public int getRound() {
    return turn / 2;
  }

  /**
   * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
   * Einträgen:
   * <ul>
   * <li>[0] - Punktekonto des Spielers (Flussfortschritt und Passagiere)
   * <li>[1] - Anzahl eingesammelter Passagiere
   * </ul>
   *
   * @param player Spieler
   *
   * @return Array mit Statistiken
   */
  public int[] getPlayerStats(Player player) {
    assert player != null;
    return getPlayerStats(player.getPlayerColor());
  }

  /**
   * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
   * Einträgen:
   * <ul>
   * <li>[0] - Punktekonto des Spielers (Flussfortschritt und Passagiere)
   * <li>[1] - Anzahl eingesammelter Passagiere
   * </ul>
   *
   * @param playerColor Farbe des Spielers
   *
   * @return Array mit Statistiken
   */
  public int[] getPlayerStats(PlayerColor playerColor) {
    assert playerColor != null;

    if (playerColor == PlayerColor.RED) {
      return getGameStats()[Constants.GAME_STATS_RED_INDEX];
    } else {
      return getGameStats()[Constants.GAME_STATS_BLUE_INDEX];
    }
  }

  /**
   * Liefert Statusinformationen zum Spiel. Diese sind ein Array der
   * {@link #getPlayerStats(PlayerColor) Spielerstats}, wobei getGameStats()[0],
   * einem Aufruf von getPlayerStats(PlayerColor.RED) entspricht.
   *
   * @return Statusinformationen beider Spieler
   *
   * @see #getPlayerStats(PlayerColor)
   */
  public int[][] getGameStats() {

    int[][] stats = new int[2][2];

    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] = this.red.getFieldIndex();
    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_CARROTS] = this.red.getCarrots();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX] = this.blue.getFieldIndex();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_CARROTS] = this.blue.getCarrots();
    return stats;

  }

  /**
   * liefert die Namen den beiden Spieler
   *
   * @return Namen der Spieler
   */
  public String[] getPlayerNames() {
    return new String[]{red.getDisplayName(), blue.getDisplayName()};

  }

  /**
   * Gibt die angezeigte Punktzahl des Spielers zurueck.
   *
   * @param playerColor Farbe des Spielers
   *
   * @return Punktzahl des Spielers
   */
  public int getPointsForPlayer(PlayerColor playerColor) {
    return getPlayer(playerColor).getFieldIndex();
  }

  /** Ueberschreibt das aktuelle Spielbrett. Fuer eigene Implementierungen. */
  protected void setBoard(Board newValue) {
    board = newValue;
  }

  public Player getOpponent(Player player) {
    return getPlayer(player.getPlayerColor().opponent());
  }

  /**
   * Setzt letzten Zug. Nur für den Server relevant.
   *
   * @param lastMove letzter Zug
   */
  protected void setLastMove(Move lastMove) {
    this.lastMove = lastMove;
  }

  /**
   * Setzt letzte Aktion eines Spielers. Für den Server in der Zugvalidierung relevant.
   *
   * @param action letzte Aktion
   */
  public void setLastAction(Action action) {
    if (action instanceof Skip) {
      return;
    }
    getCurrentPlayer().setLastNonSkipAction(action);
  }

  /**
   * Gibt den letzten Zugzurück
   *
   * @return letzter Zug
   */
  public Move getLastMove() {
    return this.lastMove;
  }


  /**
   * Gibt die letzte Aktion des Spielers zurück. Nötig für das erkennen von ungültigen Zügen.
   *
   * @param player Spieler
   *
   * @return letzte Aktion die nicht Skip war
   */
  public Action getLastNonSkipAction(Player player) {
    return getLastNonSkipAction(player.getPlayerColor());
  }

  /**
   * Gibt die letzte Aktion des Spielers der entsprechenden Farbe zurück. Nötig für das erkennen von ungültigen Zügen.
   *
   * @param playerColor Spielerfarbe
   *
   * @return letzte Aktion die nicht Skip war
   */
  public Action getLastNonSkipAction(PlayerColor playerColor) {
    return getPlayer(playerColor).getLastNonSkipAction();
  }

  /**
   * Git das Feld des derzeitigen Spielers zurück
   *
   * @return Feldtyp
   */
  public FieldType fieldOfCurrentPlayer() {
    return this.getTypeAt(this.getCurrentPlayer().getFieldIndex());
  }

  /**
   * Überprüft ob sich der derzeitige Spieler auf einem Hasenfeld befindet.
   *
   * @return true, falls auf Hasenfeld
   */
  public boolean isOnHareField() {
    return fieldOfCurrentPlayer().equals(FieldType.HARE);
  }

  public ArrayList<Move> getPossibleMoves() {
    ArrayList<Move> possibleMove = new ArrayList<>();
    if (GameRuleLogic.isValidToEat(this)) {
      // Wenn ein Salat gegessen werden kann, muss auch ein Salat gegessen werden
      Move move = new Move(new EatSalad());
      possibleMove.add(move);
      return possibleMove;
    }
    if (GameRuleLogic.isValidToExchangeCarrots(this, 10)) {
      possibleMove.add(new Move(new ExchangeCarrots(10)));
    }
    if (GameRuleLogic.isValidToExchangeCarrots(this, -10)) {
      possibleMove.add(new Move(new ExchangeCarrots(-10)));
    }
    if (GameRuleLogic.isValidToFallBack(this)) {
      possibleMove.add(new Move(new FallBack()));
    }
    // Generiere mögliche Vorwärtszüge
    for (int i = 1; i <= GameRuleLogic.calculateMoveableFields(this.getCurrentPlayer().getCarrots()); i++) {
      GameState clone = this.clone();
      // Überprüfe ob Vorwärtszug möglich ist
      if (GameRuleLogic.isValidToAdvance(clone, i)) {
        Advance tryAdvance = new Advance(i);
        try {
          tryAdvance.perform(clone);
        } catch (InvalidMoveException e) {
          // Sollte nicht passieren, da Zug valide ist
          e.printStackTrace();
          break;
        }
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(tryAdvance);
        // überprüfe, ob eine Karte gespielt werden muss/kann
        if (clone != null && clone.getCurrentPlayer().mustPlayCard()) {
          possibleMove.addAll(clone.checkForPlayableCards(actions));
        } else {
          // Füge möglichen Vorwärtszug hinzu
          possibleMove.add(new Move(actions));
        }
      }
    }
    if (possibleMove.isEmpty()) {
      logger.debug("GameState" + this.hashCode() + ": Muss aussetzen");
      possibleMove.add(new Move(new Skip()));
    }
    return possibleMove;
  }

  /**
   * Überprüft für übergebenen GameState und bisher getätigte Züge,
   * ob das Ausspielen einer Karte nötig/möglich ist
   *
   * @param actions bisherige Aktionenliste
   *
   * @return mögliche Züge
   */
  private ArrayList<Move> checkForPlayableCards(ArrayList<Action> actions) {
    ArrayList<Move> possibleMove = new ArrayList<>();
    if (this.getCurrentPlayer().mustPlayCard()) { // überprüfe, ob eine Karte gespielt werden muss
      if (GameRuleLogic.isValidToPlayEatSalad(this)) {
        actions.add(new Card(CardType.EAT_SALAD, actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.EAT_SALAD, 1));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(this, 20)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(this, -20)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, -20, actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, -20, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(this, 0)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 0, actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, 0, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayHurryAhead(this)) {
        Card card = new Card(CardType.HURRY_AHEAD, actions.size());
        actions.add(card);
        // Überprüfe ob wieder auf Hasenfeld gelandet:
        GameState clone = this.clone();
        try {
          card.perform(clone);
        } catch (InvalidMoveException e) {
          // Sollte nie passieren
          e.printStackTrace();
        }
        if (clone != null && clone.getCurrentPlayer().mustPlayCard()) {
          ArrayList<Move> moves = clone.checkForPlayableCards(actions);
          if (!moves.isEmpty()) {
            possibleMove.addAll(moves);
          }
        } else {
          possibleMove.add(new Move(actions));
        }

        actions.remove(new Card(CardType.HURRY_AHEAD, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayFallBack(this)) {
        Card card = new Card(CardType.FALL_BACK, actions.size());
        actions.add(card);
        // Überprüfe ob wieder auf Hasenfeld gelandet:
        GameState clone = this.clone();
        try {
          card.perform(clone);
        } catch (InvalidMoveException e) {
          // Sollte nie passieren
          e.printStackTrace();
        }
        if (clone != null && clone.getCurrentPlayer().mustPlayCard()) {
          ArrayList<Move> moves = clone.checkForPlayableCards(actions);
          if (!moves.isEmpty()) {
            possibleMove.addAll(moves);
          }
        } else {
          possibleMove.add(new Move(actions));
        }
        actions.remove(new Card(CardType.FALL_BACK, actions.size()));
      }
    }
    return possibleMove;
  }

  @Override
  public String toString() {
    return "GameState:\n"
            + "turn=" + this.getTurn() + this.getCurrentPlayer()
            + this.red + this.blue
            + this.board
            + this.getLastMove();

  }

}
