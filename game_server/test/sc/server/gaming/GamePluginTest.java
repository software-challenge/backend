package sc.server.gaming;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GamePluginTest {
  int i = 0;
  @Before
  public void init()
  {
    i = 1;
  }

  @Test
  public void prepareGameTest(){
    Assert.assertEquals(i, 1);
    i++;
  }


  @Test
  public void prepareGameTest2(){
    Assert.assertEquals(i, 1);
    i++;
  }

}
