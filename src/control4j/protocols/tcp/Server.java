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
public class Server extends Thread
{

  /** Port to listen at for new connection requests. */
  private int port = 51234;
  
  /** A factory object that creates new client which will serve each new 
      request for connection. */
  private IClientFactory clientFactory;

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
    super("Server");
    this.port = port;
    this.clientFactory = clientFactory;
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
  @Override 
  public void run()
  {
    while (true)
    {
      try
      {
        ServerSocket serverSocket = new ServerSocket(port);
        info("A new ServerSocket was successfuly created");
	boolean error = false;
        while (!error)
        {
          try
          {
            Socket socket = serverSocket.accept();
            clientFactory.newClient(socket);
          }
          catch (IOException ioe)
          {
            catched(getClass().getName(), "run", ioe);
	    warning("An exception while waiting for new connection " +
		"going to recreate server socket ...");
	    error = true;
            try { serverSocket.close(); } catch (IOException ioex) { }
          }
        }
      }
      catch (IOException e)
      {
        catched(getClass().getName(), "run", e);
	warning("An attempt to create server socket failed!");
        long waitTime = Math.round(Math.random() * 4000.0 + 1000.0);
        try { Thread.sleep(waitTime); } catch (InterruptedException ie) {}
      }
    }
  }

}
