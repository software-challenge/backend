package sc.plugin2019.util;

public class Constants {

	public static final int ROUND_LIMIT = 30;
  public static final String WINNING_MESSAGE = "Das Spiel ist beendet.\nEin Spieler hat seinen Schwarm vereint.";
  public static final String ROUND_LIMIT_MESSAGE = "Das Rundenlimit wurde erreicht.";
  // for gamestats array
  public static final int GAME_STATS_SWARM_SIZE = 0;
  public static final int GAME_STATS_RED_INDEX = 0;
  public static final int GAME_STATS_BLUE_INDEX = 1;
  public static final int WIN_SCORE = 2;
  public static final int LOSE_SCORE = 0;
  public static final int DRAW_SCORE = 1;

  public static final int BOARD_SIZE = 10;
  public static final int NUM_OBSTACLES = 2;
  public static final int MAX_FISH = (BOARD_SIZE -2)*2;
  public static final int OBSTACLES_START = 2; // refers to the lowest index at which a obstructed field might be placed
  public static final int OBSTACLES_END = 7; // refers to the highest index at which a obstructed field might be placed
}