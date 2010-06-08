package sc.plugin_minimal;

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
