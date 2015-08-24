package control4j.resources;

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

import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.ConfigItemNotFoundException;
import control4j.ConfigItemTypeException;
import control4j.Resource;
import control4j.protocols.tcp.RobustSocket;
import control4j.tools.IResponseCrate;

import cz.lidinsky.tools.reflect.Setter;
import cz.lidinsky.tools.reflect.Getter;

import org.apache.commons.collections4.Transformer;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Socket extends Resource {

  @Setter("host")
  public String host;

  @Setter("port")
  public int port;

  @Setter("timeout")
  public int timeout;

  RobustSocket socket;

  @Override
  public void prepare() {
    socket = new RobustSocket(host, port, timeout);
    //socket.start();
  }

  @Override
  public boolean isEquivalent(control4j.application.Resource definition) {
    try {
      Class _class = Class.forName(definition.getClassName());
      if (!this.getClass().isAssignableFrom(_class)) {
        return false;
      }
    } catch (ClassNotFoundException e) {
      // TODO:
      return false;
    }
    IConfigBuffer configuration = definition.getConfiguration();
    try {
      if (host.equals(configuration.getString("host"))
          && port == configuration.getInteger("port")) {
        return true;
      } else {
        return false;
      }
    } catch (ConfigItemNotFoundException e) {
      return false;
    } catch (ConfigItemTypeException e) {
      return false;
    }
  }

  /*public IResponseCrate write(byte[] request,
      Transformer<InputStream, Object> reader)
      throws IOException {

    return socket.write(request, reader);
  }*/

  public InputStream getInputStream() {
    return socket.getInputStream();
  }

  public OutputStream getOutputStream() {
    return socket.getOutputStream();
  }

}
