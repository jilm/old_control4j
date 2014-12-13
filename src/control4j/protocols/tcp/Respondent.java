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

import java.io.IOException;
import control4j.tools.InterchangePoint;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  Wait for request from a remote host and than send response. Typicaly
 *  it is used together with server to serve one connection.
 *
 *  <p>First of all you must use the read method to get a request from
 *  remote host. Then you can use the write method to respond to the
 *  request. The pair of opperations, send response, wait for request
 *  is done in a separate thred.
 *
 */
public class Respondent<I, O> implements Runnable
{

  private IInputStream<I> inputStream;
  private IOutputStream<O> outputStream;
  private InterchangePoint<I> request = new InterchangePoint<I>();
  private InterchangePoint<O> response = new InterchangePoint<O>();

  /** Signal that the request has been picked up and the response may be
      accepted. */
  private boolean isRequestPickedUp = false;

  /** Contains an exception thrown during proccessing, if there is one */
  private Exception exception = null;

  /**
   */
  public Respondent(IInputStream<I> inputStream, IOutputStream<O> outputStream)
  {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  /**
   *  Close the connection and release all of the resources.
   */
  public void close()
  {
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
  }

  /**
   */
  public void write(O response)
  {
    if (!isRequestPickedUp)
      throw new IllegalStateException();
    this.response.forcedSet(response);
    this.request.forcedSet(null);
    isRequestPickedUp = false;
    new Thread(this).start();
  }

  /**
   *  Return a request which has been received from a remote host. It returns
   *  null if the response has not been already got. Once the request is
   *  picked up, it is no longer available.
   *
   *  @throw IllegalStateException
   *            if you try to read the request immediatly after is was 
   *            successfuly read.
   */
  public I read()
  {
    if (isRequestPickedUp)
      throw new IllegalStateException();
    isRequestPickedUp = !request.isEmpty();
    return request.tryGet();
  }

  /**
   *  It writes a response, if there is one and then it waits for a new
   *  request. Then it end. This method is intended to be run as a parallel
   *  thread, do not call this method directly.
   */
  public void run()
  {
    try
    {
      // send response
      if (!response.isEmpty())
        outputStream.write(response.get());
      // wait for request
      request.forcedSet(inputStream.readMessage());
    }
    catch (IOException e)
    {
      exception = e;
      catched(getClass().getName(), "run", e);
    }
  }

  public boolean isActive()
  {
    return exception == null;
  }

}
