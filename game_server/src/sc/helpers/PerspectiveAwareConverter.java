package sc.helpers;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class PerspectiveAwareConverter extends ReflectionConverter
{
	private static final String	PERSPECTIVE_KEY		= "spactator";
	private static final String	CURRENT_OBJECT_KEY	= "current-object";

	public PerspectiveAwareConverter(Mapper mapper,
			ReflectionProvider reflectionProvider)
	{
		super(mapper, reflectionProvider);
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		if (object instanceof IPerspectiveProvider)
		{
			context.put(PERSPECTIVE_KEY, ((IPerspectiveProvider) object)
					.getPerspective());
		}

		Object old = context.get(CURRENT_OBJECT_KEY);
		context.put(CURRENT_OBJECT_KEY, object);
		super.marshal(object, writer, context);
		context.put(CURRENT_OBJECT_KEY, old);

		if (object instanceof IPerspectiveProvider)
		{
			context.put(PERSPECTIVE_KEY, null);
		}
	}

	@Override
	protected void marshallField(MarshallingContext context, Object newObj,
			Field field)
	{
		Object spectator = context.get(PERSPECTIVE_KEY);
		IPerspectiveAware aware = (IPerspectiveAware) context
				.get(CURRENT_OBJECT_KEY);

		if (spectator == null || aware.isVisibleFor(spectator, field.getName()))
		{
			super.marshallField(context, newObj, field);
		}
	}

	@Override
	public boolean canConvert(Class type)
	{
		return IPerspectiveProvider.class.isAssignableFrom(type)
				|| IPerspectiveAware.class.isAssignableFrom(type);
	}

}
