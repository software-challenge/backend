package sc.networking;

import java.io.*;

public class FileSystemInterface implements INetworkInterface {
  private final InputStream inputStream;
  private final OutputStream outputStream;

  public FileSystemInterface(File file) throws FileNotFoundException {
    this.inputStream = new FileInputStream(file);
    this.outputStream = new NullOutputStream(true);
  }

  public FileSystemInterface(InputStream in) {
    this.inputStream = in;
    this.outputStream = new NullOutputStream(true);
  }

  @Override
  public void close() throws IOException {
    this.inputStream.close();
    this.outputStream.close();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return this.inputStream;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    return this.outputStream;
  }

}
