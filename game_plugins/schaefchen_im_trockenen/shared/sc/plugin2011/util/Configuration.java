package sc.plugin2011.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin2011.Game;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.plugin2011.WelcomeMessage;
import sc.protocol.LobbyProtocol;

import ch.qos.logback.core.pattern.parser.Node;

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
		return Arrays.asList(new Class<?>[] { Game.class, GameState.class,
				Constants.class, Move.class, Player.class, Node.class,
				Sheep.class, WelcomeMessage.class, PlayerColor.class });
	}
}
