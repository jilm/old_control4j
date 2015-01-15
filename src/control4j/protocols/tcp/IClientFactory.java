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
import java.net.Socket;

/**
 *
 *  Creates new appropriate client for a new connection request.
 *  This interface works together with the <code>Server</code>
 *  class. The server waits for new connections and for each
 *  request it calls method <code>newClient</code>. This method
 *  must create appropriate meas that are cappable to serve this
 *  new connection.
 *
 *  @see Server
 *
 */
public interface IClientFactory
{

  /**
   *  Creates means which are cappable to serve given connection.
   *
   *  @throws IOException
   *             if something went wrong
   */
  void newClient(Socket socket) throws IOException;

}
