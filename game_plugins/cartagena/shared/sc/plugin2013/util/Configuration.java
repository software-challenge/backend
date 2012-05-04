package sc.plugin2013.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin2013.BackwardMove;
import sc.plugin2013.Board;
import sc.plugin2013.Card;
import sc.plugin2013.ForwardMove;
import sc.plugin2013.Game;
import sc.plugin2013.GameState;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.Pirate;
import sc.plugin2013.Player;
import sc.plugin2013.WelcomeMessage;
import sc.protocol.LobbyProtocol;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.thoughtworks.xstream.XStream;

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

	// public static List<Class<?>> getClassesToRegister() {
	// return Arrays.asList(new Class<?>[] { Player.class, Game.class,
	// GameState.class, WelcomeMessage.class, Pirate.class,
	// ForwardMove.class, BackwardMove.class, Move.class,
	// MoveContainer.class, Card.class, Board.class });
	// }
	public static List<Class<?>> getClassesToRegister() {
		return Arrays.asList(new Class<?>[] { Player.class, Move.class,
				ForwardMove.class, BackwardMove.class, Field.class,
				Pirate.class, Board.class });
	}
}
