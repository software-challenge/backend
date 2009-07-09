package sc.plugin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import sc.framework.plugins.IPerspectiveAware;
import sc.framework.plugins.IPerspectiveProvider;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class PerspectiveAwareConverter extends AbstractReflectionConverter
{
	private static final String	PERSPECTIVE_KEY		= "spectator";
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

	// original source code taken from
	// http://svn.xstream.codehaus.org/browse/~raw,r=1681/xstream/trunk/xstream/src/java/com/thoughtworks/xstream/converters/reflection/AbstractReflectionConverter.java
	@Override
	protected void doMarshal(final Object source,
			final HierarchicalStreamWriter writer,
			final MarshallingContext context)
	{
		final Set seenFields = new HashSet();
		final Map defaultFieldDefinition = new HashMap();

		// Attributes might be preferred to child elements ...
		reflectionProvider.visitSerializableFields(source,
				new ReflectionProvider.Visitor() {
					public void visit(String fieldName, Class type,
							Class definedIn, Object value)
					{
						if (!mapper.shouldSerializeMember(definedIn, fieldName))
						{
							return;
						}
						if(!shouldMarshallPerspectiveField(context, fieldName))
						{
							return;
						}
						if (!defaultFieldDefinition.containsKey(fieldName))
						{
							Class lookupType = source.getClass();
							// See XSTR-457 and OmitFieldsTest
							// if (definedIn != source.getClass() &&
							// !mapper.shouldSerializeMember(lookupType,
							// fieldName)) {
							// lookupType = definedIn;
							// }
							defaultFieldDefinition.put(fieldName,
									reflectionProvider.getField(lookupType,
											fieldName));
						}

						SingleValueConverter converter = mapper
								.getConverterFromItemType(fieldName, type,
										definedIn);
						if (converter != null)
						{
							if (value != null)
							{
								if (seenFields.contains(fieldName))
								{
									throw new ConversionException(
											"Cannot write field with name '"
													+ fieldName
													+ "' twice as attribute for object of type "
													+ source.getClass()
															.getName());
								}
								final String str = converter.toString(value);
								if (str != null)
								{
									writer.addAttribute(mapper
											.aliasForAttribute(mapper
													.serializedMember(
															definedIn,
															fieldName)), str);
								}
							}
							// TODO: name is not enough, need also "definedIn"!
							seenFields.add(fieldName);
						}
					}
				});

		// Child elements not covered already processed as attributes ...
		reflectionProvider.visitSerializableFields(source,
				new ReflectionProvider.Visitor() {
					public void visit(String fieldName, Class fieldType,
							Class definedIn, Object newObj)
					{
						if (!mapper.shouldSerializeMember(definedIn, fieldName))
						{
							return;
						}
						if(!shouldMarshallPerspectiveField(context, fieldName))
						{
							return;
						}
						if (!seenFields.contains(fieldName) && newObj != null)
						{
							Mapper.ImplicitCollectionMapping mapping = mapper
									.getImplicitCollectionDefForFieldName(
											source.getClass(), fieldName);
							if (mapping != null)
							{
								if (mapping.getItemFieldName() != null)
								{
									Collection list = (Collection) newObj;
									for (Iterator iter = list.iterator(); iter
											.hasNext();)
									{
										Object obj = iter.next();
										writeField(
												fieldName,
												obj == null ? mapper
														.serializedClass(null)
														: mapping
																.getItemFieldName(),
												mapping.getItemType(),
												definedIn, obj);
									}
								}
								else
								{
									context.convertAnother(newObj);
								}
							}
							else
							{
								writeField(fieldName, null, fieldType,
										definedIn, newObj);
							}
						}
					}

					private void writeField(String fieldName, String aliasName,
							Class fieldType, Class definedIn, Object newObj)
					{
						ExtendedHierarchicalStreamWriterHelper.startNode(
								writer, aliasName != null ? aliasName : mapper
										.serializedMember(source.getClass(),
												fieldName), fieldType);

						if (newObj != null)
						{
							Class actualType = newObj.getClass();
							Class defaultType = mapper
									.defaultImplementationOf(fieldType);
							if (!actualType.equals(defaultType))
							{
								String serializedClassName = mapper
										.serializedClass(actualType);
								if (!serializedClassName.equals(mapper
										.serializedClass(defaultType)))
								{
									String attributeName = mapper
											.aliasForSystemAttribute("class");
									if (attributeName != null)
									{
										writer.addAttribute(attributeName,
												serializedClassName);
									}
								}
							}

							final Field defaultField = (Field) defaultFieldDefinition
									.get(fieldName);
							if (defaultField.getDeclaringClass() != definedIn)
							{
								String attributeName = mapper
										.aliasForSystemAttribute("defined-in");
								if (attributeName != null)
								{
									writer.addAttribute(attributeName, mapper
											.serializedClass(definedIn));
								}
							}

							Field field = reflectionProvider.getField(
									definedIn, fieldName);
							marshallField(context, newObj, field);
						}
						writer.endNode();
					}

				});
	}

	protected boolean shouldMarshallPerspectiveField(MarshallingContext context,
			String fieldName)
	{
		Object currentObject = context.get(CURRENT_OBJECT_KEY);

		if (currentObject instanceof IPerspectiveAware)
		{
			Object spectator = context.get(PERSPECTIVE_KEY);
			if (spectator == null)
				return true;

			IPerspectiveAware aware = (IPerspectiveAware) currentObject;
			return aware.isVisibleFor(spectator, fieldName);
		}

		return true;
	}

	@Override
	public boolean canConvert(Class type)
	{
		return IPerspectiveProvider.class.isAssignableFrom(type)
				|| IPerspectiveAware.class.isAssignableFrom(type);
	}

}
