package sc.plugin2014.util;

import sc.plugin2014.BuildMove;
import sc.plugin2014.DebugHint;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@SuppressWarnings("unchecked")
public class BuildMoveConverter implements Converter {

    @Override
    public boolean canConvert(Class clazz) {
        try {
            return BuildMove.class.isAssignableFrom(clazz);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        BuildMove move = (BuildMove) value;
        if (!value.getClass().equals(BuildMove.class)) {
            /* adding standard xml-tag for derived moves */
            Configuration.getXStream().alias("manhattan:build",
                    value.getClass());
        }

        writer.addAttribute("city", Integer.toString(move.city));
        writer.addAttribute("slot", Integer.toString(move.slot));
        writer.addAttribute("size", Integer.toString(move.size));

        for (DebugHint hint : move.getHints()) {
            writer.startNode("hint");
            writer.addAttribute("content", hint.content);
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        int city = Integer.parseInt(reader.getAttribute("city"));
        int slot = Integer.parseInt(reader.getAttribute("slot"));
        int size = Integer.parseInt(reader.getAttribute("size"));
        BuildMove move = new BuildMove(city, slot, size);

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
