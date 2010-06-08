package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author rra
 * @since Jul 4, 2009
 * 
 */
@XStreamAlias(value = "hui:move")
public final class Move implements Cloneable
{
	@Override
	protected Move clone() throws CloneNotSupportedException
	{
		return (Move) super.clone();
	}
}
