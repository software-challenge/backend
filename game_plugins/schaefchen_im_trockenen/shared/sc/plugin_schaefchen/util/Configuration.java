package sc.plugin_schaefchen.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.Game;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.plugin_schaefchen.Sheep;
import sc.plugin_schaefchen.WelcomeMessage;
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
