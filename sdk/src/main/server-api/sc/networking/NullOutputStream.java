package sc.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
  private boolean closed = false;
  private boolean warnOnWrite;
  private static final Logger logger = LoggerFactory.getLogger(NullOutputStream.class);

  public NullOutputStream() {
    this(false);
  }

  public NullOutputStream(boolean warnOnWrite) {
    this.warnOnWrite = warnOnWrite;
  }

  @Override
  public void write(int b) throws IOException {
    warn();
  }

  private void warn() throws IOException {
    if (this.warnOnWrite) {
      logger.warn("Wrote data to a NullOutputStream.");
    }
    if (this.closed)
      throw new IOException("Write to a closed stream");
  }

  @Override
  public void write(byte[] data, int offset, int length) throws IOException {
    warn();

    if (data == null)
      throw new NullPointerException("data is null");
  }

  @Override
  public void close() {
    this.closed = true;
  }

}
