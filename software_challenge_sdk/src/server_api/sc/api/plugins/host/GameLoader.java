package sc.api.plugins.host;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import sc.networking.clients.GameLoaderClient;
import sc.networking.clients.IHistoryListener;
import sc.protocol.responses.ErrorResponse;
import sc.shared.GameResult;

public class GameLoader<T> implements IHistoryListener
{
	private static final Logger	logger = LoggerFactory.getLogger(GameLoader.class);
	private volatile boolean finished;
	private T obj = null;
	private Class<?> clazz;
	private GameLoaderClient client;
	
	public GameLoader(Class<?> clazz) {
		this.finished = false;
		this.clazz = clazz;
	}
	
	public T loadGame(XStream xstream, String filename) {
		try {
			return loadGame(xstream, new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public T loadGame(XStream xstream, File file) throws IOException {
		return loadGame(xstream, new FileInputStream(file), file.getName().endsWith(".gz"));
	}
	
	public T loadGame(XStream xstream, FileInputStream stream, boolean gzip) throws IOException {
		if (gzip) {
			return loadGame(xstream, new GZIPInputStream(stream));
		} else {
			return loadGame(xstream, stream);
		}
	}
	
	public T loadGame(XStream xstream, InputStream file) throws IOException {
		this.client = new GameLoaderClient(xstream, file);
		this.client.addListener(this);
		this.client.start();
		while(!this.finished) {};
		logger.debug("Finished");
		return this.obj;
	}

	@Override
	public void onGameError(String roomId, ErrorResponse error) 
	{
	}

	@Override
	public void onGameOver(String roomId, GameResult o)
	{
		this.finished = true;
	}

	@Override
	public void onNewState(String roomId, Object o)
	{
		logger.debug("Received new state");
		if (!this.finished) {
			if (this.clazz.isInstance(o)) {
				logger.debug("Received game info");
				this.obj = (T) o;
				this.finished = true;
				this.client.close();
			}
		}
	}

}

