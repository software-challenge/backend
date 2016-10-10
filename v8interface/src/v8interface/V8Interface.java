package v8interface;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


import sc.server.Lobby;
import sc.server.gaming.GameRoom;
import sc.shared.SlotDescriptor;
import sc.server.Configuration;

public class V8Interface implements Runnable{
  LinkedBlockingQueue<V8Turn> currentTurn;

  
  LinkedBlockingQueue<String> log;
  
  ArrayList<SlotDescriptor> slots;

  Lobby server;
  
  Integer port;
  
  public Lobby getServer() throws Exception{
	  if(server != null){
		  server.close();
		  server = null;
		  log.put("Stopped residual server");
	  }
	  server = createLobby(this.port);
	  return server;
  }
  
  private Lobby createLobby(Integer port) throws Exception{
	  if(port != null){
		  Configuration.set(Configuration.PORT_KEY,port.toString());
		  log.put("Set Lobby port to " + port);
	  }
	  final Lobby server = new Lobby();
	  server.start();
	  return server;
  }
  
  public V8Interface(){
    currentTurn = new LinkedBlockingQueue<V8Turn>();
    log  = new LinkedBlockingQueue<String>();
    slots = new ArrayList<SlotDescriptor>();
  }
  
  public void setPort(Integer port){
	  this.port = port;
  }
  
  public void addClient(String displayName, boolean canTimeout, boolean shouldBePaused){
	  slots.add(new SlotDescriptor(displayName, canTimeout, shouldBePaused));
  }
  
  public GameRoom createGame(String type) throws Exception{
	  GameRoom gr = server.getGameManager().createGame(type);
	  gr.openSlots(slots);
	  return gr;
  }
  
  public String[] getReservations(GameRoom gr) throws InterruptedException{
	  List<String> l = gr.reserveAllSlots();
	  String[] keys = new String[2];
	  keys[0] = l.get(0);
	  keys[1] = l.get(1);
	  log.put("Slot 1: " + keys[0]);
	  log.put("Slot 2: " + keys[1]);
	  return keys;
  }
  
  
  
  /*

  public V8Game createGame(){

  }

  public V8Game createGameFromReplay(String replay){

  }

  public registerClient(V8Client c){

  }

  public getClientFromPort(int port){
    return new V8Client...;
  }

  public getClientFromJar(String JAR){
    return new V8Client...;
  }

  public getHumanClient(){

  }

  public V8Turn getNextTurn(){
    server.requestNewTurn(r + 1);
    return currentTurn.take();
  }

  public V8Turn getPreviousTurn(){

  }

  public V8Turn getFirstTurn(){

  }

  public V8Turn getLastTurn(){

  }

  public V8Client getActivePlayer(){

  }

  public void setMove(V8Move move){

  }*/
  
  public void run(){
	  
  }
  
  public String getLogEntry() throws InterruptedException{
	  return log.take();
  }

}
