package control4j.modules.auxiliary;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import control4j.OutputModule;
import control4j.Signal;

/**
 *
 *  A module which provides invalid signal. It has only one output, there is
 *  invalid signal on it. It is useful mainly for debuging purposes.
 *
 */
public class OMInvalidSignal extends OutputModule
{

  /**
   *  Provides an array of size one and there is invalid signal on it.
   *
   *  @return an array of size one, there is invalid signal in it
   */
  @Override
  protected void get(Signal[] output, int outputLength)
  {
    if (outputLength > 0)
      output[0] = Signal.getSignal();
  }

}
