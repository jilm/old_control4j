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
package control4j.protocols.tcp;

import java.util.Objects;

/**
 */
public class SocketKey {

    private final String host;
    private final int port;

    public SocketKey(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SocketKey && ((SocketKey) object).host.equals(host)
                && ((SocketKey) object).port == port;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.host);
        hash = 53 * hash + this.port;
        return hash;
    }

    private SocketKey setHost(String host) {
        return new SocketKey(host, port);
    }

    private SocketKey setPort(int port) {
        return new SocketKey(host, port);
    }
}   