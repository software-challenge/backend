package sc.plugin2013.util;


import java.util.Arrays;
import java.util.List;

import sc.plugin2013.Player;
import sc.protocol.LobbyProtocol;
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
	
	public static List<Class<?>> getClassesToRegister() {
		return Arrays.asList(new Class<?>[] { Player.class }); //TODO fill in classes to Register
	}
}
