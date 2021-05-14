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
import java.util.zip.GZIPInputStream;

public class GameLoader implements IHistoryListener {
  private static final Logger logger = LoggerFactory.getLogger(GameLoader.class);
  private volatile boolean finished;
  private int turn = 0;
  private IGameState obj = null;
  private GameLoaderClient client;

  public IGameState loadGame(File file, int turn) throws IOException {
    this.turn = turn;
    return loadGame(new FileInputStream(file), file.getName().endsWith(".gz"));
  }

  public IGameState loadGame(FileInputStream stream, boolean gzip) throws IOException {
    if (gzip) {
      return loadGame(new GZIPInputStream(stream));
    } else {
      return loadGame(stream);
    }
  }

  public IGameState loadGame(InputStream file) throws IOException {
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
    if (!this.finished && state.getTurn() >= turn) {
      this.obj = state;
      this.finished = true;
      this.client.stop();
    }
  }

}

