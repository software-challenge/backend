package sc.server.plugins;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.framework.plugins.IPerspectiveAware;

public class TestGameState implements IPerspectiveAware
{
	@XStreamOmitField
	private TestGame	controller;

	public Integer		round	= 0;
	public int			secret0	= 0;
	public int			secret1	= 0;

	public void setController(TestGame controller)
	{
		this.controller = controller;
	}

	@Override
	public boolean isVisibleFor(Object viewer, String field)
	{
		if (field.equals("secret0"))
		{
			return viewer == controller.getPlayers().get(0);
		}
		else if (field.equals("secret1"))
		{
			return viewer == controller.getPlayers().get(1);
		}

		return true;
	}
}
