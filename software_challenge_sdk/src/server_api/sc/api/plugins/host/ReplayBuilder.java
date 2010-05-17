package sc.api.plugins.host;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import sc.networking.clients.IControllableGame;
import sc.networking.clients.ObservingClient;
import sc.shared.GameResult;

public class ReplayBuilder
{
	private static final Logger	logger	= LoggerFactory
												.getLogger(ReplayBuilder.class);

	public static void saveReplay(XStream xStream, IControllableGame game,
			String pathname) throws IOException
	{
		saveReplay(xStream, game, pathname, true);
	}

	public static void saveReplay(XStream xStream, IControllableGame game,
			String pathname, boolean useGzip) throws IOException
	{
		String finalPathname = pathname;

		if (useGzip)
		{
			finalPathname += ".gz";
		}

		File file = new File(finalPathname);

		logger.info("Saving Replay to {} ({})", finalPathname, file
				.getAbsoluteFile());

		if (file.exists())
		{
			if (file.isDirectory())
			{
				throw new IOException(
						"Can't delete a directory to save a replay.");
			}
			file.delete();
		}

		File dir = file.getParentFile();
		if (dir != null)
		{
			dir.mkdirs();
		}

		file.createNewFile();

		if (!file.isDirectory())
		{
			saveReplay(xStream, game, file, true);
		}
		else
		{
			logger.warn("Cannot save replay as directory");
		}
	}

	public static void saveReplay(XStream xStream, IControllableGame game,
			File file, boolean useGzip) throws IOException
	{
		OutputStream out = new FileOutputStream(file);

		if (useGzip)
		{
			out = new GZIPOutputStream(out);
		}

		saveReplay(xStream, game, out);
	}

	public static void saveReplay(XStream xStream, IControllableGame game,
			OutputStream out) throws IOException
	{
		if (game instanceof ObservingClient)
		{
			ObservingClient client = (ObservingClient) game;
			ObjectOutputStream objectOut = xStream
					.createObjectOutputStream(out);

			for (Object state : client.getHistory())
			{
				objectOut.writeObject(state);
			}

			GameResult result = client.getResult();
			
			if (result != null)
			{
				objectOut.writeObject(result);
			} else {
				logger.warn("Result was null while saving replay");
			}

			objectOut.flush();
			objectOut.close();
		}
		else
		{
			logger.warn("{} not supported", game.getClass());
		}
	}

	public static InputStream loadReplay(String filename) throws IOException
	{
		return loadReplay(filename, filename.endsWith(".gz"));
	}

	public static InputStream loadReplay(String filename, boolean useGzip)
			throws IOException
	{
		return loadReplay(new File(filename), useGzip);
	}

	public static InputStream loadReplay(File file, boolean useGzip)
			throws FileNotFoundException, IOException
	{
		return loadReplay(new FileInputStream(file), useGzip);
	}

	public static InputStream loadReplay(InputStream in, boolean useGzip)
			throws IOException
	{
		if (useGzip)
		{
			return new GZIPInputStream(in);
		}

		return in;
	}
}
