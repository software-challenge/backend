package sc.framework.plugins;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IPlayer;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.protocol.MoveRequest;

public abstract class SimplePlayer implements IPlayer
{
	public static final Logger		logger			= LoggerFactory
			.getLogger(SimplePlayer.class);

	@XStreamOmitField
	private List<IPlayerListener>	listeners;

	@XStreamOmitField
	private boolean					canTimeout;

	@XStreamOmitField
	private boolean					shouldBePaused;

	@XStreamAsAttribute
	private String					displayName;

	@XStreamOmitField
	protected boolean				violated		= false;
	
	@XStreamOmitField
	protected boolean				left		    = false;

	@XStreamOmitField
	protected String				violationReason	= null;

	public SimplePlayer()
	{
		initListeners();
	}

	public String getViolationReason()
	{
		return violationReason;
	}

	public void setViolationReason(String violationReason)
	{
		this.violationReason = violationReason;
	}

	@Override
	public void addPlayerListener(IPlayerListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void removePlayerListener(IPlayerListener listener)
	{
		this.listeners.remove(listener);
	}

	public void notifyListeners(Object o)
	{
		for (IPlayerListener listener : this.listeners)
		{
			listener.onPlayerEvent(o);
		}
	}

	public void requestMove()
	{
		MoveRequest request = new MoveRequest();

		for (IPlayerListener listener : this.listeners)
		{
			listener.onPlayerEvent(request);
		}

		logger.debug("Move requested.");
	}

	@Override
	public final void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public final String getDisplayName()
	{
		return this.displayName;
	}

	@Override
	public final void setCanTimeout(boolean canTimeout)
	{
		this.canTimeout = canTimeout;
	}

	@Override
	public final void setShouldBePaused(boolean shouldBePaused)
	{
		this.shouldBePaused = shouldBePaused;
	}

	public boolean isCanTimeout()
	{
		return this.canTimeout;
	}

	public boolean isShouldBePaused()
	{
		return this.shouldBePaused;
	}

	@Override
	public void setViolated(boolean violated)
	{
		this.violated = violated;
	}

	@Override
	public boolean hasViolated()
	{
		return this.violated;
	}

	@Override
	public void setLeft(boolean left)
	{
		this.left = left;
	}

	@Override
	public boolean hasLeft()
	{
		return this.left;
	}
	
	/**
	 * Initializes listeners, when they don't already exist. Only used for
	 * playing on an imported state
	 */
	public void initListeners()
	{
		if (this.listeners == null)
		{
			this.listeners = new LinkedList<>();
		}
	}
}
