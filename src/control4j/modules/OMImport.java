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
import static control4j.tools.Logger.*;

import control4j.AResource;
import control4j.AVariableOutput;
import control4j.OutputModule;
import control4j.Signal;
import control4j.application.Module;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
//import control4j.resources.communication.SignalClient;
import control4j.ICycleEventListener;
import control4j.protocols.signal.XmlInputStream;
import control4j.protocols.signal.XmlOutputStream;
import control4j.tools.Tools;

import cz.lidinsky.tools.reflect.Setter;

import java.util.Collection;
import java.net.Socket;
import java.io.IOException;

/**
 *
 *
 */
@AVariableOutput
public class OMImport extends OutputModule implements ICycleEventListener {

  private String host;
  private int port;

  @Setter("host")
  public void setHost(String host) {
    this.host = notBlank(host);
    if (connected) {
      close();
    }
  }

  @Setter("port")
  public void setPort(int port) {
    this.port = port;
    if (connected) {
      close();
    }
  }

  /** Names of the input signal that will be used as an identifier */
  protected String ids[];

  /**
   *  The module needs to know the names of the input signals
   *  to use them as ids inside the exprot message.
   */
  @Override
  public void initialize(control4j.application.Module declaration) {
    super.initialize(declaration);
    int size = declaration.getOutput().size();
    ids = new String[size];
    for (int i=0; i<size; i++) {
      ids[i] = declaration.getOutput().get(i).getSignal().getLabel();
    }
  }

  @Override
  public void prepare() {
    connect();
  }

  /**
   *  Sends input signals throught the given server resource.
   */
  @Override
  public void get(Signal[] output, int outputLength) {
    if (response != null) {
      for (int i=0; i<outputLength; i++) {
        output[i] = response.getData().get(ids[i]);
        if (output[i] == null) {
          output[i] = Signal.getSignal();
        }
      }
      response = null;
    }
  }

  @Override
  public void cycleStart() {
    try {
      connect();
      sendRequestReceiveResponse();
    } catch (Exception e) {
      catched(getClass().getName(), "cycleStart", e);
    }
  }

  @Override
  public void cycleEnd() {}

  @Override
  public void processingStart() {}

  protected void sendRequestReceiveResponse() {
    connect();
    if (!receving && response == null) {
      new Thread(this::run).start();
    }
  }

  private DataRequest request = new DataRequest();
  private DataResponse response;
  private boolean receving = false;

  protected void run() {
    try {
      finest("Going to write data request...");
      outputStream.write(request);
      finest("Waiting for a response...");
      response = (DataResponse)inputStream.readMessage();
      finest("Response recived.");
    } catch (Exception e) {
      catched(getClass().getName(), "run", e);
      reconnect();
    } finally {
      receving = false;
    }
  }

  private boolean connected = false;
  private Socket socket;
  private XmlInputStream inputStream;
  private XmlOutputStream outputStream;

  protected void connect() {
    try {
      if (!connected) {
        finer("Going to connect to the server, host: " + host + ", port: " + Integer.toString(port));
        socket = new Socket(host, port);
        finer("Socket was created, going to create input and output streams ...");
        inputStream = new XmlInputStream(socket.getInputStream());
        outputStream = new XmlOutputStream(socket.getOutputStream());
        connected = true;
        finer("Clinet has been successfuly connected to the server.");
      }
    } catch (IOException e) {
      catched(getClass().getName(), "connect", e);
      close();
    }
  }

  protected void reconnect() {
    close();
    connect();
  }

  public void close() {
    connected = false;
    Tools.close(socket);
    Tools.close(inputStream);
    Tools.close(outputStream);
    socket = null;
    inputStream = null;
    outputStream = null;
  }

}
