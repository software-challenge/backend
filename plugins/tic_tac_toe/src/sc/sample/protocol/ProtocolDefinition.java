package sc.sample.protocol;

import java.util.Arrays;
import java.util.Collection;

import sc.sample.shared.Move;

public abstract class ProtocolDefinition
{
	@SuppressWarnings("unchecked")
	public static Collection<Class<? extends Object>> getProtocolClasses()
	{
		return Arrays.asList(Move.class, Object.class);
	}
}
