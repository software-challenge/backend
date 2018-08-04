package sc.api.plugins.host;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.networking.clients.GameLoaderClient;
import sc.networking.clients.IHistoryListener;
import sc.protocol.responses.ProtocolErrorMessage;
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
  
  public Object loadGame(XStream xstream, String filename) {
    try {
      return loadGame(xstream, new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public Object loadGame(XStream xstream, File file) throws IOException {
    return loadGame(xstream, new FileInputStream(file), file.getName().endsWith(".gz"));
  }
  
  public Object loadGame(XStream xstream, FileInputStream stream, boolean gzip) throws IOException {
    if (gzip) {
      return loadGame(xstream, new GZIPInputStream(stream));
    } else {
      return loadGame(xstream, stream);
    }
  }
  
  public Object loadGame(XStream xstream, InputStream file) throws IOException {
    client = new GameLoaderClient(xstream, file);
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
  public void onGameError(String roomId, ProtocolErrorMessage error) {
  }
  
  @Override
  public void onGameOver(String roomId, GameResult o) {
    this.finished = true;
  }
  
  @Override
  public void onNewState(String roomId, Object o) {
    logger.debug("Received new state");
    if (!this.finished) {
      for (Class<?> clazz : this.clazzes) {
        if (clazz.isInstance(o)) {
          logger.debug("Received game info of type {}", clazz.getName());
          this.obj = clazz.cast(o);
          this.finished = true;
          this.client.stop();
        }
      }
    }
  }
  
}

