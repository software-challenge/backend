package sc.plugin2014.converters;

import java.util.Map;
import java.util.Map.Entry;
import sc.plugin2014.entities.Field;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.moves.DebugHint;
import sc.plugin2014.moves.LayMove;
import sc.plugin2014.util.XStreamConfiguration;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LayMoveConverter implements Converter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
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
            XStreamConfiguration.getXStream().alias("qw:laymove",
                    value.getClass());
        }

        Map<Stone, Field> stoneToFieldMapping = move.getStoneToFieldMapping();

        for (Entry<Stone, Field> mapping : stoneToFieldMapping.entrySet()) {
            writer.startNode("stoneToField");
            writer.startNode("stone");
            context.convertAnother(mapping.getKey());
            writer.endNode();
            writer.startNode("field");
            context.convertAnother(mapping.getValue());
            writer.endNode();
            writer.endNode();
        }

        for (DebugHint hint : move.getHints()) {
            writer.startNode("hint");
            writer.addAttribute("content", hint.getContent());
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        LayMove move = new LayMove();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("stoneToField")) {
                reader.moveDown();
                Stone stone = (Stone) context.convertAnother(move, Stone.class);
                reader.moveUp();
                reader.moveDown();
                Field field = (Field) context.convertAnother(move, Field.class);
                reader.moveUp();
                move.getStoneToFieldMapping().put(stone, field);
            }
            else if (nodeName.equals("hint")) {
                move.addHint(reader.getAttribute("content"));
            }
            reader.moveUp();
        }

        return move;

    }
}
