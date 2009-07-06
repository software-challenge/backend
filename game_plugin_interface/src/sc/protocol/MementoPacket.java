package sc.protocol;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("memento")
public final class MementoPacket
{
	private Object	state;

	@XStreamOmitField
	private Object	perspective;

	public MementoPacket(Object state, Object perspective)
	{
		this.state = state;
	}

	public MementoPacket()
	{
		// TODO Auto-generated constructor stub
	}

	public Object getState()
	{
		return this.state;
	}

	public void setState(Object state)
	{
		this.state = state;
	}
}
