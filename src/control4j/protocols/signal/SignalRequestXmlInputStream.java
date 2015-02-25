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

public class SignalRequestXmlInputStream 
implements control4j.protocols.tcp.IInputStream<Request>
{

  protected InputStream stream;
  protected XMLStreamReader reader;

  public SignalRequestXmlInputStream(InputStream stream) throws XMLStreamException
  {
    this.stream = new XmlInputStream(stream);
  }

  public Request readMessage() throws IOException
  {
    try
    {
      XMLInputFactory factory = XMLInputFactory.newFactory();
      reader = factory.createXMLStreamReader(stream);
      // find root element
      int eventType = reader.nextTag();
      if (eventType == XMLStreamReader.END_ELEMENT)
	throw new IOException("Start element expected");
      String root = reader.getLocalName();
      if (root.equals("request"))
      {
	Request message = new Request();
	//readRequest(message);
	reader.close();
	return message;
      }
      else
	throw new IOException("A request start element is expected");
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
  }

  protected DataResponse readDataMessage(DataResponse data) 
  throws XMLStreamException
  {
      while (true)
      {
	int codeType = reader.nextTag();
	if (codeType == reader.START_ELEMENT)
	  readSignal(data);
	else if (codeType == reader.END_ELEMENT)
	  return data;
	else
	  assert false;
      }
  }

  protected void readSignal(DataResponse data)
  throws XMLStreamException
  {
    try
    {
      String signalType = reader.getLocalName();
      if (signalType.equals("invalid"))
      {
        int attributes = reader.getAttributeCount();
        String id = null;
        Date timestamp = null;
        for (int i=0; i<attributes; i++)
        {
          String attrName = reader.getAttributeLocalName(i);
	  String attrValue = reader.getAttributeValue(i);
	  if (attrName.equals("id"))
	    id = attrValue;
	  else if (attrName.equals("timestamp"))
	    timestamp = (new java.text.SimpleDateFormat(
		"yyyy-MM-dd'T'HH:mm:ssZ")).parse(attrValue);
	  else
	    throw new XMLStreamException("Usupported attribute: " + attrName);
        }
        Signal signal = Signal.getSignal(timestamp);
        data.put(id, signal);
      }
      else if (signalType.equals("signal"))
      {
      }
      else
        throw new XMLStreamException(
	    "invalid or signal start element is expected");
    }
    catch (java.text.ParseException e)
    {
      throw new XMLStreamException("Timestamp is not in appropriate format");
    }
  }

  public void close() throws IOException
  {
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
