package control4j.protocols.signal;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import javax.xml.stream.XMLStreamException;

import control4j.Signal;

public class SignalRequestXmlOutputStreamTest implements Runnable
{

  java.io.PipedOutputStream os;
  java.io.PipedInputStream is;
  SignalRequestXmlOutputStream xmlOs;
  SignalRequestXmlInputStream xmlIs;

  @Before
  public void initialize() throws XMLStreamException, java.io.IOException
  {
    os = new java.io.PipedOutputStream();
    is = new java.io.PipedInputStream(os);
    xmlOs = new SignalRequestXmlOutputStream(os);
    xmlIs = new SignalRequestXmlInputStream(is);
  }

  @Test
  public void test1() throws XMLStreamException, java.io.IOException
  {
    new Thread(this).start();
    Request request = xmlIs.readMessage();
    assertNotNull(request);
  }

  public void run()
  {
    try {
    Request request = new DataRequest();
    xmlOs.write(request);
    xmlOs.close();
    } catch (java.io.IOException e) {};
  }


}

