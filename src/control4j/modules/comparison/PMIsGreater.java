package control4j.modules.comparison;

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

import control4j.Signal;
import control4j.ProcessModule;

/**
 *  TODO:
 *
 *  <p>
 *  </p>
 *
 *  <h3>Resources</h3>
 *  <table>
 *      <tr>
 *          <td>table-name</td>
 *          <td></td>
 *          <td>A name of the table to insert to. The string value is
 *          expected.</td>
 *      </tr>
 *  </table>
 *
 *  <h3>Property</h3>
 *  <table>
 *      <tr>
 *          <td>message</td>
 *          <td>The message which will be written into the log.</td>
 *      </tr>
 *  </table>
 *
 *  <h3>IO</h3>
 *  <table>
 *      <tr>
 *          <td>Input</td>
 *          <td>0</td>
 *          <td>The control input; it expects scalar boolean signal. The
 *          application exits after the value on this input becomes valid
 *          true.</td>
 *      </tr>
 *  </table>
 */
public class PMIsGreater extends ProcessModule
{

  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid())
    {
      double reference = input[0].getValue();
      if (input[1].isValid())
	output[0] = Signal.getSignal(reference < input[1].getValue());
      else
        output[0] = Signal.getSignal();
    }
    else
      output[0] = Signal.getSignal();
  }

}
