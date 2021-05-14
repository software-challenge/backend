package sc.networking.clients;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.networking.FileSystemInterface;
import sc.protocol.ProtocolPacket;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This client serves the purpose to load a game state from
 * any XML file (a replay for example).
 * It is used to load a given board to play on.
 */
public final class GameLoaderClient extends XStreamClient implements IPollsHistory {
  private static Logger logger = LoggerFactory.getLogger(GameLoaderClient.class);

  private List<IHistoryListener> listeners = new ArrayList<>();

  public GameLoaderClient(InputStream inputStream) throws IOException {
    super(new FileSystemInterface(inputStream));
    logger.trace("Loading game from {}", inputStream);
  }

  @Override
  protected void onObject(@NotNull ProtocolPacket message) {
    for (IHistoryListener listener : this.listeners) {
      listener.onNewState(null, (IGameState) message);
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
