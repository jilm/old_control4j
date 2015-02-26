package control4j.protocols.signal;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import javax.xml.stream.XMLStreamException;

import control4j.Signal;

public class SignalResponseXmlInputStreamTest implements Runnable
{

  DataResponse response;
  java.io.PipedOutputStream os;
  java.io.PipedInputStream is;
  SignalResponseXmlOutputStream xmlOs;
  SignalResponseXmlInputStream xmlIs;
  Signal signal1;
  Signal signal2;

  @Before
  public void initialize() throws Exception
  {
    // prepare a message to send
    response = new DataResponse();
    signal1 = Signal.getSignal(123.456);
    signal2 = Signal.getSignal();
    response.put("signal1", signal1);
    response.put("signal2", signal2);
    // prepare streams
    os = new java.io.PipedOutputStream();
    is = new java.io.PipedInputStream(os);
    xmlOs = new SignalResponseXmlOutputStream(os);
    xmlIs = new SignalResponseXmlInputStream(is);
  }

  @Test(timeout=1000)
  public void test1() throws Exception
  {
    new Thread(this).start();
    Response message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    assertNotNull(message);
  }

  @Test(timeout=1000)
  public void test2() throws Exception
  {
    new Thread(this).start();
    Response message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    DataResponse received = (DataResponse)message;
    Signal signal = received.get("signal1");
    System.out.println(signal);
    System.out.println(signal1.equals(signal));
    assertTrue(signal1.equals(signal));
  }

  @Test(timeout=1000)
  public void test3() throws Exception
  {
    new Thread(this).start();
    Response message = (DataResponse)xmlIs.readMessage();
    System.out.println("message received");
    DataResponse received = (DataResponse)message;
    assertTrue(signal2.equals(received.get("signal2")));
  }

  public void run()
  {
    try {
    xmlOs.write(response);
    xmlOs.close();
    } catch (Exception e) {};
  }

}
