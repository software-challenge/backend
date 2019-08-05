package sc.plugin2020.util;

import com.thoughtworks.xstream.XStream;
import sc.framework.plugins.Player;
import sc.plugin2020.*;
import sc.protocol.LobbyProtocol;
import sc.shared.WelcomeMessage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class Configuration {
  private static XStream xStream;

  static {
    xStream = new XStream();
    xStream.setMode(XStream.NO_REFERENCES);
    xStream.setClassLoader(Configuration.class.getClassLoader());
    LobbyProtocol.registerMessages(xStream);
    LobbyProtocol.registerAdditionalMessages(xStream,
            getClassesToRegister());
  }

  public static XStream getXStream() {
    return xStream;
  }

  public static List<Class<?>> getClassesToRegister() {
    return Arrays.asList(Game.class, Board.class,
            GameState.class, Move.class,
            Direction.class, Field.class,
            WelcomeMessage.class, Condition.class);
  }
}
