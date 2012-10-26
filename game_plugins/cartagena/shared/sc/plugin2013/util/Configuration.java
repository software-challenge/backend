package sc.plugin2013.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;

import sc.plugin2013.BackwardMove;
import sc.plugin2013.Board;
import sc.plugin2013.Card;
import sc.plugin2013.DebugHint;
import sc.plugin2013.ForwardMove;
import sc.plugin2013.Game;
import sc.plugin2013.GameState;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.Pirate;
import sc.plugin2013.Player;
import sc.plugin2013.WelcomeMessage;
import sc.protocol.LobbyProtocol;
import sc.plugin2013.Field;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class Configuration {
	/*
	 * The XStream which is used to translate Objects to XML and vice versa
	 */

	private static XStream xStream;

	static {
//		QNameMap qmap = new QNameMap();
//		qmap.setDefaultNamespace("urn:cg:cartagena");
////		qmap.setDefaultPrefix("cartagena");
//		StaxDriver staxDriver = new StaxDriver(qmap);
		xStream = new XStream();
		xStream.setMode(XStream.NO_REFERENCES);
		xStream.setClassLoader(Configuration.class.getClassLoader());
		//xStream.alias("pirates", List.class, LinkedList.class);
		LobbyProtocol.registerMessages(xStream);
		LobbyProtocol.registerAdditionalMessages(xStream,
				getClassesToRegister());
	}

	public static XStream getXStream() {
		return xStream;
	}

	public static List<Class<?>> getClassesToRegister() {
		return Arrays.asList(new Class<?>[] { Player.class, Move.class,
				Game.class, Constants.class, Move.class, ForwardMove.class,
				BackwardMove.class, Field.class, Pirate.class, Board.class,
				MoveContainer.class, GameState.class, WelcomeMessage.class,
				Card.class, Condition.class, DebugHint.class,
				MoveContainer.class });
	}
}
