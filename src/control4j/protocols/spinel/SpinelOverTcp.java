package control4j.protocols.spinel;

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
import control4j.tools.IResponseCrate;
import static control4j.tools.Logger.catched;
import static control4j.tools.Logger.finest;
import static control4j.tools.Logger.info;
import control4j.tools.Queue;
import control4j.tools.Tools;
import control4j.tools.Transaction;
import java.io.Closeable;
import java.io.IOException;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Communication over the spinel protocol. First of all call connect method to
 * estabilish connection. After that the send method may be used to send a
 * message through the channel.
 */
public class SpinelOverTcp implements Closeable {

    private final Queue<Transaction<SpinelMessage, SpinelMessage>> buffer;
    private boolean connected;
    private final String host;
    private final int port;
    private final String identification;

    /**
     * Initialize communication. Call start method in order to estabilish
     * connection with given parameters.
     *
     * @param host host name
     *
     * @param port port number
     */
    public SpinelOverTcp(final String host, final int port) {
        this.host = host;
        this.port = port;
        this.buffer = new Queue<>();
        this.connected = false;
        this.closed = false;
        this.identification = "Spinel protocol; host: " + host + "; port: "
                + Integer.toString(port);
    }

    /**
     * Estabilish connection with the host on the given port. New thread is
     * started to make the connection in order not to block the main thread. So,
     * this method returns immediately.
     */
    public void start() {
        new Thread(this::run, identification).start();
    }

    /**
     * This method is used to send a message through the channel. This method
     * stores the message into the quieue. The message is send in the separate
     * thred in order not to block the main thread. So, this method returns
     * immediately.
     *
     * @param message a message to be send
     *
     * @return an object which should be used to transaction status control
     *
     * @throws IOException if the connection has not been estabilished yet or if
     * it has been closed
     */
    public IResponseCrate send(SpinelMessage message) throws IOException {
        if (connected) {
            Transaction transaction = new Transaction(message, 3333);
            buffer.queue(transaction);
            return transaction;
        } else {
            throw new IOException("The channel is not connected!" + identification);
        }
    }

    /**
     * The main thread.
     */
    private void run() {

        // connect
        while (!closed) {
            try (
                    Socket socket = new Socket(host, port);
                    SpinelInputStream inputStream = new SpinelInputStream(socket.getInputStream());
                    SpinelOutputStream outputStream = new SpinelOutputStream(socket.getOutputStream());) {
                info("Connection over the spinel protocol was successfuly created. "
                        + identification);
                while (!closed) {
                    
                    connected = true;
                    // get next message to send
                    Transaction<SpinelMessage, SpinelMessage> transaction = buffer.blockingDequeue();
                    if (transaction == null) {
                        continue;
                    }
                    // send it
                    finest("Going to send a message. " + identification);
                    outputStream.write(transaction.getRequest());
                    // wait for response
                    if (closed) {
                        break;
                    }
                    SpinelMessage response = inputStream.readMessage();
                    finest("A message received. " + identification);
                    // return response
                    transaction.setResponse(response);
                }
            } catch (IOException ex) {
                catched(getClass().getName(), "run", ex);
                Tools.sleep(1237);
            } finally {
                connected = false;
            }
        }
    }

    /**
     * True if the channel has been closed.
     */
    private boolean closed;

    /**
     *
     */
    @Override
    public void close() {
        closed = true;
        while (!buffer.isEmpty()) {
            buffer.dequeue().setException(
                    new IOException("The channel has been closed! " + identification));
        }
        info("The spinel channel has been closed. " + identification);
    }

}
