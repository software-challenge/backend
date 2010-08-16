package sc.plugin_schaefchen;

/**
 * Tools to be used in the server or client plugin
 * @author sca
 *
 */
public class GameUtil
{
	
	private GameUtil()
	{
		throw new IllegalStateException("Can't be instantiated.");
	}

	public static String displayMoveAction(Move mov)
	{
		if (mov != null)
		{
			return "macht gar nichts";
		}
		return "";
	}

}
