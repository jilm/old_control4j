/*
 *  Copyright 2016 Jiri Lidinsky
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
package control4j.modules.papouch;

import control4j.AOutputSize;
import java.io.IOException;
import java.util.Date;
import control4j.ICycleEventListener;
import control4j.OutputModule;
import control4j.ResourceManager;
import control4j.Signal;
import control4j.protocols.spinel.SpinelMessage;
import control4j.protocols.spinel.SpinelOverTcp;
import control4j.protocols.tcp.SocketKey;
import control4j.tools.IResponseCrate;
import static control4j.tools.Logger.*;
import control4j.tools.Tools;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.reflect.Setter;
import java.util.Objects;

/**
 */
abstract class Papouch extends OutputModule implements ICycleEventListener {

    /**
     * Spinel address of the module.
     */
    protected int address;

    protected void reconnect() {
        if (failureCounter > TIMEOUT) {
            warning("Communication with the TQS3 thermometer obviously doesn't"
                + " work well; going to reconnect it!");
            Tools.close(ResourceManager.remove(key));
            failureCounter = 0;
        }
    }

    private SocketKey key = new SocketKey("localhost", 10001);
    
    protected SpinelMessage request;
    protected IResponseCrate<SpinelMessage> response;
    protected Date timestamp;
    protected int status = 10;
    /** How many scans without successful temperature before the connection
        will be restarted */
    private static final int TIMEOUT = 50;
    /** Counts unsuccessful scans in row. */
    private int failureCounter = 0;
    
    /**
     *  Reconnect
     */
    @Override
    public void cycleEnd() { 
        reconnect();
    }

    /**
     *  Sends request for new data.
     */
    @Override
    public void cycleStart() {
        // Request for temperature
        if (response == null || response.isFinished()) {
            try {
                SpinelOverTcp channel = ResourceManager.getOrCreate(
                        SpinelOverTcp.class, key, this::connect);
                response = channel.send(request);
            } catch (Exception e) {
                catched(getClass().getName(), "cycleStart", e);
                //Tools.close(ResourceManager.remove(key));
                response = null;
            }
        }
    }

    /**
     *  If not connected, it creates and returns new connection.
     * 
     *  @return created connection
     * 
     *  @throws CommonException
     *              if connection was not created because of some failure
     */
    protected SpinelOverTcp connect() {
        SpinelOverTcp channel = new SpinelOverTcp(
                key.getHost(), key.getPort());
        channel.start();
        return channel;
    }

    protected SpinelMessage responseMessage;
    
    /**
     *  Gets the response message and interprets is.
     */
    @Override
    public void processingStart() {
        // temperature measurement
        if (response != null && response.isFinished()) {
            try {
                responseMessage = response.getResponse();
                finest("response: " + responseMessage.toString());
                status = 0;
                timestamp = response.getTimestamp();
                failureCounter = 0;
            } catch (control4j.protocols.spinel.SpinelException e) {
                // something is wrong with the module
                catched(getClass().getName(), "processingStart", e);
                status = 1;
                failureCounter++;
            } catch (IOException e) {
                // something is wrong with the communication channal
                catched(getClass().getName(), "processingStart", e);
                status = 2;
                failureCounter++;
            } catch (Exception e) {
                // something else is wrong
                catched(getClass().getName(), "processingStart", e);
                status = 3;
                failureCounter++;
            } finally {
                response = null;
            }
        } else {
            // response has not been received yet
            status = 3;
        }
    }

    /**
     *  @param address the address to set
     */
    @Setter("address")
    public void setAddress(int address) {
        this.address = address;
        request
            = new SpinelMessage(address, control4j.hw.papouch.TQS3.MEASUREMENT);
    }

    /**
     *  @param host the host to set
     */
    @Setter("host")
    public void setHost(String host) {
        key = new SocketKey(host, key.getPort());
    }

    /**
     *  @param port the port to set
     */
    @Setter("port")
    public void setPort(int port) {
        key = new SocketKey(key.getHost(), port);
    }

}