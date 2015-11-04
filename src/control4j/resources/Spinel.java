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

package control4j.resources;

import static control4j.tools.Logger.*;
import static cz.lidinsky.tools.Validate.notNull;
import static cz.lidinsky.tools.Validate.notBlank;

import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.Resource;
import control4j.tools.IResponseCrate;
import control4j.protocols.spinel.SpinelInputStream;
import control4j.protocols.spinel.SpinelOutputStream;
import control4j.protocols.spinel.SpinelMessage;
import control4j.protocols.tcp.RobustSocket;
import cz.lidinsky.tools.reflect.Setter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 *  Provides communication with a device or devices through the socket with
 *  spinel protocol. The communication has the request / response form. Each
 *  request is placed into the queue and it is sent after all of the preceding
 *  requests were arranged. To arrange one request, the request is sent and the
 *  response must be received.
 *
 *  Properties:
 *  <ul>
 *    <li> host -- host name of the device
 *    <li> port -- port number
 *  </ul>
 *
 */
public class Spinel extends Resource implements Runnable {

  private String host;
  private int port;
  private SpinelInputStream inputStream;
  private SpinelOutputStream outputStream;
  private BlockingQueue<TransactionCrate> queue
    = new LinkedBlockingQueue<TransactionCrate>();
  private String identification;
  private long transactionTimeout = 3000;

  @Setter("host")
  public void setHost(String host) {
    this.host = notBlank(host);
  }

  @Setter("port")
  public void setPort(int port) {
    this.port = port;
  }

  /**
   *  The queue is initialized, the socket is created and the request, response
   *  loop thread is started. This method should be called only once before the
   *  communication should start.
   */
  @Override
  public void prepare() {
    queue.clear();
    // initialize socket
    RobustSocket socket = new RobustSocket(host, port, 3333);
    inputStream = new SpinelInputStream(socket.getInputStream());
    outputStream = new SpinelOutputStream(socket.getOutputStream());
    // start the send / receive loop
    identification = "Spinel, host: " + host + ", port: " + port;
    new Thread(this, identification).start();
  }

  @Override
  public boolean isEquivalent(control4j.application.Resource definition) {
    if (definition == null) return false; // TODO:
    if (!definition.getClassName().equals(getClass().getName())) return false;
    try {
      InetAddress address = InetAddress.getByName(definition.getValue("host"));
      return address.equals(InetAddress.getByName(host))
        && Integer.parseInt(definition.getValue("port")) == port;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   *  Writes the message into the internal queue and returns a case object
   *  where the sender can pick the response up as soon as it will be
   *  available. Messages are sended in the order they were written.
   *
   *  @param message
   *             the message to send
   *
   *  @return the handler object of this communication transaction where the
   *             caller could pick the response up.
   */
  public IResponseCrate<SpinelMessage> write(SpinelMessage message)
  throws java.io.IOException {
    finest("Sending request: " + message.toString());
    TransactionCrate transaction = new TransactionCrate(message);
    queue.offer(transaction);
    return transaction;
  }

  /**
   *  Sends a request from the internal queue, waits for response and than
   *  again. This infinite loop is performed in the separate thread.
   */
  @Override
  public void run() {
    while (true) {
      // get the request to send
      TransactionCrate transaction = null;
      while (transaction == null) {
        try {
          transaction = queue.take();
          transaction = transaction.isFinished() ? null : transaction;
        } catch (InterruptedException e) {} // It is OK
      }
      // send the request
      try {
        outputStream.write(transaction.getRequest());
        transaction.markRequest();
        SpinelMessage response = inputStream.readMessage();
        transaction.setResponse(response);
      } catch (Exception e) {
        catched(getClass().getName(), "run", e);
        transaction.setResponse(new IOException(e));
      }
    }
  }

  /**
   *
   *  A class which encapsulates one whole transaction. It means, the request
   *  message, the response message, response timestamp, exceptions throughout
   *  the communication. The transaction expires if the respons is not received
   *  by the specified timeout.
   *
   */
  private class TransactionCrate implements IResponseCrate<SpinelMessage> {

    private SpinelMessage request;
    private SpinelMessage response = null;
    private IOException exception = null;
    private long requestTimestamp;
    private long responseTimestamp;

    /**
     *  A time when this object was created. It is used for expiration
     *  purposes.
     */
    private long created;

    /**
     *  Indicate that the request has been sent.
     */
    private boolean requested = false;

    /**
     *  Initialize fields.
     */
    TransactionCrate(SpinelMessage request) {
      this.request = request;
      created = System.currentTimeMillis();
    }

    /**
     *  Return request message.
     */
    public SpinelMessage getRequest() {
      return request;
    }

    /**
     *  Return response message. This method blocks until the response is
     *  received.
     */
    public synchronized SpinelMessage getResponse() throws IOException {
      while (!isFinished())
        try {
          long timeToTimeout
              = transactionTimeout - System.currentTimeMillis() + created;
          if (timeToTimeout >= 0l) {
            wait(timeToTimeout);
          } else {
            setResponse(new IOException("Transaction timeout"));
          }
        } catch (InterruptedException e) {
          // it is OK, only for unwanted disturbation
        }
      if (exception != null) {
        throw exception;
      } else {
        return response;
      }
    }

    synchronized void setResponse(IOException exception) {
      if (!isFinished()) {
        this.responseTimestamp = System.currentTimeMillis();
        this.response = null;
        this.exception = exception;
      }
      notifyAll();
    }

    synchronized void setResponse(SpinelMessage response) {
      if (!isFinished()) {
        this.responseTimestamp = System.currentTimeMillis();
        this.response = response;
        this.exception = null;
      }
      notifyAll();
    }

    public synchronized boolean isFinished() {
      long timeToTimeout
          = transactionTimeout - System.currentTimeMillis() + created;
      if (timeToTimeout < 0l) {
        setResponse(new IOException("Transaction timeout"));
      }
      return response != null || exception != null;
    }

    synchronized void markRequest() {
      requestTimestamp = System.currentTimeMillis();
      requested = true;
    }

    public synchronized Date getTimestamp() {
      if (isFinished() && requested) {
        return new Date((responseTimestamp + requestTimestamp)/2);
      } else {
        return null;
      }
    }
  }

}
