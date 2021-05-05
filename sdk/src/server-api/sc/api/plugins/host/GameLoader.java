package sc.api.plugins.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameState;
import sc.networking.clients.GameLoaderClient;
import sc.networking.clients.IHistoryListener;
import sc.protocol.room.ErrorMessage;
import sc.shared.GameResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GameLoader implements IHistoryListener {
  private static final Logger logger = LoggerFactory.getLogger(GameLoader.class);
  private volatile boolean finished;
  private Object obj = null;
  private List<Class<?>> clazzes;
  private GameLoaderClient client;

  public GameLoader(List<Class<?>> clazzes) {
    this.finished = false;
    this.clazzes = clazzes;
  }

  public GameLoader(Class<?>... clazz) {
    this(Arrays.asList(clazz));
  }

  public Object loadGame(String filename) {
    try {
      return loadGame(new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Object loadGame(File file) throws IOException {
    return loadGame(new FileInputStream(file), file.getName().endsWith(".gz"));
  }

  public Object loadGame(FileInputStream stream, boolean gzip) throws IOException {
    if (gzip) {
      return loadGame(new GZIPInputStream(stream));
    } else {
      return loadGame(stream);
    }
  }

  public Object loadGame(InputStream file) throws IOException {
    client = new GameLoaderClient(file);
    client.addListener(this);
    client.start();
    while (!finished && !client.isClosed()) {
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        logger.warn("Interrupted while waiting for game to load", e);
      }
    }
    return this.obj;
  }

  @Override
  public void onGameError(String roomId, ErrorMessage error) {
  }

  @Override
  public void onGameOver(String roomId, GameResult result) {
    this.finished = true;
  }

  @Override
  public void onNewState(String roomId, IGameState state) {
    logger.debug("Received new state");
    if (!this.finished) {
      for (Class<?> clazz : this.clazzes) {
        if (clazz.isInstance(state)) {
          logger.debug("Received game info of type {}", clazz.getName());
          this.obj = clazz.cast(state);
          this.finished = true;
          this.client.stop();
        }
      }
    }
  }

}

