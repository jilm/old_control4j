package control4j.modules;

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

import control4j.AMinInput;
import control4j.AMaxInput;
import control4j.Signal;
import control4j.ConfigItem;
import control4j.ProcessModule;

/**
 *  Implements functionality of the comparator.  It compares values on input
 *  and returns a boolean value that indicates relationship between them. More
 *  precisely, it expects two input signals. First of all the difference
 *  between these two is computed: diff = input[1] - input[0]. The output
 *  signal will be false if diff &le; -hysteresis, the output will be true if
 *  diff &ge; hysteresis, and the output will stay unchanged otherwise.
 *
 *  Property: hysteresis, default 0, the positive real number is expected.
 *
 *  Input: 0, scalar real number, the reference input
 *
 *  Input: 1, scalar real number, the input to compare
 *
 *  Output: 0, scalar boolean value, if at least one of input signals is
 *  invalid, the output is invalid, otherwise, the output is valid. The
 *  timestamp of the signal will be equal to the timestamp of input one.
 */
@AMinInput(2)
@AMaxInput(2)
public class PMComparator extends ProcessModule
{
  @ConfigItem(optional=true)
  public double hysteresis = 0;

  private boolean oldValue = false;

  /**
   *  Compare input signals and returned value indicates
   *  result of the comparison. It expects two input signals.
   *  It computes the difference between them: diff = input[1] - input[0].
   *  The output signal is false if diff <= -hysteresis, the output
   *  is true if diff >= hysteresis, and the output signal
   *  stays unchanged otherwise.
   *
   *  @param input
   *             must contain two Signal objects
   *
   *  @return an array of size one. Returned signal is valid
   *             if and only if both of the input signals are
   *             valid. Timestamp of the returned signal is set
   *             to the system time.
   */
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid() && input[1].isValid())
    {
      double diff = input[1].getValue() - input[0].getValue();
      boolean value;
      if (diff >= hysteresis)
        value = true;
      else if (diff <= -hysteresis)
        value = false;
      else
        value = oldValue;
      oldValue = value;
      output[0] = Signal.getSignal(value);
    }
    else
    {
      output[0] = Signal.getSignal();
    }
  }
}
