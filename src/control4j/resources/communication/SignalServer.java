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
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.stream.XMLStreamException;

import control4j.ConfigItem;
import control4j.ICycleEventListener;
import control4j.Signal;
import control4j.protocols.IMessage;
import control4j.protocols.tcp.IClientFactory;
import control4j.protocols.tcp.Respondent;
import control4j.protocols.tcp.Server;
import control4j.protocols.signal.DataRequest;
import control4j.protocols.signal.Request;
import control4j.protocols.signal.Response;
import control4j.protocols.signal.SignalRequestXmlInputStream;
import control4j.protocols.signal.SignalResponseXmlOutputStream;
import control4j.resources.IServer;
import control4j.resources.Resource;
import control4j.tools.InterchangePoint;
import control4j.tools.Tools;
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
implements IClientFactory, ICycleEventListener, IServer<Request>
{

  /**
   *  Port on which this server listens. This configuration item is
   *  optional, defalt value is 51234.
   */     
  @ConfigItem(optional=true)
  public int port = 51234;

  private Server server;

  /** A list of active clients. */
  private LinkedList<Respondent<Request, Response>>
      clients = new LinkedList<Respondent<Request, Response>>();

  /** Temporary list of clients that are no longer active. */
  private LinkedList<Respondent<Request, Response>> 
      garbage = new LinkedList<Respondent<Request, Response>>();

  /** A buffer of received requests. */
  private LinkedList<Request> buffer = new LinkedList<Request>();

  private DataRequest defaultRequest;

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
   *  Returns all of the requests that were received before the 
   *  current control loop begins.
   *
   *  @return a collection of all the received requests
   */
  public Collection<Request> getRequests()
  {
    return buffer;
  }

  /**
   *  Create new Respondent object and add it to the clients list.
   */
  public void newClient(Socket socket)
  {
    try
    {
      SignalRequestXmlInputStream inputStream 
          = new SignalRequestXmlInputStream(socket.getInputStream());
      SignalResponseXmlOutputStream outputStream
          = new SignalResponseXmlOutputStream(socket.getOutputStream());
      Respondent<Request, Response> client 
          = new Respondent<Request, Response>(inputStream, outputStream);
      synchronized(clients)
      {
        clients.add(client);
      }
      info("New listener added, total number of listeners: " + clients.size());
      client.initialize();
    }
    catch (IOException e)
    {
      catched(this.getClass().getName(), "newClient", e);
    }
    catch (XMLStreamException e)
    {
      catched(this.getClass().getName(), "newClient", e);
    }
  }

  /**
   *  Not used.
   */
  public void cycleStart()
  {
  }

  /**
   *  Collect clients that have received request.
   */
  public void processingStart()
  {
    buffer.clear();
    defaultRequest = new DataRequest();
    buffer.add(defaultRequest);
  }

  /**
   *  Send response to each request.
   */
  public void cycleEnd()
  {
    synchronized(clients)
    {
    for (Respondent<Request, Response> client : clients)
      try
      {
	Request request = client.read();
	if (request != null)
	{
	  finest("Going to reply for the request ...");
	  Response response = defaultRequest.getResponse();
	  client.write(response);
	}
      }
      catch (java.io.IOException e)
      {
	Tools.close(client);
	garbage.add(client);
      }
      catch (IllegalStateException e)
      {
      }
    }
    gc();
  }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
    writer.println("Port: " + port);
    writer.println("Number of clients: " + clients.size());
    writer.println("Server status: " + server.getStatus());
  }

  private void gc()
  {
    if (garbage.size() > 0)
    {
      synchronized(clients)
      {
        for (Respondent<Request, Response> client : garbage)
        {
          clients.remove(client);
        }
        info("Clients removed: " + garbage.size() 
            + "; clients: " + clients.size());
      }
      garbage.clear();
    }
  }

}
