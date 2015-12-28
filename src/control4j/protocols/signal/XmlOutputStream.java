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

package control4j.protocols.signal;

import static control4j.tools.Logger.*;

import control4j.Signal;
import control4j.protocols.tcp.IOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XmlOutputStream extends OutputStream
implements IOutputStream<Message> {

  private final OutputStream stream;
  protected XMLStreamWriter writer;

  public XmlOutputStream(OutputStream stream) {
    super();
    this.stream = stream;
  }

  @Override
  public void write(int b) throws IOException {
    try {
    System.out.print(new String(new int[] {b}, 0, 1));
    } catch (Exception e) {}
    stream.write(b);
  }

  private XMLStreamWriter getXMLWriter() throws XMLStreamException {
    if (writer == null) {
      XMLOutputFactory factory = XMLOutputFactory.newFactory();
      writer = factory.createXMLStreamWriter(this);
    }
    return writer;
  }

  @Override
    public void write(Message message) {
      try {
        getXMLWriter();
        if (message instanceof DataRequest) {
          write((DataRequest)message);
        } else if (message instanceof DataResponse) {
          write((DataResponse)message);
        } else {
          assert true;
        }
        writer.flush();
        stream.flush();
      } catch (Exception e) {
        catched(this.getClass().getName(), "write", e); // TODO:
      }
    }

  public void write(String id, Signal signal) throws XMLStreamException {
    getXMLWriter();
    Date timestamp = signal.getTimestamp();
    String strTimestamp = XmlTools.formatDate(timestamp);
    if (!signal.isValid()) {
      writer.writeEmptyElement("invalid");
      writer.writeAttribute("id", id);
      writer.writeAttribute("timestamp", strTimestamp);
    } else if (signal.getSize() == 1) {
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

  public void write(DataResponse data)
    throws XMLStreamException, IOException {
    getXMLWriter();
    writer.writeStartDocument();
    writer.writeStartElement("set");
    writer.writeDefaultNamespace(Message.XMLNS);
    Set<String> ids = data.getData().keySet();
    for (String id : ids)
      write(id, data.getData().get(id));
    writer.writeEndElement();
    writer.writeEndDocument();
    writer.flush();
    stream.flush();
  }

  public void close() throws java.io.IOException { }

  protected void write(DataRequest request)
    throws XMLStreamException, IOException {
    getXMLWriter();
    writer.writeStartDocument();
    if (request.getIds().size() > 0) {
      // build the list of ids
      StringBuilder idList = new StringBuilder();
      String delimiter = "";
      for (String id : request.getIds()) {
        idList.append(delimiter)
          .append(id);
        delimiter = ",";
      }
      // write request
      writer.writeStartElement("request");
      writer.writeDefaultNamespace(Message.XMLNS);
      writer.writeCharacters(idList.toString());
      writer.writeEndElement();
    } else {
      writer.writeEmptyElement("request");
      writer.writeDefaultNamespace(Message.XMLNS);
    }
    writer.writeEndDocument();
    writer.flush();
    stream.flush();
    stream.close();
    finest("Request written...");
  }

}
