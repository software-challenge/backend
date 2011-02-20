package sc.plugin2012.util;

import sc.plugin2012.SelectMove;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("unchecked")
public class SelectMoveConverter implements Converter {

	@Override
	public boolean canConvert(Class clazz) {
		return clazz.equals(SelectMove.class);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		SelectMove move = (SelectMove) value;
		int[] selections = move.getSelections();

		for (int i = 0; i < Constants.MAX_SEGMENT_SIZE; i++) {
			writer.startNode("select");
			writer.addAttribute("size", Integer.toString(i + 1));
			writer.addAttribute("amount", Integer.toString(selections[i]));
			writer.endNode();
		}

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		int[] selections = new int[Constants.MAX_SEGMENT_SIZE];

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if (reader.getNodeName().equals("select")) {
				int size = Integer.parseInt(reader.getAttribute("size"));
				int amount = Integer.parseInt(reader.getAttribute("amount"));
				if (0 < size && size <= Constants.MAX_SEGMENT_SIZE) {
					selections[size - 1] = amount;
				}
			}
			reader.moveUp();
		}

		return new SelectMove(selections);

	}

}
