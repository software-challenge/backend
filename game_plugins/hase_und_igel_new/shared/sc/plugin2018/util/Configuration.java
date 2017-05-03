package sc.plugin2018.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin2018.Action;
import sc.plugin2018.Board;
import sc.plugin2018.FieldType;
import sc.plugin2018.PlayerColor;
import sc.plugin2018.Game;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.MoveTyp;
import sc.plugin2018.Player;
import sc.plugin2018.Position;
import sc.plugin2018.WelcomeMessage;
import sc.protocol.LobbyProtocol;

import com.thoughtworks.xstream.XStream;

public class Configuration
{
	private static XStream	xStream;

	static
	{
		xStream = new XStream();
		xStream.setMode(XStream.NO_REFERENCES);
		xStream.setClassLoader(Configuration.class.getClassLoader());
		LobbyProtocol.registerMessages(xStream);
		LobbyProtocol.registerAdditionalMessages(xStream,
				getClassesToRegister());
	}

	public static XStream getXStream()
	{
		return xStream;
	}

	public static List<Class<?>> getClassesToRegister()
	{
		return Arrays.asList(new Class<?>[] { Game.class, Board.class,
				GameState.class, Move.class, Player.class,
				WelcomeMessage.class, Action.class, FieldType.class,
				PlayerColor.class, MoveTyp.class, Position.class });
	}
}
