package control4j.modules.system;

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

import control4j.AMaxInput;
import control4j.AMinInput;
import control4j.AOutputSize;
import control4j.AVariableOutput;
import control4j.AVariableInput;
import control4j.ProcessModule;
import control4j.Signal;

@AMinInput(1)
@AMaxInput(1)
@AVariableOutput
public class PMLDAdapter extends ProcessModule {

  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength) {

    for (int i = 0; i < outputLength; i++) {
      output[i] = (Signal)input[0].clone();
    }
  }

}
