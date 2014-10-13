package control4j.modules.history;

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

import java.util.Date;
import control4j.Signal;
import control4j.SignalFormat;
import control4j.EmptyVectorException;

/**
 *  Class to hold and provide limited history of a particular signal in 
 *  the form of vector signal.
 *
 *  The timestamp of this signal is automaticaly set to the system time
 *  at the moment, the last value was added.
 *
 *  This signal is always valid. 
 *
 *  WARNING: this object is not immutable !
 */
class HistorySignal extends Signal
{
  /** circular buffer where the history is stored */
  private double[] buffer;

  /** index into the buffer, index of the first free position */
  private int tail;

  /** number of elements in the buffer */
  private int length;

  /**
   *  @param capacity
   *             required size of the circular buffer.
   */
  protected HistorySignal(int capacity)
  {
    super(new Date());
    buffer = new double[capacity];
    tail = 0;
    length = 0;
  }

  /**
   *  Adds value of the given signal into the history buffer. The timestamp
   *  of the given signal is ignored, value will be added at the head (index
   *  zero) position. If the given signal is invalid, the nan value is used
   *  instead. The internal buffer is circular so if it is full, the oldest
   *  value will be overwrited by this one.
   *
   *  @param signal
   *             a signal that will be added into the history
   */
  void add(Signal signal)
  {
    double value = signal.isValid() ? signal.getValue() : Double.NaN;
    buffer[tail] = value;
    tail = (tail+1) % buffer.length;
    if (length < buffer.length) length++;
    timestamp = new Date();
  }

  /**
   *  Provides deep copy of this object.
   */
  @Override
  public Object clone()
  {
    HistorySignal clone = new HistorySignal(buffer.length);
    clone.buffer = new double[this.buffer.length];
    System.arraycopy(this.buffer, 0, clone.buffer, 0, this.buffer.length);
    clone.timestamp = this.timestamp;
    return clone;
  }

  /**
   *  Provides deep copy of this object.
   *
   *  @param timestamp
   *             this parameter is ignored
   */
  @Override
  public Signal clone(Date timestamp)
  {
    return (Signal)clone();
  }
  
  @Override
  public boolean equals(Object object)
  {
    if (object == null) return false;
    if (object instanceof Signal)
      return hasEqualValue((Signal)object);
    else
      return false;
  }

  @Override
  public int getSize()
  {
    return length;
  }

  /**
   *  Returns most recent value. Throws an exception if the internal buffer
   *  is empty.
   *
   *  @return most recent value
   */
  @Override
  public double getValue()
  {
    if (length > 0)
    {
      int index = tail > 0 ? tail-1 : buffer.length-1;
      return buffer[index];
    }
    else
    {
      throw new EmptyVectorException();
    }
  }

  @Override
  public double getValue(int index)
  {
    if (index < 0 || index > length-1)
      throw new IndexOutOfBoundsException();
    int i = tail - index - 1;
    if (i < 0) i = buffer.length + i;
    return buffer[i];
  }

  @Override
  public boolean hasEqualValue(Signal signal)
  {
    if (signal instanceof HistorySignal)
    {
      HistorySignal paramSignal = (HistorySignal)signal;
      if (paramSignal.length != this.length) return false;
      for (int i=0; i<this.length; i++)
      {
        if (this.getValue(i) != paramSignal.getValue(i))
	  return false;
      }
      return true;
    }
    else
      return false;
  }

  @Override
  public int hashCode()
  {
    int hash = super.hashCode();
    for (int i=0; i<length; i++)
      hash = hash ^ Double.valueOf(getValue(i)).hashCode();
    return hash;
  }

  /**
   *  This object is always valid.
   *
   *  @return true
   */
  @Override
  public boolean isValid()
  {
    return true;
  }

  @Override
  public String valueToString(SignalFormat format)
  {
    StringBuffer sb = new StringBuffer();
    sb.append('[');
    if (length > 0)
    {
      sb.append(format.format(getValue(0)));
      for (int i=1; i<length; i++)
      {
        sb.append("; ");
        sb.append(format.format(getValue(i)));
      }
    }
    sb.append(']');
    return sb.toString();
  }

}
