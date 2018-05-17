package sc.networking.clients;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.networking.FileSystemInterface;

import com.thoughtworks.xstream.XStream;
import sc.protocol.responses.ProtocolMessage;


/**
 * This client serves the purpose to load a game state from
 * any XML file (a replay for example). It is used to load
 * a given board to play on (i.e.).
 *
 * @author sca
 */
public final class GameLoaderClient extends XStreamClient implements IPollsHistory {

  private static Logger logger = LoggerFactory.getLogger(GameLoaderClient.class);

  private List<IHistoryListener> listeners = new LinkedList<IHistoryListener>();

  public GameLoaderClient(XStream xstream, InputStream inputStream) throws IOException {
    super(xstream, new FileSystemInterface(inputStream));
    //logger.info("Loading game from {}", inputStream);
  }

  @Override
  protected void onObject(ProtocolMessage o) {
    for (IHistoryListener listener : this.listeners) {
      listener.onNewState(null, o);
    }
  }

  @Override
  public void addListener(IHistoryListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void removeListener(IHistoryListener listener) {
    this.listeners.remove(listener);
  }

}
