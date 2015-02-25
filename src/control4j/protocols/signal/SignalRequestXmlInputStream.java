package control4j.protocols.signal;

/*
 *  Copyright 2015 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.InputStream;
import java.io.IOException;
import java.util.Date;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import control4j.Signal;
import control4j.protocols.IRequest;
import static control4j.tools.Logger.*;

public class SignalRequestXmlInputStream 
implements control4j.protocols.tcp.IInputStream<Request>
{

  protected InputStream stream;
  protected XMLStreamReader reader;

  public SignalRequestXmlInputStream(InputStream stream) 
  throws XMLStreamException
  {
    this.stream = new XmlInputStream(stream);
  }

  public Request readMessage() throws IOException
  {
    try
    {
      XMLInputFactory factory = XMLInputFactory.newFactory();
      reader = factory.createXMLStreamReader(stream);
      // find the root element
      int eventType = reader.nextTag();
      if (eventType != XMLStreamReader.START_ELEMENT)
	throw new IOException("Start element expected");
      String root = reader.getLocalName();
      if (root.equals("request"))
      {
	DataRequest message = new DataRequest();
	//readRequest(message);
	return message;
      }
      else
	throw new IOException("A request start element is expected");
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
    finally
    {
      try
      {
        if (reader != null) reader.close();
      }
      catch (XMLStreamException ex)
      {
	catched(getClass().getName(), "readMessage", ex);
      }
    }
  }

  public void close() throws IOException
  {
    stream.close();
  }

  private void printCode()
  {
    int code = reader.getEventType();
    javax.xml.stream.Location location = reader.getLocation();
    String message;
    message = "[" + location.getLineNumber() + ", " + location.getColumnNumber() + "]";
    if (code == reader.START_ELEMENT || code == reader.END_ELEMENT)
    {
      message += " " + code + ": " + reader.getName();
    }
    else
    {
      message += " " + code;
    }
    System.out.println(message);
  }
  
}
