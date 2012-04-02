package sc.plugin2014.util;

import java.util.Arrays;
import java.util.List;
import sc.plugin2014.*;
import sc.protocol.LobbyProtocol;
import com.thoughtworks.xstream.XStream;

/**
 * Configuration
 * 
 * @author sca
 * 
 */
public class Configuration {
    /*
     * The XStream which is used to translate Objects to XML and vice versa
     */

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
        return Arrays.asList(new Class<?>[] { BuildMove.class, Game.class,
                GameState.class, Constants.class, Move.class, SelectMove.class,
                Player.class, WelcomeMessage.class, PlayerColor.class });
    }
}
