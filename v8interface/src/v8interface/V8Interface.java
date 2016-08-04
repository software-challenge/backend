package v8interface;

import java.util.concurrent.LinkedBlockingQueue;

public class V8Interface implements Runnable{
  LinkedBlockingQueue<V8Turn> currentTurn;

  public V8Interface(){
    currentTurn = new LinkedBlockingQueue<V8Turn>();
  }

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

  }

}
