package root;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.KXml2Driver;
import sc.plugin2019.Direction;
import sc.plugin2019.GameState;
import sc.plugin2019.Move;
import sc.protocol.LobbyProtocol;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.shared.GameResult;
import sc.shared.SlotDescriptor;
import sc.shared.WelcomeMessage;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class WorkerThread {
  /**
   *
   */
  private JTextArea output;
  private Socket socket;
  private XStream stream;
  private Thread read;
  private ObjectOutputStream outputStream;
  private ObjectInputStream is;
  private ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<>();

  private static String reserveration[] = new String[2];
  private static String roomId;
  private static String clientState[] = new String[2];
  private int type;
  private List<IStateUpdate> stateChangeListener = new LinkedList<>();
  private List<IErrorHandler> errorListener = new LinkedList<>();


  void move(int x, int y, Direction d){
    if (roomId == null) return;
    try {
      if(type!=0){
        Move move = new Move(x,y, d);
        RoomPacket packet = new RoomPacket(roomId, move);
        System.out.println(stream.toXML(packet));
        outputStream.writeObject(packet);
        outputStream.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



  private static Class[] clazz = new Class[]{
      LobbyProtocol.class,
      PrepareGameProtocolMessage.class,
      SlotDescriptor.class,
      AuthenticateRequest.class,
      PrepareGameRequest.class,
      WelcomeMessage.class,
      JoinRoomRequest.class,
      RoomPacket.class,
      JoinGameProtocolMessage.class,
      Move.class,
      ObservationProtocolMessage.class,
      ObservationRequest.class,
      StepRequest.class,
      MementoPacket.class,
      GameState.class,
      GameResult.class,
      LeftGameEvent.class,
      ProtocolErrorMessage.class,
      PauseGameRequest.class,
      ControlTimeoutRequest.class
  };

  WorkerThread(JTextArea output, Socket socket, int type) {
    this.output = output;
    this.socket = socket;
    this.stream = new XStream(new KXml2Driver());

    stream.setMode(XStream.NO_REFERENCES);
    stream.setClassLoader(Window.class.getClassLoader());

    for(Class c : clazz){
      stream.processAnnotations(c);
    }
    try {
      initReadThread();
      outputStream = stream.createObjectOutputStream(socket.getOutputStream(),"protocol");
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.type = type;
  }

  public void addStateChangedListener(IStateUpdate stateUpdate){
    this.stateChangeListener.add(stateUpdate);
  }

  public void close(){
    try {
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void work0() {

    try {

      outputStream.writeObject(new AuthenticateRequest("examplepassword"));
      outputStream.flush();
      outputStream.writeObject(new PrepareGameRequest("swc_2019_piranhas"));
      outputStream.flush();


      PrepareGameProtocolMessage protocolMessage = (PrepareGameProtocolMessage)read();
      String reserveration1 =  protocolMessage.getReservations().get(0);
      String reserveration2 =  protocolMessage.getReservations().get(1);

      outputStream.writeObject(new ObservationRequest(protocolMessage.getRoomId()));
      outputStream.flush();

      roomId = protocolMessage.getRoomId();


      addLine("room: "+protocolMessage.getRoomId());
      addLine("reservation1: "+reserveration1);
      addLine("reservation2: "+reserveration2);
      reserveration[0] = reserveration1;
      reserveration[1] = reserveration2;


      ObservationProtocolMessage obs = (ObservationProtocolMessage)read();
      addLine("observing the room: "+obs.getRoomId());

      while(clientState[0] == null || clientState[1] == null || !clientState[0].equals("connected") || !clientState[0].equals("connected")){
        Thread.sleep(10);
      }


      outputStream.writeObject(new PauseGameRequest(roomId, false));
      outputStream.flush();

      outputStream.writeObject(new ControlTimeoutRequest(roomId, false, 0));
      outputStream.writeObject(new ControlTimeoutRequest(roomId, false, 1));
      outputStream.flush();

      addLine("Both clients are connected");

      outputStream.writeObject(new StepRequest(obs.getRoomId()));
      outputStream.flush();

      while(!Thread.interrupted() && !socket.isClosed()){
        System.out.println("read new object in T"+type);
        Object o = read();
        addLine("new object: " + o);
        if (o instanceof RoomPacket){
          RoomPacket packet = (RoomPacket) o;
          addLine("    of typ"+packet.getData());
          Object data = packet.getData();
          if (data instanceof ProtocolErrorMessage){
            ProtocolErrorMessage leftGameEvent = (ProtocolErrorMessage)data;
            System.out.println("Error: "+leftGameEvent.getMessage());
            for(IErrorHandler handler : errorListener){
              handler.onError(leftGameEvent.getMessage());
            }
          } else if (data instanceof MementoPacket){
            MementoPacket memento = (MementoPacket)data;
            GameState state = (GameState) memento.getState();
            for(IStateUpdate listener : stateChangeListener){
              listener.onStateChanged(state);
            }
          }
        }
      }
      if (socket.isClosed()) System.out.println("that makes sense");
      addLine("Request a move");

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }



  private void work1() {
    try {

      while(reserveration[0] == null) Thread.sleep(10);
      Thread.sleep(100);
      write(new JoinPreparedRoomRequest(reserveration[0]));
      JoinGameProtocolMessage answer = (JoinGameProtocolMessage) read();

      addLine("I joined the room: "+answer.getRoomId());
      RoomPacket welcome = (RoomPacket) read();
      WelcomeMessage welcomeMessage = (WelcomeMessage) welcome.getData();
      addLine("my color is: "+welcomeMessage.getPlayerColor());

      clientState[0] = "connected";

      System.out.println("memento? "+read());

      Object o;
      while(!Thread.interrupted() && !socket.isClosed()){
        o = read();
        if (o instanceof RoomPacket){

          addLine("new message: "+((RoomPacket)o).getData());
        } else {
          addLine("new message: "+o);
        }
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void addText(String text){
    output.setText(output.getText()+text);
    output.repaint();
  }
  private void addLine(String text){
    output.setText(output.getText()+text+"\n");
    output.repaint();
  }


  private void work2() {

    try {

      while(reserveration[1] == null) Thread.sleep(10);
      Thread.sleep(100);

      write(new JoinPreparedRoomRequest(reserveration[1]));
      JoinGameProtocolMessage answer = (JoinGameProtocolMessage) read();

      addLine("I joined the room: "+answer.getRoomId());

      RoomPacket welcome = (RoomPacket) read();

      WelcomeMessage welcomeMessage = (WelcomeMessage) welcome.getData();
      addLine("my color is: "+welcomeMessage.getPlayerColor());


      clientState[1] = "connected";
      Object o;
      while(!Thread.interrupted() && !socket.isClosed()){
        o = read();
        if (o instanceof RoomPacket){

          addLine("new message: "+((RoomPacket)o).getData());
        } else {
          addLine("new message: "+o);
        }
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void write(Object msg){
    try {
      outputStream.writeObject(msg);
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private synchronized Object read(){
    while (queue.size() == 0 && !socket.isClosed() && !Thread.interrupted());
    System.out.println("done waiting "+type);
    return queue.poll();
  }

  private void initReadThread(){

    read = new Thread(()->{
      try {
        is = stream.createObjectInputStream(socket.getInputStream());

        while(!read.isInterrupted() && !Thread.interrupted()){
          try {

            Object o = is.readObject();
            queue.add(o);
            System.out.println("added object to queue "+type+": "+o);
            if (o instanceof RoomPacket){
              Object roomObj = ((RoomPacket)o).getData();
              System.out.println("|---"+roomObj);
              if (roomObj instanceof ProtocolErrorMessage){
                read.interrupt();
              }
            }
          } catch (EOFException | StreamException e){
            System.err.println("socket closed");
            break;
          }
        }
      } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
      }
    });
    read.setName("XStream reader thread");
    read.setDaemon(false);
    read.start();
    while (!read.isAlive()) ;

    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  void work(){
    switch (type){
      case 0: work0(); break;
      case 1: work1(); break;
      case 2: work2(); break;
    }
  }

  public void addErrorListener(IErrorHandler iErrorHandler) {
    errorListener.add(iErrorHandler);
  }
}
