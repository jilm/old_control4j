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

import control4j.Signal;
import control4j.ConfigItem;
import control4j.IConfigBuffer;

public class SignalBuffer extends Resource
{
  @ConfigItem(optional=false)
  public int size;

  private Signal[] buffer;	// circular buffer
  private int length;		// number of signals in the buffer
  private int first;		// index of the first signal in the buffer

  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    buffer = new Signal[size];
    length = 0;
    first = 0;
  }

  /**
   *  Put the argument at the last position in the buffer. If the buffer is
   *  full, the signal at the first position is descarded.
   *
   *  @param signal object to store in the buffer
   */
  public void put(Signal signal)
  {
    int index = (first + length) % buffer.length;
    buffer[index] = signal;
    if (length == buffer.length)
      first++;
    else
      length++;
  }

  /**
   *  Return number of signals that are stored in the buffer.
   *
   *  @return number of signals in the buffer
   */
  public int getLength()
  {
    return length;
  }

  /**
   *  Return signal which was stored in the position index. Indexes are zero
   *  based.
   *
   *  @param index index of desired signal
   *  @return signal which is stored in the position <code>index</code>
   *  @throws IndexOutOfBoundsExeption if the <code>index</code> is negative
   *          number or if it is greater than number of signal actually stored
   *          in the buffer.
   */
  public Signal get(int index)
  {
    if (index < 0 || index >= length)
      throw new IndexOutOfBoundsException();
    int bufferIndex = (first + index) % buffer.length;
    return buffer[bufferIndex];
  }
}
