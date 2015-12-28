package control4j.protocols.signal;

import control4j.Signal;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.HashMap;

public class XmlInputStreamTest implements Runnable
{

  String xmlRequestString = "<?xml version='1.0' ?><request xmlns='http://control4j.lidinsky.cz/signal'/>";

  @Test(timeout=1000)
  public void test1() throws java.io.IOException
  {
    java.io.InputStream stream
      = new java.io.StringBufferInputStream(xmlRequestString);
    XmlInputStream xmlStream = new XmlInputStream(stream);
    DataRequest request = (DataRequest)xmlStream.readMessage();
    assertNotNull(request);
  }

  DataResponse response;
  java.io.PipedOutputStream os;
  java.io.PipedInputStream is;
  XmlOutputStream xmlOs;
  XmlInputStream xmlIs;
  Signal signal1;
  Signal signal2;

  @Before
  public void initialize() throws Exception
  {
    // prepare a message to send
    signal1 = Signal.getSignal(123.456);
    signal2 = Signal.getSignal();
    HashMap<String, Signal> signals = new HashMap<>();
    signals.put("signal1", signal1);
    signals.put("signal2", signal2);
    response = new DataResponse(signals);
    // prepare streams
    os = new java.io.PipedOutputStream();
    is = new java.io.PipedInputStream(os);
    xmlOs = new XmlOutputStream(os);
    xmlIs = new XmlInputStream(is);
  }

  @Test(timeout=1000)
  public void test2() throws Exception
  {
    new Thread(this).start();
    DataResponse message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    assertNotNull(message);
  }

  @Test(timeout=1000)
  public void test3() throws Exception
  {
    new Thread(this).start();
    DataResponse message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    DataResponse received = message;
    Signal signal = received.getData().get("signal1");
    System.out.println(signal);
    System.out.println(signal1.equals(signal));
    assertTrue(signal1.equals(signal));
  }

  @Test(timeout=1000)
  public void test4() throws Exception
  {
    new Thread(this).start();
    DataResponse message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    DataResponse received = message;
    assertTrue(signal2.equals(received.getData().get("signal2")));
  }

  public void run()
  {
    try {
    xmlOs.write(response);
    xmlOs.flush();
    os.flush();
    xmlOs.close();
    os.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    };
  }

}
