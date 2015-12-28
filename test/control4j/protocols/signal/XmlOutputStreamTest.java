package control4j.protocols.signal;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import java.util.HashMap;

import control4j.Signal;

public class XmlOutputStreamTest implements Runnable
{

  java.io.PipedOutputStream os;
  java.io.PipedInputStream is;
  XmlOutputStream xmlOs;
  XmlInputStream xmlIs;
  DataResponse response;
  XmlOutputStream xmlStream;

  @Before
  public void initialize() throws XMLStreamException, java.io.IOException
  {
    os = new java.io.PipedOutputStream();
    is = new java.io.PipedInputStream(os);
    xmlOs = new XmlOutputStream(os);
    xmlIs = new XmlInputStream(is);
    Signal signal1 = Signal.getSignal(123.456);
    Signal signal2 = Signal.getSignal();
    Map<String, Signal> signals = new HashMap<>();
    signals.put("signal1", signal1);
    signals.put("signal2", signal2);
    response = new DataResponse(signals);
    xmlStream = new XmlOutputStream(System.out);
  }

  @Test
  public void testRequst1() throws XMLStreamException, java.io.IOException
  {
    new Thread(this).start();
    DataRequest request = (DataRequest)xmlIs.readMessage();
    assertNotNull(request);
  }

  public void run()
  {
    try {
    DataRequest request = new DataRequest();
    xmlOs.write(request);
    xmlOs.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    };
  }


  @Test
  public void test1() throws XMLStreamException, java.io.IOException
  {
    xmlStream.write(response);
  }


}
