/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

package control4j.resources.communication;

import static control4j.tools.Logger.*;
import static cz.lidinsky.tools.Validate.notNull;
import static cz.lidinsky.tools.Validate.notBlank;

//import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.Resource;
import control4j.Signal;
import control4j.protocols.IMessage;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import control4j.protocols.signal.Message;
import control4j.protocols.signal.XmlInputStream;
import control4j.protocols.signal.XmlOutputStream;
import control4j.protocols.tcp.IClientFactory;
import control4j.protocols.tcp.Respondent;
import control4j.protocols.tcp.Server;
import control4j.resources.IServer;
import control4j.tools.InterchangePoint;
import control4j.tools.Tools;

import cz.lidinsky.tools.reflect.Setter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.stream.XMLStreamException;

/**
 *  Encapsulate a communication server which is dedicated to exchange signal
 *  objects between different control4j instances. It opens a socket server
 *  that listens on the given port. New client object is created for each new
 *  incoming connection request.
 *
 *  <p>Signal values are collected in the internal buffer during the actual
 *  scan. At the end of the scan, the response object is created, which will be
 *  sent to clients. Simoultaneously the internal buffer is cleand.
 *
 *  @see control4j.protocols.tcp.Server
 *  @see control4j.modules.IMExport
 *  @see control4j.modules.OMImport
 *
 */
public class SignalServer extends Resource
implements IClientFactory, ICycleEventListener {

  /**
   *  Port on which this server listens. This configuration item is optional,
   *  defalt value is 51234.
   */     
  @Setter("port")
    public int port = 51234;

  private Server server;

  /** Buffer to store signals. */
  private final Map<String, Signal> signalBuffer = new HashMap<>();

  /** A response which is created at the end of the scan. */
  private DataResponse response = new DataResponse();

  /**
   *  Create and run a server thread.
   *
   *  @see control4j.protocols.tcp.Server
   */
  @Override
    public void prepare() {
      server = new Server(port, this::newClient);
      server.start();
    }

  /**
   *  Given signals are stored into the internal buffer to be sent.
   */
  public synchronized void put(String id, Signal signal) {
    signalBuffer.put(notBlank(id), notNull(signal));
  }

  /**
   *  Create new Respondent object which is responsible for taking care of the
   *  client needs.
   */
  @Override
  public synchronized void newClient(Socket socket) {
    try {
      Respondent<Message, Message> client = new Respondent<Message, Message>(
          new XmlInputStream(socket.getInputStream()),
          new XmlOutputStream(socket.getOutputStream()),
          this::getResponse);
      info("New listener added.");
      client.initialize();
    } catch (IOException e) {
      catched(this.getClass().getName(), "newClient", e);
    }
  }

  /**
   *  Returns a response to the given request. This method should be used by
   *  the clients.
   */
  public synchronized DataResponse getResponse(Message request) {
    return response;
  }

  /**
   *  Not used.
   */
  public void cycleStart() { }

  /**
   *  Not used.
   */
  public void processingStart() { }

  /**
   *  Prepare new response.
   */
  public void cycleEnd() {
    response = new DataResponse(signalBuffer);
    signalBuffer.clear();
  }

  @Override
  public boolean isEquivalent(control4j.application.Resource definition) {
    try {
      if (definition.containsKey("port")) {
        int port = Integer.parseInt(definition.getValue("port"));
        return port == this.port; // TODO: check class
      }
    } catch (Exception e) { }
    return false;
  }

}
