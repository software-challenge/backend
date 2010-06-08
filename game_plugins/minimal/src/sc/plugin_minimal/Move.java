package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author sca
 * 
 */
@XStreamAlias(value = "minimal:move")
public final class Move implements Cloneable
{
	@Override
	protected Move clone() throws CloneNotSupportedException
	{
		return (Move) super.clone();
	}
}
