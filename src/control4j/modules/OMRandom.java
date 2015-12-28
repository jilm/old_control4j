package control4j.modules;

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

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.reflect.Setter;

import control4j.Signal;
import control4j.OutputModule;

/**
 *  Output module, which returns a random value from range 0.0 to 1.0. Returned
 *  values have uniform distribution.
 *
 *  Output: 0, Scalar random value from rande 0.0 to 1.0.
 *             It is always valid and with the actual timestamp.
 */
public class OMRandom extends OutputModule implements IToStringBuildable {

  @Override
  public void get(Signal[] output, int outputLength) {
     output[0] = Signal.getSignal(Math.random());
  }

  @Override
  public String toString() {
    return new ToStringBuilder()
        .append(this)
        .toString();
  }

  public void toString(ToStringBuilder builder) {
  }

}
