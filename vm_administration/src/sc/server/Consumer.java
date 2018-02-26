package sc.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

public class Consumer
{
  private final Connection	conn;
  protected String			bash;
  protected String			command;
  protected String			queue;
  protected List<IConsumerListener> listeners = new LinkedList<IConsumerListener>();

  public Consumer(String hostname, int port) throws IOException, TimeoutException
  {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(hostname);
    factory.setPort(port);
    factory.setAutomaticRecoveryEnabled(true);
    this.conn = factory.newConnection();
  }

  public void addConsumerListener(IConsumerListener listener) {
    this.listeners.add(listener);
  }

  public void removeConsumerListener(IConsumerListener listener) {
    this.listeners.remove(listener);
  }

  protected void notifyOnStartConsuming() {
    for (IConsumerListener listener : this.listeners) {
      listener.onStartConsuming();
    }
  }

  protected void consume(String queue) throws IOException, TimeoutException
  {
    final Channel ch = this.conn.createChannel();
    ch.queueDeclare(queue, false, false, false, null);

    boolean noAck = true;
    GetResponse response = ch.basicGet(queue, noAck);
    if (response == null)
      {
        // Do nothing
      } else
      {
        Logger.log("Consuming message...");
        notifyOnStartConsuming();
        onConsume(new String(response.getBody()));
      }
    ch.close();
  }

  protected void onConsume(String message)
  {
    int exitValue = -1;
    try
      {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(new String[]{ this.bash, this.command, message });
        process.waitFor();
        exitValue = process.exitValue();
      }
    catch (IOException e)
      {
        Logger.logError("Unable to consume message!");
        e.printStackTrace();
      }
    catch (InterruptedException e)
      {
        Logger.logError("Unable to consume message!");
        e.printStackTrace();
      } finally {
      Logger.log("Message consumed. Exit-Status-Code of started process: " + exitValue);
    }
  }

  protected void free() throws IOException
  {
    this.conn.close();
  }

}
