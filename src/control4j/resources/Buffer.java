package control4j.resources;

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

public class Buffer
{
  private int[] buffer;
  private int length;
  
  public Buffer(int length)
  {
    this.buffer = new int[length];
    this.length = 0;
  }
  
  public int length()
  {
    return this.length;
  } 
  
  public int[] getBuffer()
  {
    return buffer;
  }
  
  public int get(int index)
  {
    return buffer[index];
  }
  
  public void remove(int offset, int length)
  {
    System.arraycopy(buffer, offset+length, buffer, offset, this.length-offset-length);
    this.length -= length;
  }
  
  public void add(byte[] buffer, int offset, int length)
  {
    for (int i=0; i<length; i++)
      this.buffer[this.length+i] = (int)buffer[offset+i] & 0xff;  
    this.length += length;
  }
}
