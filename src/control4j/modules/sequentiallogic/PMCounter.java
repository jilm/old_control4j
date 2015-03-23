package control4j.modules.sequentiallogic;

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

import control4j.Signal;
import control4j.ConfigItem;
import control4j.IConfigBuffer;
import control4j.ProcessModule;

/**
 *  Each cycle it increases or decreases its output as long as the input
 *  is true.
 *
 *  @deprecated
 */
public class PMCounter extends ProcessModule
{
  @ConfigItem(key="low-limit", optional=true)
  public int lowLimit = 0;

  @ConfigItem(key="high-limit", optional=true)
  public int highLimit = 0xffff;

  private int count;

  /**
   */
  @Override 
  public void initialize(IConfigBuffer configuration)
  {
    count = lowLimit;
  }

  /**
   *  Each processing cycle it increases or decreases its output by one as
   *  long as the input is true.
   *
   *  <p>In fact it takes three inputs. First one is required and its function
   *  is to enable or disable counting. Second one is optional and it
   *  determines the direction of counting. The third one is optional
   *  and it is reset input.
   *
   *  <p>Each processing cycle the count output is increased or decreased 
   *  by one as long as the enable input is valid and its value may be 
   *  interpreted as boolean true. If enable input is false or invalid
   *  the counting is stopped.
   *  
   */
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    // count direction: false: up, true: down
    boolean direction = false;
    if (inputLength > 1 && input[1] != null)
    {
      if (input[1].isValid())
        direction = input[1].getBoolean();
      else
      {
	output[0] = Signal.getSignal(count);
        return;
      }
    }
    // reset signal
    boolean reset = false;
    if (inputLength > 2 && input[2] != null && input[2].isValid() 
	&& input[2].getBoolean())
    {
      count = direction ? highLimit : lowLimit;
      output[0] = Signal.getSignal(count);
      return;
    }
    // count input
    if (input[0].isValid() && input[0].getBoolean())
    {
      count = direction ? count - 1 : count + 1;
    }
    output[0] = Signal.getSignal(count);
  }

}
