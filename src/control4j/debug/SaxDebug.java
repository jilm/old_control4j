package control4j.debug;

import java.io.InputStream;
import java.io.FileInputStream;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxDebug
{
  public static void main(String[] args) throws Exception
  {
    String filename = args[0];
    InputStream is = new FileInputStream(filename);
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    SAXParser parser = factory.newSAXParser();
    parser.parse(is, new Handler());
    is.close();
  }

  private static class Handler extends DefaultHandler
  {
    @Override
    public void startDocument()
    {
      System.out.println("event: startDocument");
    }

    @Override
    public void endDocument()
    {
      System.out.println("event: endDocument");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      System.out.println("event: startElement");
      String message = String.format("  parameters: uri: %s, local name: %s, qname: %s", uri, localName, qName);
      System.out.println(message);
    }
  }
}
