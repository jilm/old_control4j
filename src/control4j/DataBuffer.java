package control4j;

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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  In principle, the DataBuffer is just an array of signals, which
 *  are identified by zero based index.
 *     
 *  <p>Serves as a data exchange point between computation modules.
 *  There are two fundamental methods in this class. Call {@link #get} 
 *  method to obtain an array of signals which are needed as an 
 *  input for the module. Call {@link #put} method after a module is 
 *  finished to store the outputs of the module for the future use.
 *
 *  <p>Methods <code>put</code> and <code>get</code> needs an extra
 *  parameter which maps index of input or output of the particular
 *  module into the index of the <code>DataBuffer</code> array.
 *
 *  <p>For the purpose of signals exchange between modules and this
 *  data buffer the only array is used over and over again. This array
 *  is big enough to contain data for a module with the bigest number
 *  of input signals. The relevant data are filled in into the lowes
 *  part of the array and the extra elements are left behind. Therefore,
 *  the module should not relay on the size of the array it gets as
 *  an input!
 *
 *  <p>At the beginnig of each cycle the DataBuffer should be erased 
 *  so that at the beginning of each cycle, the DataBuffer is empty.
 */    
class DataBuffer implements Iterable<Signal>
{

  private Signal[] buffer;
  private Signal[] crate;
  
  /**
   *  Alocates the internal store with the given size.
   *
   *  @param size
   *             required capacity of the buffer
   */
  public DataBuffer(int size)
  {
    this.buffer = new Signal[size];
    this.crate = new Signal[10];
  }
  
  /**
   *  Erase all of the values from the buffer. This method should be
   *  called before each computation cycle. After it is called, the
   *  buffer is empty.      
   */        
  public void clear()
  {
    for (int i=0; i<buffer.length; i++)
      buffer[i] = null;
  }
  
  /**
   *  Returns requested signals. Purpose of this
   *  method is to obtain input signals for the
   *  module to be executed.
   *
   *  @return an array of signals whose indexes
   *             are given as parameter. The size of
   *             the array may be greater than the size of parameter 
   *             requestedSignals.
   *
   *  @param requestedSignals must be an array of
   *             indexes of signals that are to be returned.
   *             If some position is unutilized, it should
   *             contain -1.
   *
   *  @throws IndexOutOfBoundsException if requestedSignals array
   *             contains index which is out of bounds of the internal
   *             buffer.
   */        
  public Signal[] get(int[] requestedSignals)
  {
    int length = requestedSignals.length;
    if (length > crate.length) crate = new Signal[length];
    for (int i=0; i<length; i++)
    {
      if (requestedSignals[i] >= 0)
        crate[i] = buffer[requestedSignals[i]];
      else
        crate[i] = null;
    }
    return crate;
  }
  
  /**
   *  Stores signals into the buffer. Signals to store are passed
   *  as an argument. 
   *  Purspose of this method is to store signals produced as the
   *  output of a module. Not all of the signals are to be stored.
   *
   *  @param signals an array of signals that are be stored
   *             in the data buffer.
   *
   *  @param map an array of indexes that identify the signal.
   *             Indexes specify where to store corresponding
   *             signal. If the corresponding signal is not to 
   *             be stored, the index must be -1. The map array 
   *             may be smaller than the signals array. Extra
   *             signals are silently ignored.
   *
   *  @throws IndexOutOfBoundsException if some index in the map
   *             array is greater than internal buffer size
   *
   *  @throws NullPointerException if some signal in signals array
   *             contain null value and the corresponding index in
   *             the map array is positive
   */     
  public void put(Signal[] signals, int[] map)
  {
    for (int i=0; i<map.length; i++)
      if (map[i] >= 0)
      {
        buffer[map[i]] = signals[i];
      }
  }

  /**
   *  Return size of internal buffer.
   *
   *  @return size of internal buffer
   */
  public int size()
  {
    return buffer.length;
  }

  public Iterator<Signal> iterator()
  {
    return new SignalIterator();
  }

  /**
   *  Writes a content of the data buffer to the given print writer.
   *  serves for debug purposes.
   *
   *  @param writer
   *             a writer where the content of the buffer will be printed
   */
  void dump(java.io.PrintWriter writer)
  {
    writer.println("DATA BUFFER CONTENT:");
    for (int i=0; i<buffer.length; i++)
    {
      writer.println(" " + i + ": " + buffer[i].toString());
    }
  }
  
  /*
   *
   *   Iterator class implementation
   *   
   */         

  class SignalIterator implements Iterator<Signal>
  {
    int index = 0;

    public boolean hasNext()
    {
      return index < buffer.length;
    }

    public Signal next()
    {
      if (index < buffer.length)
      {
	      index++;
	      return buffer[index-1];
      }
      throw new NoSuchElementException();
    }

    public void remove()
    {
    }
  }

}
