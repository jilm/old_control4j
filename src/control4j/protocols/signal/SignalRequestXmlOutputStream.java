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

import control4j.protocols.IMessage;

import control4j.Signal;

public class SignalRequestXmlOutputStream
implements control4j.protocols.tcp.IOutputStream<Request>
{

  protected XMLStreamWriter writer;

  public SignalRequestXmlOutputStream(OutputStream stream) 
  throws XMLStreamException
  {
    XMLOutputFactory factory = XMLOutputFactory.newFactory();
    writer = factory.createXMLStreamWriter(stream);
  }

  public void write(Request message) throws java.io.IOException
  {
    try
    {
      if (message instanceof DataRequest)
      {
	write((DataRequest) message);
      }
      else
	throw new IOException("Not supported message type");
    }
    catch (XMLStreamException e)
    {
      throw new IOException(e);
    }
  }

  protected void write(DataRequest request) throws XMLStreamException
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

  public void close() throws java.io.IOException
  {
  }

}
