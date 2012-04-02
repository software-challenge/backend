package sc.plugin2014.util;

import java.util.LinkedList;
import java.util.List;
import sc.plugin2014.DebugHint;
import sc.plugin2014.SelectMove;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("unchecked")
public class SelectMoveConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        try {
            return SelectMove.class.isAssignableFrom(clazz);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        if (!value.getClass().equals(SelectMove.class)) {
            /* adding standard xml-tag for derived moves */
            Configuration.getXStream().alias("manhattan:select",
                    value.getClass());
        }
        SelectMove move = (SelectMove) value;
        int[] selections = move.getSelections();

        for (int i = 0; i < Constants.MAX_SEGMENT_SIZE; i++) {
            writer.startNode("select");
            writer.addAttribute("size", Integer.toString(i + 1));
            writer.addAttribute("amount", Integer.toString(selections[i]));
            writer.endNode();
        }

        for (DebugHint hint : move.getHints()) {
            writer.startNode("hint");
            writer.addAttribute("content", hint.content);
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        int[] selections = new int[Constants.MAX_SEGMENT_SIZE];
        List<String> hints = new LinkedList<String>();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("select")) {
                int size = Integer.parseInt(reader.getAttribute("size"));
                int amount = Integer.parseInt(reader.getAttribute("amount"));
                if ((0 < size) && (size <= Constants.MAX_SEGMENT_SIZE)) {
                    selections[size - 1] = amount;
                }
            }
            else if (nodeName.equals("hint")) {
                hints.add(reader.getAttribute("content"));
            }
            reader.moveUp();
        }

        SelectMove move = new SelectMove(selections);
        for (String hint : hints) {
            move.addHint(hint);
        }

        return move;

    }

}
