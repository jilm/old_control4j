package control4j.resources.communication;

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

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.Signal;
import control4j.protocols.tcp.IClientFactory;
import control4j.protocols.tcp.Respondent;
import control4j.protocols.tcp.Server;
import control4j.protocols.tcp.SignalInputStream;
import control4j.protocols.tcp.SignalOutputStream;
import control4j.resources.Resource;
import control4j.tools.InterchangePoint;
import static control4j.tools.Logger.*;

/**
 *
 *  Encapsulate a communication server which can provide signals as a native
 *  java objects.
 *
 *  @see control4j.protocols.tcp.Server
 *
 */
public class SignalServer extends Resource 
implements IClientFactory, ICycleEventListener
{

  /**
   *  Port on which this server listens. This configuration item is
   *  optional, defalt value is 51234.
   */     
  @ConfigItem(optional=true)
  public int port = 51234;

  private Server server;
  private LinkedList<Respondent> clients = new LinkedList<Respondent>();
  private LinkedList<Respondent> garbage = new LinkedList<Respondent>();

  /**
   *  Create and run a server thread.
   *
   *  @see control4j.protocols.tcp.Server
   */
  @Override
  public void prepare()
  {
    server = new Server(port, this);
    server.start();
  }

  /**
   *  Give new data to clients to send them.
   *
   *  @param signals
   *             a data to send
   *
   *  @param size
   *             how many elements in the signals array should be sent
   */
  public void send(Signal[] signals, int size)
  {
    synchronized(clients)
    {
      for (Respondent<Signal[], Signal[]> client : clients)
      {
	try
	{
          if (client.read() != null)
          {
            Signal[] buffer = new Signal[size];
            System.arraycopy(signals, 0, buffer, 0, size);
            client.write(buffer);
          }
        }
	catch (IOException e)
	{
	  garbage.add(client);
	}
      }
      for (Respondent<Signal[], Signal[]> client : garbage)
        clients.remove(client);
      if (garbage.size() > 0)
      {
        info("Number of listeners removed: " + garbage.size() 
            + ", total number of listeners: " + clients.size());
        garbage.clear();
      }
    }
  }

  /**
   *  Create new Respondent object and add it to the clients list.
   */
  public void newClient(Socket socket)
  {
    try
    {
      SignalInputStream inputStream 
          = new SignalInputStream(socket.getInputStream());
      SignalOutputStream outputStream
          = new SignalOutputStream(socket.getOutputStream());
      Respondent<Signal[], Signal[]> client 
          = new Respondent<Signal[], Signal[]>(inputStream, outputStream);
      synchronized(clients)
      {
        clients.add(client);
      }
      info("New listener added, total number of listeners: " + clients.size());
    }
    catch (IOException e)
    {
      catched(this.getClass().getName(), "newClient", e);
    }
  }

  public void cycleStart()
  {
  }

  public void processingStart()
  {
  }

  public void cycleEnd()
  {
  }

}
