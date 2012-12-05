package sc.plugin2014.converters;

import java.util.*;
import sc.plugin2014.entities.Stone;
import sc.plugin2014.moves.DebugHint;
import sc.plugin2014.moves.ExchangeMove;
import sc.plugin2014.util.XStreamConfiguration;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ExchangeMoveConverter implements Converter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        try {
            return ExchangeMove.class.isAssignableFrom(clazz);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        if (!value.getClass().equals(ExchangeMove.class)) {
            /* adding standard xml-tag for derived moves */
            XStreamConfiguration.getXStream().alias("qw:exchangemove",
                    value.getClass());
        }
        ExchangeMove move = (ExchangeMove) value;
        List<Stone> selections = move.getStonesToExchange();

        for (int i = 0; i < selections.size(); i++) {
            writer.startNode("select");
            context.convertAnother(selections.get(i));
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

        List<Stone> selections = new ArrayList<Stone>();
        List<String> hints = new LinkedList<String>();

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("select")) {
                selections.add((Stone) context.convertAnother(selections,
                        Stone.class));
            }
            else if (nodeName.equals("hint")) {
                hints.add(reader.getAttribute("content"));
            }
            reader.moveUp();
        }

        ExchangeMove move = new ExchangeMove(selections);
        for (String hint : hints) {
            move.addHint(hint);
        }

        return move;

    }
}
