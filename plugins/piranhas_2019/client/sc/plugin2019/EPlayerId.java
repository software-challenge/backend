package sc.plugin2019;

/** Identifiziert die Rolle des Clients */
public enum EPlayerId {
  // NOTE: Order is important, see {@link GamePreparation#nextHumanPlayerId()}.
  // Player one needs to be declared before player two.
	NONE, OBSERVER, PLAYER_ONE, PLAYER_TWO
}
