package control4j.modules.counters;

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
import control4j.ProcessModule;

/**
 *  An up counter; it increases the output by one as long as the input is true.
 */
public class PMUpCounter extends ProcessModule
{

  private long counter = 0;

  /**
   *  It increments once for every processing cycle in which the input is
   *  true. The counter can be reset.
   *
   *  <p>The module has two inputs. The first one enables counting and it
   *  is mandatory, whereas the second one is to reset counter and it is
   *  optional. Both of the inputs are interpreted to be boolean scalar
   *  values.
   *
   *  <p>The module provides one output. After the start of the application
   *  it is initialized to zero and it is increased by one every cycle,
   *  in which the enable input is valid and true. The output can be reset
   *  to zero by valid true value on reset input.
   *
   *  <p>The output is always valid and the timestamp is set to the
   *  current system time.
   *
   *  <p>The java datatype long is used internally for the counter.
   *
   *  @param input
   *             an array of size one or two. The second element can
   *             be <code>null</code> value. The element with zero index
   *             is enable input and the element with index one is reset
   *             input.
   *
   *  @return an array of size one. The returned signal is the output
   *             of the counter.
   */
  @Override
  protected Signal[] process(Signal[] input)
  {
    int inputSize = getNumberOfAssignedInputs();
    // enable input
    boolean enable = input[0].isValid() ? input[0].getBoolean() : false;
    // reset input
    boolean reset;
    if (inputSize >= 2 && input[1] != null && input[1].isValid())
      reset = input[1].getBoolean();
    else
      reset = false;
    // increase
    if (reset)
      counter = 0;
    else
      if (enable)
        counter++;
    input[0] = Signal.getSignal((double)counter);
    return input;
  }

}
