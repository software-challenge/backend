package root;

import com.thoughtworks.xstream.XStream;
import sc.plugin2019.Board;
import sc.plugin2019.Direction;
import sc.plugin2019.Field;
import sc.shared.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.Objects;

public class Window {
  private JPanel rootPane;
  private JButton acceptButton;
  private JTextField hostTextField;
  private JComboBox portSelection;
  private JTextArea outputAdmin;
  private JTextArea outputCli1;
  private JTextArea outputCli2;
  private JButton randomButton;
  private JPanel fieldState;
  private JTextField xCoordinate;
  private JComboBox direction;
  private JTextField yCoordinate;
  private JPanel coordinateMovePanel;
  private JButton moveButton;
  private JLabel statusLabel;

  private XStream stream;
  WorkerThread thread0;
  WorkerThread thread1;
  WorkerThread thread2;

  public int port = 0;
  public String host ="";
  Thread[] t;
  PlayerColor turn = null;

  static Window win = new Window();

  public static void main(String[] args) {
    JFrame frame = new JFrame("Window");
    frame.setContentPane(win.rootPane);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    System.out.println("win destroyed");
  }

  public Window() {
    portSelection.addItem("13050");
    portSelection.addItem("13051");
    fieldState.setLayout(new GridLayout(10,0));
    for(int i = 0; i< 100; i++){
      fieldState.add(new JLabel("-"));
    }

    for(Direction dir : Direction.values()){
      if (dir != Direction.INVALID){

        direction.addItem(dir);
      }
    }


    t = new Thread[3];
    acceptButton.addActionListener(actionEvent -> {
      port = Integer.parseInt((String) Objects.requireNonNull(portSelection.getSelectedItem()));
      host = hostTextField.getText();

      try {
        thread0 = new WorkerThread(outputAdmin, new Socket(host, port), 0);
        thread0.addStateChangedListener(state -> {
          Board board = state.getBoard();
          turn = state.getCurrentPlayerColor();
          statusLabel.setText("Status: "+turn+"'s turn");
          fieldState.removeAll();
          for(int j = 9; j>= 0; j--){
            for(int i = 0; i < 10; i++){
              Field field = board.getField(i,j);
              String value = "<html>("+(i)+","+(j)+")<br>";
              value += field.getPiranha()==null?"_":field.getPiranha().toString();
              value += "</html>";
              fieldState.add(new JLabel(value));
            }
          }
          fieldState.updateUI();
        });
        thread0.addErrorListener(errormsg -> {
          statusLabel.setText("ERROR: "+errormsg);
        });

        thread1 = new WorkerThread(outputCli1, new Socket(host, port), 1);

        thread2 = new WorkerThread(outputCli2, new Socket(host, port), 2);
      } catch (Exception e){
        e.printStackTrace();
      }


      t[0] = null;
      t[1] = null;
      t[2] = null;

      // Admin client
      t[0] = new Thread(() -> {
        thread0.work();
      });
      t[0].setDaemon(true);
      t[0].start();

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      t[1] = new Thread(()-> thread1.work());
      t[1].setDaemon(true);
      t[1].start();

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      t[2] = new Thread(()-> thread2.work());
      t[2].setDaemon(true);
      t[2].start();

    });
    randomButton.addActionListener(actionEvent -> {
    });
    moveButton.addActionListener(actionEvent -> {
      int x = Integer.parseInt(xCoordinate.getText());
      int y = Integer.parseInt(yCoordinate.getText());
      Direction d = (Direction) direction.getSelectedItem();

      if (turn == PlayerColor.RED){
        thread1.move(x,y,d);
      } else if (turn == PlayerColor.BLUE){
        thread2.move(x,y,d);
      }

    });
  }
}
