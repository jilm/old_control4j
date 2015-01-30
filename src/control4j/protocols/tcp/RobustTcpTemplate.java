package control4j.protocols.tcp;

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

import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;
import control4j.tools.Queue;
import control4j.tools.IResponseCrate;
import control4j.tools.Tools;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  An easy to use template for request / response communication client
 *  over TCP/IP protocol. A commutication is performed by the independent 
 *  thread, so the methods for read and write doesn't block. If an 
 *  exception is thrown during the communication, or during the communication 
 *  negotiation, all of the resources which were created so far are closed 
 *  and released, and the new process of communication connection is started.
 *  This process is completly transparent.
 *
 *  <p>The only thing you must do to use this template is override 
 *  methods <code>getSpecificInputStream</code> and 
 *  <code>getSpecificOutputStream</code>.
 *
 */
public abstract class RobustTcpTemplate<I, O>
implements Runnable, java.io.Closeable
{

  /** Reading response timeout in milliseconds. */
  protected int timeout = 3333;

  /** Host name of the remote server. */
  protected String host;

  /** A port on which the remote server listens. */
  protected int port;

  /** A queue of the requests that waits for sending. */
  protected final Queue<TransactionCrate> transactionBuffer 
                               = new Queue<TransactionCrate>();

  /** An instruction to stop the thread that provides request - response loop */
  private volatile boolean stop = false;

  /** Indicate wheater the request - response loop has ever been started. */
  private volatile boolean run = false;

  /** Indicates that the connection has been estabilished. */
  private volatile boolean connected = false;

  /** A time after which the transation expires, if it is not finished. */
  protected long transactionTimeout = 10000l;

  /** Identification of this client for logging purposes. */
  protected String identification;

  /**
   *  Initialize internal fields.
   *
   *  @param host 
   *             host name of the remote server
   *
   *  @param port 
   *             port number on which the remote server listens
   */
  public RobustTcpTemplate(String host, int port)
  {
    this.host = host;
    this.port = port;
    identification = "TCP/IP client; class: " + getClass().getName() 
	+ "; host: " + host + "; port: " + port;
  }

  public RobustTcpTemplate(String host, int port, int timeout)
  {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    identification = "TCP/IP client; class: " + getClass().getName() 
	+ "; host: " + host + "; port: " + port;
  }

  /**
   *  Starts a request - response loop in a separate thread.
   *
   *  <p>If the thread is already running, nothing happens.
   *
   *  <p>If the thread has been already closed ???
   */
  public void start()
  {
    if (!run && !stop)
    {
      new Thread(this, identification).start();
      run = true;
    }
    else
    {
      assert false;
    }
  }

  /**
   *  Create and return input stream which correspond to the required
   *  protocol. In other words, this method must return an input stream,
   *  which can receve the message in appropriate format.
   *
   *  @param inputStream
   *             lower level input stream
   *
   *  @return an input stream which is cappable to receive the message
   *             of the appropriate form
   */
  abstract protected IInputStream<I> getSpecificInputStream(
      InputStream inputStream);
  
  /**
   *  Create and return output stream which correspond to the required
   *  protocol. In other words, this method must return an output stream,
   *  which can send the given massage through the given lower level
   *  output stream.
   *
   *  @param outputStream
   *             lower level output stream
   *
   *  @return an output stream which is cappable to send the message
   *             through the given lower level output stream
   */
  abstract protected IOutputStream<O> getSpecificOutputStream(
      OutputStream outputStream);

  /**
   *  Should return a valid request message. This message is never
   *  sent, it servers only for internal purposes, so the content
   *  of the message may be arbitrary. It should not return 
   *  <code>null</code> value.
   *
   *  @return an empty request message
   */
  abstract protected O getEmptyRequest();
  
  /**
   *  Causes the request - response loop to stop. Before the loop stops
   *  it closes all of the resources. Once the loop is stopped, it cannot
   *  be started again.
   *
   *  <p>If the request - response loop is not running, or if it has
   *  already been stopped, nothing happens.
   */
  public void close()
  {
    stop = true;
    // put an empty essage into the queue to wake up the r/r thread
    try
    {
      write(getEmptyRequest());
    }
    catch (IOException e) { }
  }

  /**
   *  Sends the request given as a parameter through the communication
   *  connection. The request is placed into the internal buffer and
   *  send after all of the previous requests have been sent.
   *
   *  <p>This method doesn't block. It returns an object through
   *  which you can get response, after it was received, or through
   *  which you can watch the state of this transaction.
   *
   *  @param request 
   *             request to be send
   *
   *  @return an object through which the response will be passed
   *
   *  @throws IOException
   *             if connection has not been estabilished
   */
  public IResponseCrate<I> write(O request) throws IOException
  {
    if (connected)
    {
      TransactionCrate transaction = new TransactionCrate(request);
      transactionBuffer.queue(transaction);
      return transaction;
    }
    else
    {
      throw new IOException("Connection has not been estabished!\n"
	  + identification);
    }
  }

  private Socket socket;
  private InputStream inputStream;
  private OutputStream outputStream;
  private IInputStream<I> specificInputStream;
  private IOutputStream<O> specificOutputStream;
  private boolean logFlag = true;

  /**
   *
   */
  public void run()
  {
    while (!stop)
    {
      // estabilish a connection
      try
      {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
        socket.setSoTimeout(timeout);
        outputStream = socket.getOutputStream();
        specificOutputStream = getSpecificOutputStream(outputStream);
        inputStream = socket.getInputStream();
        specificInputStream = getSpecificInputStream(inputStream);
        logFlag = true;
	connected = true;
        fine("Connection has been estabilished\n" + identification);

        // enter the request / response loop
        TransactionCrate transaction = null;
        try
        {
          while (!stop)
          {
	    // get request
            transaction = transactionBuffer.blockingDequeue();
	    if (transaction.isFinished()) continue; // transaction may expire
            // send request
	    if (stop) break;
            transaction.markRequest();
            specificOutputStream.write(transaction.getRequest());
            finest("Request has been sent.\n" + identification);
            // wait for response
	    if (stop) break;
            I response = specificInputStream.readMessage();
            finest("Response received\n" + identification);
            transaction.setResponse(response, null);
          }
        }
        catch (IOException e)
        {
          transaction.setResponse(null, e);
          catched(getClass().getName(), "run", e);
          //throw e;
        }
        catch (Exception e)
        {
          catched(getClass().getName(), "run", e);
        }
      }
      catch (UnknownHostException e)
      {
        if (logFlag)
	  catched(identification, "run", e);
        logFlag = false;
      }
      catch (IOException e)
      {
        if (logFlag)
	  catched(identification, "run", e);
        logFlag = false;
      }
      finally   // close all resources
      {
	connected = false;
        closeConnection();
	// remove and discard all of the requests
	IOException exception 
	    = new IOException("Connection has been closed!\n" + identification);
	TransactionCrate transaction = transactionBuffer.dequeue();
	while (transaction != null)
	{
	  transaction.setResponse(null, exception);
	  transaction = transactionBuffer.dequeue();
	}
	// wait for a while
	Tools.sleep(1000l);
      }

    }
  }

  /**
   *  Close all of the opened connections.
   */
  private void closeConnection()
  {
    fine("Going to close connections ...\n" + identification);
    Tools.close(specificInputStream, getClass().getName(), "closeConnection");
    specificInputStream = null;
    Tools.close(specificOutputStream, getClass().getName(), "closeConnection");
    specificOutputStream = null;
    Tools.close(inputStream, getClass().getName(), "closeConnection");
    inputStream = null;
    Tools.close(outputStream, getClass().getName(), "closeConnection");
    outputStream = null;
    if (socket != null)
    {
      try { socket.close(); } catch (IOException ioex) {}
      socket = null;
    }
  }

  /**
   *
   *  A class that encapsulates one whole transaction. It means, the
   *  request message, the response message, response timestamp,
   *  exceptions throughout the communication. The transaction
   *  expires if the respons is not received during the specified
   *  timeout.
   *
   */
  private class TransactionCrate implements IResponseCrate<I>
  {

    private O request;
    private I response = null;
    private IOException exception = null;
    private long requestTimestamp;
    private long responseTimestamp;

    /** A time when this object was created. It is used for expiration
	purposes. */
    private long created;

    private boolean requested = false;
  
    /**
     *  Initialize fields.
     */
    TransactionCrate(O request)
    {
      this.request = request;
      created = System.currentTimeMillis(); 
    }
  
    /**
     *  Return request message.
     */
    public O getRequest()
    {
      return request;
    }
  
    /**
     *  Return response message. This method blocks until the response
     *  is received.
     */
    public synchronized I getResponse() throws IOException
    {
      while (!isFinished())
	try 
	{
          long timeToTimeout 
	      = transactionTimeout - System.currentTimeMillis() + created;
          if (timeToTimeout >= 0l)
            wait(timeToTimeout);
	  else
	    setResponse(null, new IOException("Transaction timeout"));
        }
	catch (InterruptedException e) 
	{
	}
      if (exception != null)
        throw exception;
      else
        return response;
    }
  
    synchronized void setResponse(I response, IOException exception)
    {
      if (!isFinished())
      {
        this.responseTimestamp = System.currentTimeMillis();
        this.response = response;
        this.exception = exception;
        notifyAll();
      }
      else
      {
	// TODO
      }
    }
  
    public synchronized boolean isFinished()
    {
      long timeToTimeout 
	  = transactionTimeout - System.currentTimeMillis() + created;
      if (timeToTimeout < 0l)
	setResponse(null, new IOException("Transaction timeout"));
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
