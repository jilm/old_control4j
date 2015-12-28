/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.Resource;
import control4j.Signal;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.DataResponse;
import control4j.protocols.signal.Message;
import control4j.protocols.signal.XmlInputStream;
import control4j.protocols.signal.XmlOutputStream;
import control4j.protocols.tcp.IInputStream;
import control4j.protocols.tcp.IOutputStream;
import control4j.protocols.tcp.RobustSocket;
import control4j.tools.IResponseCrate;

import cz.lidinsky.tools.reflect.Setter;

import java.io.IOException;

/**
 *  Dedicated for comunication with signal server. The request for data is
 *  sent et the end of the scan.
 */
public class SignalClient extends Resource implements ICycleEventListener {

  @Setter("host")
  public String host;

  @Setter("port")
  public int port = 51234;

  private IInputStream<Message> inputStream;
  private IOutputStream<Message> outputStream;

  private DataRequest request = new DataRequest();

  @Override 
  public void prepare() {
    RobustSocket socket = new RobustSocket(host, port, 3671);
    inputStream = new XmlInputStream(socket.getInputStream());
    outputStream = new XmlOutputStream(socket.getOutputStream());
  }

  public boolean isEquivalent(control4j.application.Resource declaration) {
    return false; // TODO:
  }

  public DataResponse read() {
    return response;
  }

  public void cycleStart() { }

  public void processingStart() { }

  private boolean active = false;
  private DataResponse response = null;

  public void cycleEnd() {
    response = null;
    if (!active) {
      new Thread(this::run).start();
    }
  }

  private void run() {
    try {
      active = true;
      outputStream.write(request);
      outputStream.write(request);
      finest("Request for new data has been sent.");
      response = (DataResponse)inputStream.readMessage();
      finest("Response received.");
    } catch (Exception e) {
      catched(this.getClass().getName(), "run", e);
    } finally {
      active = false;
    }
  }

}
