package control4j.modules.auxiliary;

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
 *  Tests wheather the input signal is valid; if so, it returns true,
 *  and false otherwise.
 */
public class PMIsValid extends ProcessModule
{

  /**
   *  It returns a boolean true signal if input signal is valid and
   *  returns a boolean false signal otherwise.
   *
   *  <p>It takes at least one input. Number of outputs is the same
   *  as number of inputs. The output with index i express validity
   *  of the input with index i.
   *
   *  <p>Output signals are always valid. Timestamp of the output signal
   *  will be the same as timestamp of the coresponding input signal.
   *
   *  @param input
   *             an array of size at least one. May not contain
   *             <code>null</code> values
   *
   *  @return an array of the same size as the input array
   */
  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid())
      output[0] = Signal.getSignal(true, input[0].getTimestamp());
    else
      output[0] = Signal.getSignal(false, input[0].getTimestamp());
  }

}
