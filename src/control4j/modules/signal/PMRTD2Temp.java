package control4j.modules.signal;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import control4j.ProcessModule;
import control4j.Signal;
import control4j.AMaxInput;
import control4j.AMinInput;

@AMaxInput(1) @AMinInput(1)
public class PMRTD2Temp extends ProcessModule {

  public static final double A = 3.9093e-3d;
  public static final double B = -5.775e-7d;
  public static final double C = -4.183e-12d;

  @Override
  public void process(Signal[] input, int inputLength,
      Signal[] output, int outputLength) {

    if (input[0].isValid()) {
      double r = input[0].getValue();
      double temp = (-A + Math.sqrt(A*A - 4*B*(100d - 0.01d*r))) * 0.5d / B;
      output[0] = Signal.getSignal(temp, input[0].getTimestamp());
    } else {
      output[0] = Signal.getSignal(input[0].getTimestamp());
    }

  }

}
