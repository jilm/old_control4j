package control4j.modules;

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

import control4j.AMinInput;
import control4j.AMaxInput;
import control4j.Module;
import control4j.Signal;
import control4j.ProcessModule;
import control4j.IConfigBuffer;

/**
 *
 *  Indicates whether the input signal has changed since 
 *  the last control loop.
 *
 */
@AMinInput(1)
@AMaxInput(1)
public class PMHasChanged extends ProcessModule
{

  private Signal old;

  /**
   *  Indicates whether the input signal has changed since
   *  the last control loop.
   *
   *  <p>It requires at lest one input signal. It provides
   *  one output signal for each input signal. The output
   *  is true if correspondig input signal has changed since
   *  the last call of this method, eg. since the last control
   *  loop. The output is false otherwise.
   *
   *  <p>The output is allways valid and the timestamp is set
   *  to the current system time.
   *
   *  <p>For change detection the {@link control4j.Signal#equals}
   *  method is used.
   *
   *  @param input
   *             must be an array of size at least one. Must not
   *             contain <code>null</code> values.
   *
   *  @return an array of the same size as the input array.
   *
   *  @see control4j.Signal#equals
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (inputLength > 0 && outputLength > 0)
    {
      if (old == null)
      {
        output[0] = Signal.getSignal();
      }
      else
      {
        boolean result = !input[0].equals(old);
        old = input[0];
        input[0] = Signal.getSignal(result);
      }
    }
  }

}
