package sc.plugin2019;

/** Die Rolle eines Clients */
public enum PlayerType {
  // NOTE: Order is important, see {@link GamePreparation#nextHumanPlayerId()}.
  // Player one needs to be declared before player two.
  NONE, OBSERVER, PLAYER_ONE, PLAYER_TWO
}
