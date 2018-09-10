package sc.server.network;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.UnprocessedPacketException;
import sc.protocol.responses.ProtocolMessage;
import sc.protocol.responses.RoomPacket;
import sc.server.Configuration;
import sc.server.helpers.StringNetworkInterface;
import sc.shared.InvalidGameStateException;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MockClient extends Client {
  private final static Logger logger = LoggerFactory.getLogger(MockClient.class);
  private final Queue<Object> outgoingMessages = new ArrayDeque<>();
  private BlockingQueue<Object> objects = new LinkedBlockingQueue<>();
  private final XStream xStream;

  public MockClient(StringNetworkInterface stringInterface, XStream xStream)
          throws IOException {
    super(stringInterface, xStream);
    this.xStream = xStream;
  }

  public MockClient() throws IOException {
    this(new StringNetworkInterface("<protocol>"), Configuration
            .getXStream());
  }

  @Override
  public synchronized void send(ProtocolMessage packet) {
    super.send(packet);

    Object parsedPacket = this.xStream.fromXML(this.xStream.toXML(packet));
    this.outgoingMessages.add(parsedPacket);
  }

  public Object popMessage() {
    return this.outgoingMessages.poll();
  }

  @SuppressWarnings("unchecked")
  public <T> T seekMessage(Class<T> type) {
    int i = -1;
    Object current;
    do {
      i++;
      current = popMessage();
    } while (current != null && current.getClass() != type);

    if (current == null) {
      throw new RuntimeException(
              "Could not find a message of type " + type + ", searched " + i + " messages");
    } else {
      if (i > 0) {
        logger.info("Skipped {} messages.", i);
      }
      return (T) current;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T seekRoomMessage(String roomId, Class<T> type) {
    int i = -1;
    Object current = null;
    do {
      i++;
      RoomPacket response = seekMessage(RoomPacket.class);
      if (roomId.equals(response.getRoomId())) {
        current = response.getData();
      }
    } while (current != null && current.getClass() != type);

    if (current == null) {
      throw new RuntimeException("Could not find a message of the specified type");
    } else {
      if (i > 0) {
        logger.info("Skipped {} messages.", i);
      }
      return (T) current;
    }
  }

  @Override
  protected void onObject(ProtocolMessage o) throws UnprocessedPacketException, InvalidGameStateException {
    super.onObject(o);
    this.objects.add(o);
  }

  public Object receive() throws InterruptedException {
    return this.objects.take();
  }

}
