package control4j.protocols.tcp;

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

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.EOFException;

import static control4j.tools.Logger.*;

/**
 *
 *  Listen at the specified port, and creates a client for each new request
 *  for connection. The server runs as a separate thread and repeatedly 
 *  tries to recreate server socket whenever something goes wrong. The 
 *  thead must be started from outside.
 *
 *  @see IClientFactory
 *
 */ 
public class Server implements Runnable, java.io.Closeable
{

  /** The server has not been started yet, or was already stopped. */
  public static final int STATUS_STOP = 0;

  /** The server has been started, but the connection has not been
      estabilished yet. */
  public static final int STATUS_RUNNING = 1;

  /** The server socket was created and the server accepts incoming
      requests for connection. */
  public static final int STATUS_CONNECTED = 2;

  /** Some error occurs, the server is trying to restore the connection */
  public static final int STATUS_ERROR = 3;

  /** Port to listen at for new connection requests. */
  private int port = 51234;
  
  /** A factory object that creates new client which will serve each new 
      request for connection. */
  private IClientFactory clientFactory;

  /** Status of the server. */
  private volatile int status = STATUS_STOP;

  /** A request to stop the server. */
  private volatile boolean stop = false;

  /** An identification of the server mainly for debug purposes. */
  protected String identification;

  /**
   *  Initialization.
   *
   *  @param port
   *             a port to listen at
   *
   *  @param clientFactory
   *             a class that gives an appropriate client which can
   *             serve the new connection request
   */
  public Server(int port, IClientFactory clientFactory)
  {
    super();
    this.port = port;
    this.clientFactory = clientFactory;
    identification = "Server, class: " + getClass().getName() 
	+ ", port: " + port;
  }

  /**
   *  Starts the server.
   */
  public void start()
  {
    new Thread(this, identification).start();
  }

  /**
   *  Runs an infinite loop in which it listens on the specified port
   *  and creates a new client each time it receives a new connection
   *  request. If attempt to create server socket fails, it will try
   *  to recreate it again and again ... To create new client for
   *  incoming connection, it calls <code>newClient</code> method
   *  of the given <code>IClientFactory</code>.
   *
   *  <p>This method must be run as a separate thread from outside
   *  this object.
   */
  public void run()
  {
    ServerSocket serverSocket = null;
    while (!stop)
    {
      status = STATUS_RUNNING;
      try
      {
        serverSocket = new ServerSocket(port);
        info("A new ServerSocket was successfuly created\n" + identification);
	status = STATUS_CONNECTED;
        while (!stop)
        {
          try
          {
            Socket socket = serverSocket.accept();
            clientFactory.newClient(socket);
          }
          catch (IOException ioe)
          {
	    status = STATUS_ERROR;
            catched(getClass().getName(), "run", ioe);
	    warning("An exception while waiting for new connection " +
		"going to recreate server socket ...\n" + identification);
          }
        }
      }
      catch (IOException e)
      {
	status = STATUS_ERROR;
        catched(getClass().getName(), "run", e);
	warning("An attempt to create server socket failed!");
      }
      finally
      {
	if (serverSocket != null)
          try { serverSocket.close(); } catch (IOException ioex) { }
	serverSocket = null;
	if (!stop)
	{
          long waitTime = Math.round(Math.random() * 4000.0 + 1000.0);
          try { Thread.sleep(waitTime); } catch (InterruptedException ie) {}
	}
      }
    }
    status = STATUS_STOP;
  }

  /**
   *  Returns a stutus of the server.
   */
  public int getStatus()
  {
    return status;
  }

  /**
   *  Will close the server socket and stop the server thread.
   */
  public void close()
  {
    stop = true;
  }

}
