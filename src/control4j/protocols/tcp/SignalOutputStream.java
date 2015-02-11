package control4j.protocols.tcp;

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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import control4j.Signal;

/**
 */
public class SignalOutputStream extends ObjectOutputStream
implements IOutputStream<Signal[]>
{

  public SignalOutputStream(OutputStream outputStream) throws IOException
  {
    super(outputStream);
  }

  /**
   *  Send given message via the underlaying output stream.
   *
   *  @param message
   *             message to be sent
   *
   *  @throws IOException
   *             if somethig wend wrong
   */
  public void write(Signal[] message) throws IOException
  {
    reset();
    writeUnshared(message);
  }

}
