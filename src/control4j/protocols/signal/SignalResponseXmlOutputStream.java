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

public class SignalResponseXmlOutputStream
implements control4j.protocols.tcp.IOutputStream<Response>
{

  protected XMLStreamWriter writer;

  public SignalResponseXmlOutputStream(OutputStream stream)
  throws XMLStreamException
  {
    XMLOutputFactory factory = XMLOutputFactory.newFactory();
    writer = factory.createXMLStreamWriter(stream);
  }

  public void write(String id, Signal signal) throws XMLStreamException
  {
      Date timestamp = signal.getTimestamp();
      String strTimestamp = XmlTools.formatDate(timestamp);
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

  public void write(DataResponse data) throws XMLStreamException
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

  public void write(Response data) throws java.io.IOException
  {
    try
    {
      if (data instanceof DataResponse)
	write((DataResponse)data);
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
