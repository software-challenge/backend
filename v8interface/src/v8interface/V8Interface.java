package v8interface;

import java.util.concurrent.LinkedBlockingQueue;

public class V8Interface implements Runnable{
  LinkedBlockingQueue<String> buffer;

  public V8Interface(){
    buffer = new LinkedBlockingQueue<String>(1);
  }

  public String test(){
    return "Hello World";
  }

  public String testAsync() throws InterruptedException{
    Thread t = new Thread(this);
    t.start();
      return buffer.take();
  }

  public void run(){
    try{
      Thread.sleep(2000);
      buffer.put("Hello Async World!");
    }catch(InterruptedException ie){

    }
  }
}
