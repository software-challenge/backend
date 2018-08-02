package sc.plugin2010.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin2010.Action;
import sc.plugin2010.Board;
import sc.plugin2010.FieldTyp;
import sc.plugin2010.FigureColor;
import sc.plugin2010.Game;
import sc.plugin2010.GameState;
import sc.plugin2010.Move;
import sc.plugin2010.MoveTyp;
import sc.plugin2010.Player;
import sc.plugin2010.Position;
import sc.plugin2010.WelcomeMessage;
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
				WelcomeMessage.class, Action.class, FieldTyp.class,
				FigureColor.class, MoveTyp.class, Position.class });
	}
}
