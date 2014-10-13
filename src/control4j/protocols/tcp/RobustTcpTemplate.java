package control4j.protocols.tcp;

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

import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;
import control4j.tools.Queue;
import control4j.tools.IResponseCrate;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *  An easy to use template for request / response communication over TCP 
 *  protocol. A commutication is performed by the independent thread, 
 *  so the methods read and write doesn't block. If an exception is 
 *  thrown during the communication, or during the communication 
 *  negotiation, all of the resources which were created so far are closed 
 *  and released, and the new process of communication connection is started.
 *  This process is completly transparent.
 *
 *  <p>The only thing you must do to use this template is override the 
 *  methods getSpecificInputStream and getSpecificOutputStream.
 */
public abstract class RobustTcpTemplate<I, O>
{

  /**
   *  Reading response timeout in milliseconds.
   */
  public int timeout = 3333;

  private String host;
  private int port;
  protected Queue<TransactionCrate> transactionBuffer = new Queue<TransactionCrate>();
  private Connection connection;

  /**
   *  Starts a thread which performs loop: send request, wait for response.
   *
   *  @param host 
   *             host name
   *
   *  @param port 
   *             port number
   */
  public RobustTcpTemplate(String host, int port)
  {
    this.host = host;
    this.port = port;
    connection = new Connection();
    connection.start();
  }

  /**
   *  Create and return input stream which correspond to the required
   *  protocol.
   *
   *  @param inputStream
   *             lower level input stream
   */
  abstract protected IInputStream<I> getSpecificInputStream(InputStream inputStream);
  
  /**
   *  Create and return output stream which correspond to the required
   *  protocol.
   *
   *  @param outputStream
   *             lower level output stream
   */
  abstract protected IOutputStream<O> getSpecificOutputStream(OutputStream outputStream);
  
  /**
   *  Close the connection and release all of the resources.
   */
  public void close()
  {
  }

  /**
   *  Sends the request given as a parameter through the communication
   *  connection. The request is placed into the internal buffer and
   *  send after all of the previous requests have been sent.
   *
   *  @param request 
   *             request to be send
   *
   *  @return an object through which the response will be passed
   */
  public IResponseCrate<I> write(O request)
  {
    TransactionCrate transaction = new TransactionCrate(request);
    transactionBuffer.queue(transaction);
    return transaction;
  }

  private class Connection extends Thread
  {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private IInputStream<I> specificInputStream;
    private IOutputStream<O> specificOutputStream;
    private boolean logFlag = true;

    public Connection()
    {
      super("RobustTcpTemplate:Connection");
    }

    @Override public void run()
    {
      while (true)
      {
        // estabilish a connection
	try
	{
          InetAddress address = InetAddress.getByName(host);
          socket = new Socket(address, port);
	  socket.setSoTimeout(timeout);
          inputStream = socket.getInputStream();
          outputStream = socket.getOutputStream();
          specificInputStream = getSpecificInputStream(inputStream);
          specificOutputStream = getSpecificOutputStream(outputStream);
	  logFlag = true;

	  // enter the request / response loop
	  TransactionCrate transaction = null;
	  try
	  {
	    while (true)
	    {
	      transaction = transactionBuffer.blockingDequeue();
	      // send request
	      transaction.markRequest();
	      specificOutputStream.write(transaction.getRequest());
	      // wait for response
	      I response = specificInputStream.readMessage();
	      transaction.setResponse(response, null);
	    }
	  }
	  catch (IOException e)
	  {
	    transaction.setResponse(null, e);
	    throw e;
	  }
	}
        catch (UnknownHostException e)
        {
          if (logFlag)
            warning(getMessage("spn04", e.getMessage()));
          logFlag = false;
        }
        catch (IOException e)
        {
          if (logFlag)
            warning(getMessage("spn06", e.getMessage()));
          logFlag = false;
        }
        finally   // close all resources
        {
          close();
          try { sleep(1000); } catch (InterruptedException ex) {}
        }

      }
    }

    private void close()
    {
      if (specificInputStream != null)
      {
        try { specificInputStream.close(); } catch (IOException ioex) {}
        specificInputStream = null;
      }
      if (specificOutputStream != null)
      {
        try { specificOutputStream.close(); } catch (IOException ioex) {}
        specificOutputStream = null;
      }
      if (inputStream != null)
      {
        try { inputStream.close(); } catch (IOException ioex) {}
        inputStream = null;
      }
      if (outputStream != null)
      {
        try { outputStream.close(); } catch (IOException ioex) {}
        outputStream = null;
      }
      if (socket != null)
      {
        try { socket.close(); } catch (IOException ioex) {}
        socket = null;
      }
    }
  }


  private class TransactionCrate implements IResponseCrate<I>
  {
    private O request;
    private I response = null;
    private IOException exception = null;
    private long requestTimestamp;
    private long responseTimestamp;
    private boolean requested = false;
  
    TransactionCrate(O request)
    {
      this.request = request;
    }
  
    public O getRequest()
    {
      return request;
    }
  
    public synchronized I getResponse() throws IOException
    {
      while (response == null && exception == null)
	try 
	{
          wait();
        }
	catch (InterruptedException e) { }
      if (exception != null)
        throw exception;
      else
        return response;
    }
  
    synchronized void setResponse(I response, IOException exception)
    {
      this.responseTimestamp = System.currentTimeMillis();
      this.response = response;
      this.exception = exception;
      notifyAll();
    }
  
    public synchronized boolean isFinished()
    {
      return response != null || exception != null;
    }
  
    synchronized void markRequest()
    {
      requestTimestamp = System.currentTimeMillis();
      requested = true;
    }
  
    public synchronized Date getTimestamp()
    {
      if (isFinished() && requested)
        return new Date((responseTimestamp + requestTimestamp)/2);
      else
        return null;
    }
  }

}
