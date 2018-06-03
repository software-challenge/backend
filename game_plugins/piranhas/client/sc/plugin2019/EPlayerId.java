package sc.plugin2019;

/**
 * To identify clients, which role they have
 *
 * @author ffi
 *
 */
public enum EPlayerId {
  // NOTE: Order is important, see {@link GamePreparation#nextHumanPlayerId()}.
  // Player one needs to be declared before player two.
	NONE, OBSERVER, PLAYER_ONE, PLAYER_TWO
}
