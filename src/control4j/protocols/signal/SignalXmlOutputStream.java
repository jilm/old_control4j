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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import control4j.Signal;

public class SignalXmlOutputStream
implements control4j.protocols.tcp.IOutputStream<Message>
{

  protected XMLStreamWriter writer;

  public SignalXmlOutputStream(OutputStream stream) throws XMLStreamException
  {
    XMLOutputFactory factory = XMLOutputFactory.newFactory();
    writer = factory.createXMLStreamWriter(stream);
  }

  public void write(String id, Signal signal) throws java.io.IOException
  {
    try
    {
      Date timestamp = signal.getTimestamp();
      String strTimestamp = String.format("%1$TFT%1$TT%1$Tz", timestamp);
      if (!signal.isValid())
      {
        writer.writeEmptyElement("invalid");
        writer.writeAttribute("id", id);
        writer.writeAttribute("timestamp", strTimestamp);
      }
      else if (signal.getSize() == 1)
      {
        writer.writeStartElement("signal");
        writer.writeAttribute("id", id);
        writer.writeAttribute("timestamp", strTimestamp);
        String strValue = String.format("%g", signal.getValue());
        writer.writeAttribute("value", strValue);
        String unit = signal.getUnit();
        if (unit != null && unit.length() > 0)
          writer.writeAttribute("unit", unit);
        writer.writeEndElement();
      }
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
  }

  public void write(Message message) throws java.io.IOException
  {
    if (message instanceof Request)
    {
    }
    else if (message instanceof DataMessage)
    {
    }
    else
    {
      assert false;
    }
  }

  public void write(Request request) throws java.io.IOException
  {
    try
    {
      writer.writeStartDocument();
      if (request.size() > 0)
      {
        // build the list of ids
        StringBuilder idList = new StringBuilder();
        String delimiter = "";
        for (String id : request)
        {
          idList.append(delimiter)
	        .append(id);
	  delimiter = ",";
        }
        // write request
        writer.writeStartElement("request");
	writer.writeDefaultNamespace(Message.XMLNS);
        writer.writeCharacters(idList.toString());
        writer.writeEndElement();
      }
      else
      {
        writer.writeEmptyElement("request");
	writer.writeDefaultNamespace(Message.XMLNS);
      }
      writer.writeEndDocument();
      writer.flush();
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
  }

  public void write(DataMessage data) throws java.io.IOException
  {
    try
    {
      writer.writeStartDocument();
      writer.writeStartElement("set");
      writer.writeDefaultNamespace(Message.XMLNS);
      Set<String> ids = data.getIdSet();
      for (String id : ids)
	write(id, data.get(id));
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.flush();
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
  }

  public void close() throws java.io.IOException
  {
  }

}
