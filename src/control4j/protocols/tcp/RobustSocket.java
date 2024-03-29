package control4j.protocols.tcp;

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

import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

import control4j.tools.IResponseCrate;
import control4j.tools.Queue;
import control4j.tools.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Transformer;

/**
 *
 *  An easy to use wrapper to the Socket class which provides socket that is
 *  trying to make a connection evry time the read or write method is called
 *  and the communication has not been created yet.
 *
 */
public class RobustSocket implements java.io.Closeable {

  /** Reading response timeout in milliseconds. */
  protected int timeout = 3333;

  /** Host name of the remote server. */
  protected String host;

  /** A port on which the remote server listens. */
  protected int port;

  /** Indicates that the connection has been estabilished. */
  private volatile boolean connected = false;

  /** Identification of this client for logging purposes. */
  protected String identification;

  protected volatile boolean close = false;

  /**
   *  Initialize internal fields.
   *
   *  @param host
   *             host name of the remote server
   *
   *  @param port
   *             port number on which the remote server listens
   */
  public RobustSocket(String host, int port, int timeout) {
    this.host = host;
    this.port = port;
    //this.timeout = timeout;
    identification = "TCP/IP client; class: " + getClass().getName()
        + "; host: " + host + "; port: " + port;
    connect();
  }

  private boolean connecting;

  /**
   *  Close this socket.
   */
  public synchronized void close() {
    close = true;
    if (connecting) {
      notifyAll();
    }
    closeConnection();
  }

  private InputStream inputStream;
  private OutputStream outputStream;
  private boolean logFlag = true;

  /**
   *
   */
  private synchronized void connect() {

    close = false;
    try {
      connecting = true;

      while (!close && !connected) {

        // estabilish a connection
        try {
          fine("Going to estabilish connection...\n" + identification);
          InetAddress address = InetAddress.getByName(host);
          Socket socket = new Socket(address, port);
          socket.setSoTimeout(timeout);
          outputStream = socket.getOutputStream();
          inputStream = socket.getInputStream();
          logFlag = true;
          connected = true;
          fine("Connection has been estabilished\n" + identification);

        } catch (IOException e) {
          if (logFlag) {
            catched(identification, "run", e);
          }
          logFlag = false;
          // close all resources
          closeConnection();
          // wait for a while
          try {
            wait(1000l);
          } catch (InterruptedException ie) { }
        }
      }

    } finally {
      connecting = false;
    }
  }

  /**
   *  Close all of the opened connections.
   */
  private synchronized void closeConnection() {
    connected = false;
    fine("Going to close connections ...\n" + identification);
    Tools.close(inputStream, getClass().getName(), "closeConnection");
    inputStream = null;
    Tools.close(outputStream, getClass().getName(), "closeConnection");
    outputStream = null;
  }

  private class RobustInputStream extends InputStream {

    @Override
    public int read() throws IOException {
      synchronized (RobustSocket.this) {
        connect();
        if (connected) {
          try {
            return inputStream.read();
          } catch (IOException e) {
            closeConnection();
            throw e;
          }
        } else {
          throw new IOException("Not connected!");
        }
      }
    }

  }

  private RobustInputStream robustInputStream = new RobustInputStream();

  public InputStream getInputStream() {
    return robustInputStream;
  }

  private class RobustOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {
      synchronized (RobustSocket.this) {
        connect();
        if (connected) {
          try {
            outputStream.write(b);
          } catch (IOException e) {
            closeConnection();
            throw e;
          }
        } else {
          throw new IOException("Not connected!");
        }
      }
    }

    @Override
    public synchronized void flush() throws IOException {
      if (connected) {
        outputStream.flush();
      }
    }

  }

  private RobustOutputStream robustOutputStream = new RobustOutputStream();

  public OutputStream getOutputStream() {
    return robustOutputStream;
  }

}
