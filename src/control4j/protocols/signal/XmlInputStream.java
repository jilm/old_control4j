package control4j.protocols.signal;

/*
 *  Copyright 2015 Jiri Lidinsky
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

public class XmlInputStream extends InputStream
{

  private InputStream stream;

  private final int BUFFER_SIZE = 20;
  private final byte[] buffer = new byte[BUFFER_SIZE]; 
  private int length = 0;
  private boolean eof = false;
  private boolean xmlHeader = true;
  
  public XmlInputStream(InputStream stream)
  {
    this.stream = stream;
  }

  private void fillBuffer() throws IOException
  {
    if (eof) return;
    int available = stream.available();
    if (available > 0)
    {
      int empty = BUFFER_SIZE - length;
      int bytesToRead = Math.min(available, empty);
      int returnCode = stream.read(buffer, length, bytesToRead);
      if (returnCode < 0) 
	eof = true;
      else
        length += returnCode;
    }
    else if (length == 0 && available == 0)
    {
      int returnCode = stream.read();
      if (returnCode < 0)
	eof = true;
      else
      {
	buffer[0] = (byte)returnCode;
	length ++;
      }
    }
  }

  @Override
  public int read() throws IOException
  {
    while (length < 5)
    {
      if (eof && length < 5 && length > 0) return get();
      if (eof && length == 0) return -1;
      fillBuffer();
    }
    // test for new xml
    if (xmlHeader)
    {
      xmlHeader = false;
      return get();
    }
    else if (new String(buffer, 0, 5).equals("<?xml"))
    {
      xmlHeader = true;
      System.out.println("!!!");
      return -1;
    }
    else
      return get();
  }

  private byte get()
  {
    byte result = buffer[0];
    System.arraycopy(buffer, 1, buffer, 0, length-1);
    length --;
    System.out.print(Character.toChars(result));
    return result;
  }

  @Override
  public int available()
  {
    if (length < 5) return 0;
    for (int i=0; i<length-5; i++)
      if (new String(buffer, i, 5).equals("<?xml")) return i;
    return length-5;
  }

}
