package sc.sample.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import sc.helpers.ReplayBuilder;
import sc.protocol.IControllableGame;
import sc.protocol.LobbyClient;
import sc.protocol.RequestResult;
import sc.protocol.clients.ObservingClient;
import sc.protocol.responses.PrepareGameResponse;
import sc.sample.protocol.ProtocolDefinition;
import sc.sample.server.GamePluginImpl;

import com.thoughtworks.xstream.XStream;

public class ClientApp
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{
		final XStream xStream = new XStream();
		LobbyClient admin = new LobbyClient(xStream, ProtocolDefinition
				.getProtocolClasses());
		RequestResult<PrepareGameResponse> preparation = admin
				.prepareGameAndWait(GamePluginImpl.PLUGIN_UUID, 2);

		if (!preparation.isSuccessful())
		{
			throw new RuntimeException("Couldn't prepare the game.");
		}

		LobbyClient observerClient = new LobbyClient(xStream, ProtocolDefinition
				.getProtocolClasses());
		IControllableGame observer = observerClient.observe(preparation.getResult());

		final Semaphore sem = new Semaphore(0);
		final Object lock = new Object();
		final Queue<String> reservations = new LinkedList<String>();
		reservations.addAll(preparation.getResult().getReservations());

		Runnable automaticClient = new Runnable() {
			@Override
			public void run()
			{
				try
				{
					String reservation = null;
					synchronized (lock)
					{
						reservation = reservations.poll();
					}

					SimpleClient client = new SimpleClient(xStream) {
						public void onGameLeft(String roomId)
						{
							super.onGameLeft(roomId);
							sem.release();
							System.out.println("Released.");
						};
					};
					client.joinPreparedGame(reservation);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		Thread t1 = new Thread(automaticClient);
		Thread t2 = new Thread(automaticClient);
		t1.start();
		t2.start();

		System.out.println("Waiting.");
		sem.acquire(2);

		System.out.println("Done.");
		admin.close();
		observerClient.close();
		ReplayBuilder.saveReplay(observer, new FileOutputStream("./replay.xml"));
	}
}
