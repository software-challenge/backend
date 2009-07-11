package sc.plugin2010;

import java.io.IOException;

public class GamePlayTest
{
	public static void main(String[] args) throws IOException,
			InterruptedException
	{
		TestClient c1 = new TestClient();
		Thread.sleep(250);
		c1.joinAnyGame();
		Thread.sleep(250);
		TestClient c2 = new TestClient();
		Thread.sleep(250);
		c2.joinAnyGame();
	}
}
