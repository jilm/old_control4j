package control4j.modules;

/*
 *  Copyright 2013 Jiri Lidinsky
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

import control4j.Module;
import control4j.Signal;
import control4j.ConfigItem;
import control4j.InputModule;
import control4j.IConfigBuffer;
import control4j.tools.InterchangePoint;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import static control4j.tools.Logger.*;

/**
 *  Sends input data to the client over the TCP protocol.
 */ 
public class IMNetOutput extends InputModule
{
  /**
   *  Port on which this server listens. This configuration item is
   *  optional, defalt value is 51234.
   */     
  @ConfigItem(optional=true)
  public int port = 51234;
  
  private Server server;
  private LinkedList<Client> clients = new LinkedList<Client>();
  private LinkedList<Client> garbage = new LinkedList<Client>();

  @Override protected void initialize(IConfigBuffer configuration)
  {
    server = new Server();
    server.start();
  }
  
  /**
   *  Sends input data to a client over the TCP protocol.
   *  
   *  @param input 
   *             data so send
   */         
  @Override
  protected void put(Signal[] input)
  {
    synchronized(clients)
    {
      for (Client client : clients)
      {
        if (client.isActive())
	{
	  int size = getNumberOfAssignedInputs();
	  Signal[] buffer = new Signal[size];
	  System.arraycopy(input, 0, buffer, 0, size);
          client.send(buffer);
	}
        else
	  garbage.add(client);
      }
      for (Client client : garbage)
      {
        clients.remove(client);
      }
      if (garbage.size() > 0)
      {
        info("Number of listeners removed: " + garbage.size() + ", total number of listeners: " + clients.size());
        garbage.clear();
      }
    }
  }
  
  class Server extends Thread
  {

    Server()
    {
      super("IMNetOutput:Server");
    }
  
    @Override public void run()
    {
      while (true)
      {
	try
	{
          ServerSocket serverSocket = new ServerSocket(port);
	  while (true)
	  {
	    try
	    {
	      Socket socket = serverSocket.accept();
	      Client client = new Client(socket);
	      client.start();
	      synchronized(clients)
	      {
	        clients.add(client);
	      }
              info("New listener added, total number of listeners: " + clients.size());
	    }
	    catch (IOException ioe)
	    {
	      catched("IMNetOutput$Server", "run", ioe);
	    }
	  }
	}
	catch (IOException e)
	{
	  catched("IMNetOutput$Server", "run", e);
	  long waitTime = Math.round(Math.random() * 4000.0 + 1000.0);
	  try { Thread.sleep(waitTime); } catch (InterruptedException ie) {}
	}
      }
    }
  }
  
  /**
   *  Represent one data reader.
   */     
  class Client extends Thread
  {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private InterchangePoint<Signal[]> dataToSend = new InterchangePoint<Signal[]>();
    private boolean isActive = false;
    
    Client(Socket socket) throws IOException
    {
      super("IMNetOutput:Client");
      this.socket = socket;
      InputStream is = null;
      OutputStream os = null;
      try
      {
	os = socket.getOutputStream();
	outputStream = new ObjectOutputStream(os);
        is = socket.getInputStream();
	inputStream = new ObjectInputStream(is);
	isActive = true;
      }
      catch (IOException e)
      {
        if (inputStream != null)
	  try { inputStream.close(); } catch (IOException ioe) {}
        if (outputStream != null)
	  try { outputStream.close(); } catch (IOException ioe) {}
        if (is != null)
	  try { is.close(); } catch (IOException ioe) {}
        if (os != null)
	  try { os.close(); } catch (IOException ioe) {}
        try { this.socket.close(); } catch (IOException ioe) {}
	throw e;
      }
    }
    
    void send(Signal[] signals)
    {
      dataToSend.forcedSet(signals);
    }
    
    @Override public void run()
    {
      try
      {
        while(true)
        {
	  try
	  {
            Object request = inputStream.readObject();
	  }
	  catch (ClassNotFoundException cnfe) {}
          Signal[] data = dataToSend.get();
          outputStream.writeObject(data);
        }
      }
      catch (EOFException e)
      {
      }
      catch (IOException e)
      {
        catched("IMNetOutput$Client", "run", e);
      }
      finally
      {
        try { inputStream.close(); } catch (IOException ioe) {}
	try { outputStream.close(); } catch (IOException ioe) {}
	try { socket.close(); } catch (IOException ioe) {}
	synchronized(this)
	{
	  isActive = false;
	}
      }
    }

    synchronized boolean isActive()
    {
      return isActive;
    }
    
  }
  
}
