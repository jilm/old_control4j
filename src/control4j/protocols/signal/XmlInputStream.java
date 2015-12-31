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
import static cz.lidinsky.tools.Validate.notBlank;
import static cz.lidinsky.tools.Validate.notNull;

import control4j.Signal;
import control4j.protocols.tcp.IInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *  Reads and returns signals from underlying input stream in the XML format.
 *
 *  Two types of message are expected.
 *
 *  <p>Request:
 *  {@code <request />}
 *
 *  <p>Response:
 *  {@code
 *  <set>
 *    <invalid id="sig1" timestamp="..." />
 *    <signal id="sig2" timestamp="..." value="15.47" />
 *  </set>
 *  }
 */
public class XmlInputStream extends InputStream
implements IInputStream<Message> {

  private final InputStream stream;
  protected XMLStreamReader reader;
  private final XMLInputFactory factory;

  /**
   *  @param stream
   *             a stream, the message will be reade from
   */
  public XmlInputStream(InputStream stream) {
    this.stream = notNull(stream);
    factory = XMLInputFactory.newFactory();
  }

  /**
   *  Reads and returns a character from the given input stream.
   */
  @Override
  public int read() throws IOException {
    int b = stream.read();
    System.out.print((char)b);
    return b;
  }

  /**
   *  Reads and reaturns a message from the given input stream. This method
   *  blocks until the whole message is read and available.
   */
  @Override
  public Message readMessage() throws IOException {
    try {
      //System.out.println("Read message");
      if (reader == null) {
        reader = factory.createXMLStreamReader(stream);
      }
      // find root element
      finest("Waiting for the first event...");
      int eventType = nextTag();
      finest("The first event is: " + Integer.toString(eventType));
      if (eventType != XMLStreamReader.START_ELEMENT) {
        throw new IOException("Start element expected");
      }
      String root = reader.getLocalName();
      if (root.equals("set")) {
        finest("Response detected.");
        readDataMessage();
        return new DataResponse(signalBuffer);
      } else if (root.equals("request")) {
        finest("Request detected.");
        readRequest();
        return new DataRequest();
      } else {
        throw new IOException("A set or request start element is expected");
      }
    } catch (XMLStreamException e) {
      throw new IOException(e);
    } finally {
        reader = null;
    }
  }

  private int next() throws XMLStreamException {
    int code = reader.next();
    //System.out.print("xxxx");
    System.out.print("...");
    System.out.print(code);
    System.out.print("...");
    return code;
  }

  private int nextTag() throws XMLStreamException {
    int code = next();
    while (code != XMLStreamReader.START_ELEMENT
        && code != XMLStreamReader.END_ELEMENT) {
      code = next();
    }
    return code;
  }

  private Map<String, Signal> signalBuffer;

  /**
   *  Reads the signal message. The result is stored in the signal buffer map.
   */
  private void readDataMessage()
    throws XMLStreamException {
      if (reader == null) {
        reader = factory.createXMLStreamReader(this);
      }
      signalBuffer = new HashMap<>();
      while (true) {
        int codeType = nextTag();
        //printCode();
        if (codeType == reader.START_ELEMENT) {
          readSignal();
        } else if (codeType == reader.END_ELEMENT) {
          break;
        } else {
          assert false;
        }
      }
      findEndDocument();
    }

  /**
   *  Reads and returns one signal from the xml input stream.
   */
  private void readSignal() throws XMLStreamException {
    String signalType = reader.getLocalName();
    // read invalid signal
    if (signalType.equals("invalid")) {
      readSignalAttributes();
      Signal signal = Signal.getSignal(notNull(timestamp));
      signalBuffer.put(notBlank(id), signal);
      // read valid signal
    } else if (signalType.equals("signal")) {
      readSignalAttributes();
      Signal signal = Signal.getSignal(value, notNull(timestamp));
      signalBuffer.put(notBlank(id), signal);
      // unsupported element
    } else {
      throw new XMLStreamException(
          "invalid or signal start element is expected");
    }
    nextTag();
  }

  // signal attributes
  private String id;
  private Date timestamp;
  private double value;
  private String unit;

  /**
   *  Reads attributes that belong to the signal element from the xml reader
   *  and stores them in the fields: id, timestamp, value.
   *
   *  @throws XMLStreamException
   *              if the element contains unsupported attribute
   */
  private void readSignalAttributes() throws XMLStreamException {

    id = null;
    timestamp = null;
    unit = null;
    value = Double.NaN;

    try {
      int attributes = reader.getAttributeCount();
      for (int i=0; i<attributes; i++) {
        String attrName = reader.getAttributeLocalName(i);
        String attrValue = reader.getAttributeValue(i);
        if (attrName.equals("id")) {
          id = attrValue;
        } else if (attrName.equals("timestamp")) {
          timestamp = XmlTools.parseDate(attrValue);
        } else if (attrName.equals("value")) {
          value = Double.parseDouble(attrValue);
        } else if (attrName.equals("unit")) {
          unit = attrValue;
        } else {
          throw new XMLStreamException("Usupported attribute: " + attrName);
        }
      }
    } catch (java.text.ParseException e) {
      throw new XMLStreamException("Timestamp is not in appropriate format");
    }
  }

  public void readRequest() throws IOException {
    try {
      if (reader == null) {
        reader = factory.createXMLStreamReader(this);
      }
      int codeType = nextTag();
      if (codeType != reader.END_ELEMENT) {
        throw new IOException("End element expected!");
      }
      findEndDocument();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }

  private void findEndDocument() throws XMLStreamException {
    //while (reader.hasNext()) {
      //int codeType = next();
    //}
    //reader.close();
    //reader = null;
    System.out.print("...end document...");
  }

  public void close() throws IOException { }

  /**
   *  For debug purposes only.
   */
  private void printCode() {
    int code = reader.getEventType();
    javax.xml.stream.Location location = reader.getLocation();
    String message;
    message = "[" + location.getLineNumber() + ", "
      + location.getColumnNumber() + "]";
    if (code == reader.START_ELEMENT || code == reader.END_ELEMENT) {
      message += " " + code + ": " + reader.getName();
    } else {
      message += " " + code;
    }
    finest(message);
  }

}
