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

import control4j.tools.InterchangePoint;
import control4j.tools.Tools;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  Waits for new data request from a remote host and than send response. 
 *  Typicaly it is used together with server to serve one connection.
 *
 *  <p>First of all you must use the read method to get a request from
 *  remote host. Then you can use the write method to respond to the
 *  request. The pair of opperations, send response, wait for request
 *  is done in a separate thred.
 *
 *  @see Server
 *  @see IClientFactory
 *
 */
public class Respondent<I, O> implements Runnable
{

  private IInputStream<I> inputStream;
  private IOutputStream<O> outputStream;
  private InterchangePoint<I> request = new InterchangePoint<I>();
  private InterchangePoint<O> response = new InterchangePoint<O>();

  /** Signal that the new data request has been received. */
  private volatile boolean gotRequest = false;

  /** Contains an exception thrown during proccessing, if there is one */
  private volatile IOException exception = null;

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

  public void initialize()
  {
    new Thread(this).start();
  }

  /**
   *  Close the given input and output stream and release all of the 
   *  resources.
   */
  public void close()
  {
    Tools.close(inputStream, getClass().getName(), "close");
    inputStream = null;
    Tools.close(outputStream, getClass().getName(), "close");
    outputStream = null;
  }

  /**
   *  Going to send response message. The response is send in separate
   *  thread, so this method returns immediately and doesn't block.
   *  The response is send only under condition that data request was
   *  received since the last response was sent and that the data
   *  request was picked up by the <code>read</code> method.
   *
   *  @param response
   *             a message to send
   *
   *  @throws IOException
   *             if an exception was thrown while waiting for new data
   *             request or while sending a response
   *
   *  @throws IllegalStateException
   *             if you call this method without a new data request or
   *             without reading it
   *
   *  @see #read
   */
  public void write(O response) throws IOException
  {
    if (exception != null) throw exception;
    if (gotRequest && request.isEmpty())
    {
      this.response.forcedSet(response);
      new Thread(this).start();
    }
    else
    {
      throw new IllegalStateException(
	  "Trying to send data response without a request");
    }
  }

  /**
   *  Returns a new data request that was received or null. Once the 
   *  request is picked up, it is no longer available. This method
   *  returns immediately and doesn't block.
   *
   *  @throws IOException
   *             if the exception was thrown while waiting for request
   *             or sending a response
   *
   *  @return a new data request or null
   */
  public I read() throws IOException
  {
    if (exception != null) throw exception;
    return request.tryGet();
  }

  /**
   *  It writes a response, if there is one and then it waits for a new
   *  request. Then it ends. This method is intended to be run as a parallel
   *  thread, that is run by <code>write</code> method.
   *
   *  <p>If an <code>IOException</code> is catched while waiting for new
   *  data request or while sending a response, the given input and output
   *  stream is closed and <code>isActive</code> method starts to return
   *  <code>false</code> value.
   *
   *  @throws IllegalStateException
   *             if this method is invoked without a response to sent
   *
   *  @see #write
   */
  public void run()
  {
    if (exception != null) return;
    try
    {
      // send response if there is one
      if (!response.isEmpty())
      {
	finest("Going to send response");
        outputStream.write(response.get());
	gotRequest = false;
      }
      if (!gotRequest)
      {
        // wait for request
        request.forcedSet(inputStream.readMessage());
        finest("Got new data request");
        gotRequest = true;
      }
      else
      {
	throw new IllegalStateException(
	    "Doesn't have a response for a data request to sent!");
      }
    }
    catch (IOException e)
    {
      exception = e;
      catched(getClass().getName(), "run", e);
      warning("Catched IOException and closing the connection!");
      close();
    }
  }

  /**
   *  Returns true if the connection is active and no exception was
   *  already catched, false otherwise.
   */
  public boolean isActive()
  {
    return exception == null;
  }

}
