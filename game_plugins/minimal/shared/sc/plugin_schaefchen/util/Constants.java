package sc.plugin_schaefchen.util;

import sc.plugin_schaefchen.GamePlugin;

public class Constants {
	
	public final static int SCORE_PER_SHEEP = 8;
	public final static int SCORE_PER_SAVE_SHEEP = 16;
	public final static int SCORE_PER_FLOWER = 2;
	public final static int SCORE_PER_SAVE_FLOWER=4;
	
	public final static int TURN_LIMIT = GamePlugin.MAX_TURN_COUNT;
	
	public final static boolean GOLD_LIMIT_RULE = false;
	public final static boolean PRE_GOLDEN_RULE = true;
	
	public final static int SHEEPS_AT_HOME = 3;

	public final static int	DIE_COUNT = 3;
	public final static int	DIE_SIZE = 6;

	public final static int TOTAL_FLOWERS = 48;
	public final static int MIN_FLOWERS = -1;
	public final static int MAX_FLOWERS = 2;
	
	public final static int ARC_LENGTH= 9;
	public final static int ARC_SAVE_DIST = 5;
	public final static int ARC_SAVE_DIST2 = ARC_LENGTH - ARC_SAVE_DIST -1;
	public final static boolean USE_ARC_SAVE = true;
	
	public final static int STRAIGHT_LENGTH = 5;
	public final static int STRAIGHT_SAVE_DIST = STRAIGHT_LENGTH - 3;
	public final static boolean USE_STRAIGHT_SAVE = true;
	
	
	


}