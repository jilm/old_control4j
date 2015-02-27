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

import java.io.IOException;

import control4j.tools.Tools;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  Waits for new request from a remote host and than sends a response. 
 *  Typicaly, it is used together with server to serve one connection.
 *
 *  <p>First of all you must use the read method to get a request from
 *  remote host. Then you can use the write method to respond to the
 *  request. The pair of opperations, send response, wait for request
 *  is done in a separate thread.
 *
 *  @see Server
 *  @see IClientFactory
 *
 */
public class Respondent<I, O> implements Runnable, java.io.Closeable
{

  private IInputStream<I> inputStream;
  private IOutputStream<O> outputStream;

  /** Received request. */
  private volatile I request = null;

  /** Response to be sent. */
  private volatile O response = null;

  /** Contains an exception thrown during proccessing, if there is one.
      There is only one place where this field is modified. */
  private volatile IOException exception = null;

  /** Indicate that the input and output stream has been already closed. */
  private volatile boolean closed = false;

  /** Indicates that the thread for sending and receiving messages 
      is running. */
  private volatile boolean processing = false;

  /** Indicates that the initialized method has been called. */
  private volatile boolean initialized = false;

  /**
   *  Initialize
   *
   *  @param inputStream
   *
   *  @param outputStream
   */
  public Respondent(IInputStream<I> inputStream, IOutputStream<O> outputStream)
  {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  /**
   *  Starts a thread that waits for the first request.
   */
  public void initialize()
  {
    new Thread(this).start();
    initialized = true;
  }

  /**
   *  Close the given input and output stream and release all of the 
   *  resources.
   */
  public void close()
  {
    closed = true;
    Tools.close(inputStream, getClass().getName(), "close");
    inputStream = null;
    Tools.close(outputStream, getClass().getName(), "close");
    outputStream = null;
  }

  /**
   *  Going to send a response message. The response is send in separate
   *  thread, so this method returns immediately and doesn't block.
   *  The response is send only under condition that data request has been
   *  received since the last response was sent.
   *
   *  @param response
   *             a message to send
   *
   *  @throws IOException
   *             if an exception was catched during the last IO operation
   *
   *  @throws IOException
   *             if the channel has been closed
   *
   *  @throws IllegalStateException
   *             if called without a request
   *
   *  @throws IllegalStateException
   *             if previous response has not been sent yet
   *
   */
  public synchronized void write(O response) throws IOException
  {
    if (closed)
      throw new IOException("The Responder has already been already closed.");
    if (exception != null)
      throw exception;
    if (processing)
      throw new IllegalStateException(
	  "Previous transaction has not been finished yet");
    if (request != null && this.response == null)
    {
      this.response = response;
      new Thread(this).start();
    }
    else if (request == null)
    {
      throw new IllegalStateException(
	  "Trying to send data response without a request");
    }
    else if (this.response != null)
    {
      throw new IllegalStateException(
	  "Previous request has not been sent yet");
    }
    else
      assert false; // should not happen
  }

  /**
   *  Returns data request that has been received or null.
   *  The request message is not removed before the response
   *  is sent. This method returns immediately and doesn't block.
   *
   *  @return data request or null
   *
   *  @throws IOException
   *             if the exception was thrown while waiting for request
   *             or sending a response
   *
   *  @throws IOException
   *             if the channel has been closed
   */
  public synchronized I read() throws IOException
  {
    if (closed)
      throw new IOException("The Responder has already been closed.");
    if (exception != null)
      throw exception;
    if (processing) return null;
    return request;
  }

  /**
   *  It writes a response, if there is one and then it waits for a new
   *  request. Then it ends. This method is intended to be run as a parallel
   *  thread; it is run by <code>write</code> method.
   *
   *  <p>If an <code>IOException</code> is catched while waiting for new
   *  data request or while sending a response,
   *  <code>isActive</code> method starts to return
   *  <code>false</code> value.
   *
   *  @see #write
   */
  public void run()
  {
    if (closed) return;
    synchronized(this)
    {
      processing = true;
    }
    try
    {
      // send response if there is one
      if (response != null)
      {
	finest("Going to send response");
        outputStream.write(response);
	response = null;
	request = null;
      }
      // wait for request
      while (request == null)
      {
        request = inputStream.readMessage();
        finest("Got new data request");
      }
    }
    catch (IOException e)
    {
      exception = e;
      catched(getClass().getName(), "run", e);
      warning("Catched IOException and closing the connection!");
      //close();
    }
    finally
    {
      processing = false;
    }
  }

  /**
   *  Returns true if the connection is active and no exception was
   *  already catched, false otherwise.
   */
  public synchronized boolean isActive()
  {
    return exception == null && !closed;
  }

}
