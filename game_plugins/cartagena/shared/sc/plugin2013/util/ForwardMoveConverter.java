package sc.plugin2013.util;

import sc.plugin2013.ForwardMove;
import sc.plugin2013.SymbolType;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ForwardMoveConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		// TODO Auto-generated method stub
		return arg0.equals(ForwardMove.class);
	}

	@Override
	public void marshal(Object forwardMove, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		ForwardMove fW = (ForwardMove) forwardMove;
		writer.addAttribute("fieldIndex", String.valueOf(fW.fieldIndex));
		writer.addAttribute("symbol", fW.symbol.toString());

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		// TODO Auto-generated method stub
		ForwardMove fw = new ForwardMove(0, SymbolType.BOTTLE);
		try {
			Field field = ForwardMove.class.getDeclaredField("fieldIndex");
			field.setAccessible(true);
			field.set(fw, Integer.parseInt(reader.getAttribute("fieldIndex")));
			field.setAccessible(false);

			field = ForwardMove.class.getDeclaredField("symbol");
			String symbol = reader.getAttribute("symbol").toString();
//			System.out.println(symbol);

			if (symbol.equals("BOTTLE")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.BOTTLE);
				field.setAccessible(false);
			} else if (symbol.equals("DAGGER")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.DAGGER);
				field.setAccessible(false);
			} else if (symbol.equals("SKULL")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.SKULL);
				field.setAccessible(false);
			} else if (symbol.equals("HAT")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.HAT);
				field.setAccessible(false);
			} else if (symbol.equals("KEY")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.KEY);
				field.setAccessible(false);
			} else if (symbol.equals("PISTOL")) {
				field.setAccessible(true);
				field.set(fw, SymbolType.PISTOL);
				field.setAccessible(false);
			} else {
				field = ForwardMove.class.getDeclaredField("wrongSymbolString");
				field.setAccessible(true);
				field.set(fw, true);
				field.setAccessible(false);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return fw;
	}

}
