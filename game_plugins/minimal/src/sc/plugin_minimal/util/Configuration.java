package sc.plugin_minimal.util;

import java.util.Arrays;
import java.util.List;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.FigureColor;
import sc.plugin_minimal.Game;
import sc.plugin_minimal.GameState;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.WelcomeMessage;
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
				WelcomeMessage.class, FigureColor.class });
	}
}
