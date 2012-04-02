package sc.plugin2014.util;

import java.util.Arrays;
import java.util.List;
import sc.plugin2014.*;
import sc.protocol.LobbyProtocol;
import com.thoughtworks.xstream.XStream;

public class Configuration {

    private static XStream xStream;

    static {
        xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.setClassLoader(Configuration.class.getClassLoader());
        LobbyProtocol.registerMessages(xStream);
        LobbyProtocol.registerAdditionalMessages(xStream,
                getClassesToRegister());
    }

    public static XStream getXStream() {
        return xStream;
    }

    public static List<Class<?>> getClassesToRegister() {
        return Arrays.asList(new Class<?>[] { LayMove.class, Game.class,
                GameState.class, Constants.class, Move.class,
                ExchangeMove.class, Player.class, WelcomeMessage.class,
                PlayerColor.class });
    }
}
