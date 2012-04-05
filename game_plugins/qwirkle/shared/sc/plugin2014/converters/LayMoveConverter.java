package sc.plugin2014.converters;

import sc.plugin2014.moves.DebugHint;
import sc.plugin2014.moves.LayMove;
import sc.plugin2014.util.XStreamConfiguration;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("unchecked")
public class LayMoveConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        try {
            return LayMove.class.isAssignableFrom(clazz);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        LayMove move = (LayMove) value;
        if (!value.getClass().equals(LayMove.class)) {
            /* adding standard xml-tag for derived moves */
            XStreamConfiguration.getXStream().alias("qw:laymove", value.getClass());
        }

        writer.addAttribute("city", Integer.toString(move.city));
        writer.addAttribute("slot", Integer.toString(move.slot));
        writer.addAttribute("size", Integer.toString(move.size));

        for (DebugHint hint : move.getHints()) {
            writer.startNode("hint");
            writer.addAttribute("content", hint.getContent());
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        int city = Integer.parseInt(reader.getAttribute("city"));
        int slot = Integer.parseInt(reader.getAttribute("slot"));
        int size = Integer.parseInt(reader.getAttribute("size"));
        LayMove move = new LayMove(city, slot, size);

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("hint")) {
                move.addHint(reader.getAttribute("content"));
            }
            reader.moveUp();
        }

        return move;

    }

}
