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

import control4j.AVariableInput;
import control4j.Signal;
import control4j.ProcessModule;

@AVariableInput
public class PMFirstValid extends ProcessModule {

  @Override
  public void process(Signal[] input, int inputLength,
        Signal[] output, int outputLength) {

    for (int i = 0; i < inputLength; i++) {
      if (input[i].isValid()) {
        output[0] = input[i];
        return;
      }
    }
    output[0] = Signal.getSignal();

  }

}
