package sc.server.helpers;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.UnprocessedPacketException;
import sc.protocol.ProtocolPacket;
import sc.protocol.room.RoomPacket;
import sc.server.network.Client;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MockClient extends Client {
  private final static Logger logger = LoggerFactory.getLogger(MockClient.class);
  private final Queue<Object> outgoingMessages = new ArrayDeque<>();
  private BlockingQueue<Object> objects = new LinkedBlockingQueue<>();

  public MockClient(StringNetworkInterface stringInterface) throws IOException {
    super(stringInterface);
  }

  public MockClient() throws IOException {
    this(new StringNetworkInterface("<protocol>"));
  }

  @Override
  public synchronized void send(ProtocolPacket packet) {
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
  protected void onObject(@NotNull ProtocolPacket message) throws UnprocessedPacketException {
    super.onObject(message);
    this.objects.add(message);
  }

  public Object receive() throws InterruptedException {
    return this.objects.take();
  }

}
