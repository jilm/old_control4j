package control4j.protocols.tcp;

/*
 *  Copyright 2013 Jiri Lidinsky
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

/**
 *  Auxiliary interface which must be implemented by the protocol in order
 *  to use RobustTcpTemplate class.
 */
public interface IInputStream<T>
{
  
  /**
   *  Reades one message from the underlaying input stream and returns it.
   *
   *  @return a received message
   *
   *  @throws IOException if somethig went wrong
   */
  public T readMessage() throws IOException;
  
  /**
   *  Close the input stream and release all of the resources. It shoud
   *  close even the underlaying input stream.
   *
   *  @throws IOException if something went wrong
   */
  public void close() throws IOException;
  
}
