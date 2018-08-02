package sc.sample.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "sampleMove")
public class Move
{
	private int	x;
	private int	y;

	public Move(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
}
