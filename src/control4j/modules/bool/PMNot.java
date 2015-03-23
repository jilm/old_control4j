package control4j.modules.bool;

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

import control4j.Module;
import control4j.Signal;
import control4j.ProcessModule;

/**
 *  Perform logical NOT operation on input.
 */
public class PMNot extends ProcessModule
{

  /**
   *  Perform logical NOT operation on input. It expects one or more
   *  input signal. For each input signal provides one corresponding
   *  otput signal which value is logical NOT on input. The output
   *  signal is invalid if and only if corresponging input signal is
   *  invalid. Timestamp of output signal is copy of timestamp of
   *  correspoing input signal.
   *
   *  @param input an array of size one or more. It shall not contain
   *         null value. Signals are treated to be boolean values.
   *  @return an array of size equal to input array size. Value of
   *         signal on each index is logical NOT of signal on input
   *         with the same index. Signal is invalid if and only if
   *         input signal with the same index is invalid. Timestamp
   *         is identical with the corresponding input signal.
   */
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (input[0].isValid())
      output[0] = Signal.getSignal(
	  !input[0].getBoolean(), input[0].getTimestamp());
    else
      output[0] = Signal.getSignal(input[0].getTimestamp());
  }
}
