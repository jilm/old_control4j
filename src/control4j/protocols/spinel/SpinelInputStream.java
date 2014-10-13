package control4j.protocols.spinel;

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

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import control4j.protocols.tcp.IInputStream;

/**
 *  Input stream which reads data in spinel format.
 */
public class SpinelInputStream 
extends BufferedInputStream 
implements IInputStream<SpinelMessage>
{
  private int[] buffer = new int[255];

  /**
   *  Create and initialize new input stream.
   *
   *  @param inputStream
   *             underlying input stream
   */
  public SpinelInputStream(InputStream inputStream)
  {
    super(inputStream, 255);
  }

  /**
   *  Reades and returns one spinel message from underlying
   *  input stream. This method blocks.
   *
   *  @return received spinel message
   *
   *  @throws IOException
   *              if something went wrong
   */
  public SpinelMessage readMessage() throws IOException
  {
    mark(200);
    // receive a header of the message
    for (int i=0; i<4; i++)
    {
      buffer[i] = read();
    } 
    int num = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
    // receive the rest of the message
    for (int i=4; i<num+4; i++)
    {
      buffer[i] = read();
    }
    try
    {
      return new SpinelMessage(buffer, 0, num+4);
    }
    catch (SpinelException e)
    {
      reset();
      skip(1);
      throw e;
    }
  } 

}
