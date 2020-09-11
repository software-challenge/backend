package sc.networking.clients;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.networking.FileSystemInterface;
import sc.protocol.responses.ProtocolMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * This client serves the purpose to load a game state from
 * any XML file (a replay for example). It is used to load
 * a given board to play on (i.e.).
 *
 * @author sca
 */
public final class GameLoaderClient extends XStreamClient implements IPollsHistory {
  private static Logger logger = LoggerFactory.getLogger(GameLoaderClient.class);

  private List<IHistoryListener> listeners = new ArrayList<>();

  public GameLoaderClient(XStream xstream, InputStream inputStream) throws IOException {
    super(xstream, new FileSystemInterface(inputStream));
    logger.trace("Loading game from {}", inputStream);
  }

  @Override
  protected void onObject(ProtocolMessage o) {
    for (IHistoryListener listener : this.listeners) {
      listener.onNewState(null, (IGameState) o);
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
