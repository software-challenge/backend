package sc.logic.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sc.shared.SharedConfiguration;

public class ConfigCreateGameDialog implements Serializable {

	private static final long serialVersionUID = -2640736500193574274L;

	private final List<Player> players;

	private boolean timeLimit;

	private int port;

	private String gameTypeName;
	
	public ConfigCreateGameDialog() {
		this.players = new ArrayList<Player>();
		this.timeLimit = false;
		this.port = SharedConfiguration.DEFAULT_PORT;
		this.gameTypeName = "";
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setTimeLimit(boolean timeLimit) {
		this.timeLimit = timeLimit;
	}

	public boolean isTimeLimit() {
		return timeLimit;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setGameType(String gameTypeName) {
		this.gameTypeName = gameTypeName;
	}

	public String getGameType() {
		return gameTypeName;
	}
}
