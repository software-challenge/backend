package shared.sc.plugin2015.util;

import java.util.Arrays;
import java.util.List;

import shared.sc.plugin2015.BuildMove;
import server.sc.plugin2015.Game;
import shared.sc.plugin2015.GameState;
import shared.sc.plugin2015.Move;
import shared.sc.plugin2015.Player;
import shared.sc.plugin2015.PlayerColor;
import shared.sc.plugin2015.SelectMove;
import shared.sc.plugin2015.WelcomeMessage;
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
		return Arrays.asList(new Class<?>[] { BuildMove.class, Game.class, GameState.class,
				Constants.class, Move.class, SelectMove.class, Player.class,
				WelcomeMessage.class, PlayerColor.class });
	}
}
