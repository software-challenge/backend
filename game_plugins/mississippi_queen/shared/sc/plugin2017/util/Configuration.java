package sc.plugin2017.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;

import sc.plugin2017.Acceleration;
import sc.plugin2017.Action;
import sc.plugin2017.Board;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.Game;
import sc.plugin2017.GameState;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Push;
import sc.plugin2017.Advance;
import sc.plugin2017.Tile;
import sc.plugin2017.Turn;
import sc.plugin2017.WelcomeMessage;
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
	  // print board
	  /*Move move = new Move();
    move.actions.add(new Acceleration(1,0));
    move.actions.add(new Step(2,1));
    move.actions.add(new Push(1,2));
    move.actions.add(new Turn(1,3));
	  xStream.alias("move", move.getClass());
	  System.out.println(xStream.toXML(move));*/
	  
		return Arrays.asList(new Class<?>[] { Game.class,
				GameState.class, Constants.class, Move.class,
				Player.class, WelcomeMessage.class, PlayerColor.class,
				Condition.class, Board.class, Field.class, FieldType.class,
				Action.class, Turn.class, Advance.class, Push.class, Acceleration.class,
				Tile.class
				});
	}
}
