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
 */
public class Questioner<I, O> extends Thread
{

  /**
   *  Reading response timeout in milliseconds.
   */
  public int timeout = 3333;

  private IInputStream<I> inputStream;
  private IOutputStream<O> outputStream;
  protected Queue<TransactionCrate> transactionBuffer 
      = new Queue<TransactionCrate>();
  private boolean logFlag = true;

  /**
   */
  public Questioner(IInputStream<I> inputStream, IOutputStream<O> outputStream)
  {
    super("RequestResponseTemplate");
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

  public void run()
  {
    // enter the request / response loop
    TransactionCrate transaction = null;
    try
    {
      while (true)
      {
        transaction = transactionBuffer.blockingDequeue();
        // send request
        transaction.markRequest();
        outputStream.write(transaction.getRequest());
        // wait for response
        I response = inputStream.readMessage();
        transaction.setResponse(response, null);
      }
    }
    catch (IOException e)
    {
      transaction.setResponse(null, e);
      catched(getClass().getName(), "run", e);
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
