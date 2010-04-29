package finals;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class FinalsConfiguration {
	public FinalsConfiguration() {
		super();
	}

	public FinalsConfiguration(String serverStartupCommand, String spielname,
			Date date, int speed) {
		super();
		this.serverStartupCommand = serverStartupCommand;
		this.spielname = spielname;
		this.date = date;
		this.speed = speed;
	}

	private String serverStartupCommand;
	private String spielname;
	private Date date;
	private int speed;

	public String getServerStartupCommand() {
		return serverStartupCommand;
	}

	public void setServerStartupCommand(String serverStartupCommand) {
		this.serverStartupCommand = serverStartupCommand;
	}

	public String getSpielname() {
		return spielname;
	}

	public void setSpielname(String spielname) {
		this.spielname = spielname;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
