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

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import control4j.Signal;
import static control4j.tools.Logger.*;

/**
 */
public class SignalInputStream extends ObjectInputStream
implements IInputStream<Signal[]>
{
  
  public SignalInputStream(InputStream inputStream) throws IOException
  {
    super(inputStream);
  }

  /**
   *  Reades one message from the underlaying input stream and returns it.
   *
   *  @return a received message
   *
   *  @throws IOException if somethig went wrong
   */
  public Signal[] readMessage() throws IOException
  {
    try
    {
      return (Signal[])readUnshared();
    }
    catch (ClassNotFoundException e)
    {
      catched(getClass().getName(), "readMessage", e);
      return null; // TODO
    }
  }
  
}
