package sc.plugin2014.util;

import java.util.Arrays;
import java.util.List;
import sc.plugin2014.*;
import sc.plugin2014.entities.*;
import sc.plugin2014.moves.*;
import sc.protocol.LobbyProtocol;
import com.thoughtworks.xstream.XStream;

public class XStreamConfiguration {

    private static XStream xStream;

    static {
        xStream = new XStream();
        xStream.setMode(XStream.NO_REFERENCES);
        xStream.setClassLoader(XStreamConfiguration.class.getClassLoader());
        LobbyProtocol.registerMessages(xStream);
        LobbyProtocol.registerAdditionalMessages(xStream,
                getClassesToRegister());
    }

    public static XStream getXStream() {
        return xStream;
    }

    public static List<Class<?>> getClassesToRegister() {
        return Arrays.asList(new Class<?>[] { LayMove.class, Game.class,
                GameState.class, Move.class, ExchangeMove.class, Player.class,
                WelcomeMessage.class, PlayerColor.class, Board.class,
                Field.class, Stone.class, StoneColor.class, StoneShape.class });
    }
}
