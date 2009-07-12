package sc.api.plugins;

import java.util.LinkedList;
import java.util.List;

public class ScoreDefinition
{
	private List<ScoreFragment>	fragments	= new LinkedList<ScoreFragment>();

	public void add(String name)
	{
		this.fragments.add(new ScoreFragment(name));
	}

	public int size()
	{
		return this.fragments.size();
	}

	public boolean isValid()
	{
		return size() > 0;
	}
}
