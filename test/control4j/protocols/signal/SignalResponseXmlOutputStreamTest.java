package control4j.protocols.signal;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import javax.xml.stream.XMLStreamException;

import control4j.Signal;

public class SignalResponseXmlOutputStreamTest
{

  DataResponse response;
  SignalResponseXmlOutputStream xmlStream;

  @Before
  public void initialize() throws XMLStreamException
  {
    response = new DataResponse();
    Signal signal1 = Signal.getSignal(123.456);
    Signal signal2 = Signal.getSignal();
    response.put("signal1", signal1);
    response.put("signal2", signal2);
    xmlStream = new SignalResponseXmlOutputStream(System.out);
  }

  @Test
  public void test1() throws XMLStreamException
  {
    xmlStream.write(response);
  }


}

