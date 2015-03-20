package control4j;

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

import control4j.application.Input;
import control4j.application.Output;

/**
 *  Represents processing module, it is a module that gets input, performs
 *  some calculation on it and provides output.
 */
public abstract class ProcessModule extends Module
{

  /**
   *  This method should implement the module functionality.
   */
  public abstract void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength);

  public void setInputConfiguration(int input,  IConfigBuffer configuration)
  { }

  public void setOutputConfiguration(int input, IConfigBuffer configuration)
  { }

  @Override
  public void dump(java.io.PrintWriter writer)
  {
    super.dump(writer);
  }

}
