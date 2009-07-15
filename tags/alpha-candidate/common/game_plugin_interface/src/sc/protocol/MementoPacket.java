package sc.protocol;

import sc.framework.plugins.IPerspectiveProvider;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("memento")
public final class MementoPacket implements IPerspectiveProvider
{
	private Object	state;

	@XStreamOmitField
	private Object	perspective;

	public MementoPacket(Object state, Object perspective)
	{
		this.state = state;
		this.perspective = perspective;
	}

	public MementoPacket()
	{
		// TODO Auto-generated constructor stub
	}

	public Object getState()
	{
		return this.state;
	}

	@Override
	public Object getPerspective()
	{
		return this.perspective;
	}
}
