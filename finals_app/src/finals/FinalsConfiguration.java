package finals;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.JOptionPane;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class FinalsConfiguration {
	public FinalsConfiguration() {
		super();
	}

	public FinalsConfiguration(String gameName,
			Date date, int speed) {
		super();
		this.gameName = gameName;
		this.date = date;
		this.speed = speed;
		this.openReplay = true;
	}

	private String gameName;
	private Date date;
	private int speed;
	private String version = "v. 1.0";
	private boolean openReplay;
	private File config = new File("setting.conf");
    String introReplayPath;
	private String serverJarPath = "server/softwarechallenge-gui.jar";
	private String serverPluginPath = "server/plugins/";
	private boolean isSkipActive = true;
	public String[] developers = {"André Domnick"};
	public String TEXT_STYLE = "Arial";
	String titleText = "Software Challenge Finale: ";
	public Color BACKGROUND = new Color(255,255,255);


	public String getGameName() {
		return gameName;
	}

	public void setSpielname(String gameName) {
		this.gameName = gameName;
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

	public boolean isOpenReplay() {
		return openReplay;
	}

	public void setOpenReplay(boolean openReplay) {
		this.openReplay = openReplay;
	}
	
	public boolean save(Component parent){
		try {
			System.out.println("Saving setting to configuration: "+config.getAbsolutePath());
			FileWriter writer = new FileWriter(config);
			writer.write("Finals Tool Configuration File \n");
			writer.write("speed = "+speed+"\n");
			writer.write("intro path = "+introReplayPath+"\n");
			writer.write("skipActive = "+isSkipActive+"\n");
			writer.write("title text = "+titleText+"\n");
			writer.close(); 
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent,"Beim Speichern der Konfiguration trat ein Fehler auf!");
			return false;
		}
		
	}
	
	public boolean load(Component parent){
		try {
			if(config.exists()){
				BufferedReader reader = new BufferedReader(new FileReader(config));
				System.out.println("Reading configuration: "+config.getAbsolutePath());
				String ln = reader.readLine();
				while(ln!=null){
					System.out.println(ln);
					if(ln.contains("speed")){
						String sp = ln.split("=")[1].trim();
						int spe = Integer.parseInt(sp);
						if(spe <= 100){
							speed = spe;
						}else{
							System.out.println("The speed setting must be an integer between 0 and 100!");
						}
					}else if (ln.contains("intro path")) {
						String path = ln.split("=")[1].trim();
						File t = new File(path);
						if(t.exists()){
							introReplayPath = path;
						}else{
							System.out.println("The given path to the intro file is not valid!");
						}
					}else if (ln.contains("skipActive")){
						int res = Integer.parseInt(ln.split("=")[1].trim());
						if(res == 1) this.isSkipActive = true;
					}else if (ln.contains("title text")){
						titleText = ln.split("=")[1].trim();
					}
					ln = reader.readLine();
				}
			}	
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent,"Beim Laden der Konfiguration trat ein Fehler auf!");
			e.printStackTrace();
			return false;
		}
	}
	
	public void setServerJarPath(String path){
		File file = new File(path);
		if(file.exists() && file.canRead()){
			serverJarPath = path;
		}
	}
	
	public String getServerJarPath(){
		return serverJarPath;
	}

	public String getServerPluginPath() {
		return serverPluginPath;
	}

	public void setServerPluginPath(String serverPluginPath) {
		this.serverPluginPath = serverPluginPath;
	}

	public boolean isSkipActive() {
		return isSkipActive;
	}

	public void setSkipActive(boolean isSkipActive) {
		this.isSkipActive = isSkipActive;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
