package control4j.debug;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class Ups
{


  public static void main(String[] args) throws Exception
  {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    Socket socket = new Socket(host, port);
    OutputStream os = socket.getOutputStream();
    InputStream is = socket.getInputStream();
    byte[] request = new byte[] { 0x00, 0x06, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73 };
    os.write(request);
    while (true)
    {
      int response = is.read();
      int len = response * 0xff;
      response = is.read();
      len = len + response;
      if (len == 0) break;
      int read = 0;
      byte[] buffer = new byte[len];
      while (read < len)
      {
        read = is.read(buffer, read, len - read);
      }
      String line = new String(buffer);
      System.out.print(line);
    }
  }
}
