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

package control4j.modules;

import static cz.lidinsky.tools.Validate.notBlank;
import static cz.lidinsky.tools.Validate.notNull;
import static control4j.tools.Logger.*;

import control4j.AResource;
import control4j.AVariableInput;
import control4j.ICycleEventListener;
import control4j.InputModule;
import control4j.Signal;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import control4j.protocols.signal.Message;
import control4j.protocols.signal.XmlInputStream;
import control4j.protocols.signal.XmlOutputStream;
import control4j.protocols.tcp.Respondent;
import control4j.protocols.tcp.Server;
import control4j.resources.communication.SignalServer;

import cz.lidinsky.tools.reflect.Setter;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *  Exports input signals through the given resource. This module is dedicated
 *  to provide data to another control4j instance, or to some external program
 *  or application.
 *
 *  <h3>Resources</h3>
 *  <table>
 *      <caption>Resources</caption>
 *      <tr>
 *          <td>server</td>
 *          <td>IServer</td>
 *          <td></td>
 *      </tr>
 *  </table>
 *
 *  <h3>IO</h3>
 *  <table>
 *      <caption>IO</caption>
 *      <tr>
 *          <td>Input</td>
 *          <td></td>
 *          <td>A signal which will be exported.</td>
 *      </tr>
 *  </table>
 *
 *  @see SignalServer
 */
@AVariableInput
public class IMExport extends InputModule implements ICycleEventListener {

  private int port = 51234;

  @Setter("port")
  public void setPort(int port) {
    this.port = port;
  }

  /** Names of the input signal that will be used as an identifier */
  protected String ids[];

  /**
   *  For identification purposes, the communicated signals are identified by
   *  the signal label. This method collects all of the labels into the ids
   *  field.
   */
  @Override
  public void initialize(control4j.application.Module declaration) {
    super.initialize(declaration);
    // get id's of the signals
    int size = declaration.getInput().size();
    ids = new String[size];
    for (int i=0; i<size; i++) {
      ids[i] = declaration.getInput().get(i).getSignal().getLabel();
    }
  }

  /**
   *  Sends input signals throught the given server resource.
   */
  @Override
  protected void put(Signal[] input, int inputLength) {
    for (int i = 0; i < inputLength; i++) {
      put(ids[i], input[i]);
    }
  }

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

}
