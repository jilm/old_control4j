package control4j.protocols.tcp;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
 *  Listen on the specified port, and creates a client for each new request.
 *  The server runs as a separate thread and repeatedly tries to recreate
 *  server socket whenever something goes wrong. The thead must be started
 *  from outside.
 *
 */ 
public class Server extends Thread
{

  private int port = 51234;
  
  private IClientFactory clientFactory;

  /**
   *  @param port
   *             a port to listen to
   *
   *  @param clientFactory
   *             a class that gives an appropriate client which can
   *             serve the new request for connection
   */
  public Server(int port, IClientFactory clientFactory)
  {
    super("Server");
    this.port = port;
    this.clientFactory = clientFactory;
  }

  @Override 
  public void run()
  {
    while (true)
    {
      try
      {
        ServerSocket serverSocket = new ServerSocket(port);
        fine("A new ServesSocket successfuly created");
        while (true)
        {
          try
          {
            Socket socket = serverSocket.accept();
            clientFactory.newClient(socket);
          }
          catch (IOException ioe)
          {
            catched(getClass().getName(), "run", ioe);
          }
        }
      }
      catch (IOException e)
      {
        catched(getClass().getName(), "run", e);
        long waitTime = Math.round(Math.random() * 4000.0 + 1000.0);
        try { Thread.sleep(waitTime); } catch (InterruptedException ie) {}
      }
    }
  }

}
