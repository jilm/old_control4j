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

package control4j.protocols.tcp;

import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

import java.io.IOException;
import java.util.function.Function;

import control4j.tools.Tools;

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
public class Respondent<I, O> implements Runnable, java.io.Closeable {

  private IInputStream<I> inputStream;
  private IOutputStream<O> outputStream;
  private Function<I, O> responseProvider;

  private static final java.util.Set<Respondent> respondents
    = new java.util.HashSet<>();

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
  public Respondent(
      IInputStream<I> inputStream,
      IOutputStream<O> outputStream,
      Function<I, O> responseProvider) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.responseProvider = responseProvider;
  }

  /**
   *  Starts a thread that waits for the first request.
   */
  public void initialize() {
    respondents.add(this);
    new Thread(this).start();
    initialized = true;
  }

  /**
   *  Close the given input and output stream and release all of the 
   *  resources.
   */
  public void close() {
    closed = true;
    Tools.close(inputStream, getClass().getName(), "close");
    inputStream = null;
    Tools.close(outputStream, getClass().getName(), "close");
    outputStream = null;
  }

  /**
   *  It writes a response, if there is one and then it waits for a new
   *  request. Then it ends. This method is intended to be run as a parallel
   *  thread; it is run by <code>write</code> method.
   *
   *  <p>If an <code>IOException</code> is catched while waiting for new data
   *  request or while sending a response, <code>isActive</code> method starts
   *  to return <code>false</code> value.
   *
   *  @see #write
   */
  public void run() {
    if (closed) return;
    synchronized(this) {
      processing = true;
    }
    try {
      while (true) {
        I request = null;
        // wait for request
        while (request == null) {
          finest("Waiting for a data request...");
          request = inputStream.readMessage();
          finest("Got new data request");
        }
        // get response
        O response = responseProvider.apply(request);
        // send response
        finest("Going to send response: " + response.toString());
        outputStream.write(response);
        finest("Response sent.");
      }
    } catch (IOException e) {
      exception = e;
      catched(getClass().getName(), "run", e);
      warning("Catched IOException and closing the connection!");
      //close();
    } finally {
      processing = false;
    }
  }

  /**
   *  Returns true if the connection is active and no exception was
   *  already catched, false otherwise.
   */
  public synchronized boolean isActive() {
    return exception == null && !closed;
  }

}
