package sc.shared;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScoreDefinition implements Iterable<ScoreFragment>
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

	public ScoreFragment get(int i)
	{
		return this.fragments.get(i);
	}

	@Override
	public Iterator<ScoreFragment> iterator() {
		return fragments.iterator();
	}
}
