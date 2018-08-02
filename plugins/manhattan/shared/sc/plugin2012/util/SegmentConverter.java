package sc.plugin2012.util;

import java.lang.reflect.Field;

import sc.plugin2012.Segment;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("unchecked")
public class SegmentConverter implements Converter {

	@Override
	public boolean canConvert(Class clazz) {
		return clazz.equals(Segment.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		Segment segment = (Segment) value;

		writer.addAttribute("size", Integer.toString(segment.size));
		writer.addAttribute("usable", Integer.toString(segment.getUsable()));
		writer.addAttribute("retained", Integer.toString(segment.getRetained()));

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		int size = Integer.parseInt(reader.getAttribute("size"));
		int usable = Integer.parseInt(reader.getAttribute("usable"));
		int retained = Integer.parseInt(reader.getAttribute("retained"));
		int used = Constants.SEGMENT_AMOUNTS[size - 1] - retained - usable;

		Segment segment = new Segment(size, retained);

		try {
			Field field = Segment.class.getDeclaredField("used");
			field.setAccessible(true);
			field.set(segment, used);
			field.setAccessible(false);
			
			 field = Segment.class.getDeclaredField("usable");
			field.setAccessible(true);
			field.set(segment, usable);
			field.setAccessible(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return segment;

	}

}
