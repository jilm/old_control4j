package control4j.modules.auxiliary;

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
import control4j.Module;
import control4j.Signal;
import control4j.ProcessModule;
import java.util.Date;

/**
 *  Copy timestamp.
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
@AMinInput(2)
@AMaxInput(2)
public class PMCopyTimestamp extends ProcessModule
{

  public void process(
      Signal[] input, int intputLength, Signal[] output, int outputLength)
  {
    Date reference = input[0].getTimestamp();
    if (outputLength > 0)
      output[0] = input[1].clone(reference);
  }

}
