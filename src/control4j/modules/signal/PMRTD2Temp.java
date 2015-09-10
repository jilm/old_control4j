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

/**
 *  Link: http://www.mosaic-industries.com/embedded-systems/microcontroller-projects/temperature-measurement/platinum-rtd-sensors/resistance-calibration-table
 */
@AMaxInput(1) @AMinInput(1)
public class PMRTD2Temp extends ProcessModule {

  public static final double c0 = -245.19d;
  public static final double c1 = 2.5293d;
  public static final double c2 = -0.066046d;
  public static final double c3 = 4.0422e-3d;
  public static final double c4 = -2.0697e-6d;
  public static final double c5 = -0.025422d;
  public static final double c6 = 1.6883e-3d;
  public static final double c7 = -1.3601e-6d;

  @Override
  public void initialize(control4j.application.Module moduleDef) {
    super.initialize(moduleDef);
    // set the unit of the output signal to celsius degree
    moduleDef.getOutput().get(0).getSignal().setUnit('\u00B0' + "C");
  }

  @Override
  public void process(Signal[] input, int inputLength,
      Signal[] output, int outputLength) {

    if (input[0].isValid()) {
      double r = input[0].getValue();
      double temp = c0 + (r * (c1 + r*(c2 + r*(c3 + r*c4))))
          / (1 + r*(c5 + r*(c6 + r*c7)));
      output[0] = Signal.getSignal(temp, input[0].getTimestamp());
    } else {
      output[0] = Signal.getSignal(input[0].getTimestamp());
    }
  }

}
