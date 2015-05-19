package sc.plugin2016.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;

import sc.plugin2015.Field;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.Penguin;
import sc.plugin2016.Board;
import sc.plugin2016.Game;
import sc.plugin2016.GameState;
import sc.plugin2016.Move;
import sc.plugin2016.NullMove;
import sc.plugin2016.RunMove;
import sc.plugin2016.SetMove;
import sc.plugin2016.WelcomeMessage;
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
		return Arrays.asList(new Class<?>[] { SetMove.class, Game.class,
				GameState.class, Constants.class, Move.class, RunMove.class,
				Player.class, WelcomeMessage.class, PlayerColor.class,
				Penguin.class, Field.class, Board.class, Condition.class,
				NullMove.class });
	}
}
