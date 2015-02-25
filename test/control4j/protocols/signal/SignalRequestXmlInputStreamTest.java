package control4j.protocols.signal;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class SignalRequestXmlInputStreamTest
{

  String xmlRequestString = "<?xml version='1.0' ?><request xmlns='http://control4j.lidinsky.cz/signal'/>";
  java.io.InputStream stream;
  SignalRequestXmlInputStream xmlStream;

  @Before
  public void initialize() throws javax.xml.stream.XMLStreamException
  {
    stream = new java.io.StringBufferInputStream(xmlRequestString);
    xmlStream = new SignalRequestXmlInputStream(stream);
  }

  @Test
  public void test1() throws java.io.IOException
  {
    Request request = xmlStream.readMessage();
    assertNotNull(request);
  }


}

