package control4j.modules;

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

import java.util.Date;
import control4j.AMinInput;
import control4j.AMaxInput;
import control4j.ProcessModule;
import control4j.Signal;

/**
 *
 *  Measure an operation time of some device.
 *  It expects two scalar input signals which are interpreted as a
 *  boolean signals. First one is for signalization of the operation,
 *  the module in fact measure a duration for which the signal is
 *  in high state. The second one is the reset signal.
 *
 */
@AMinInput(1)
@AMaxInput(2)
public class PMOperationTime extends ProcessModule
{

  private long sum;
  private Date begin;
  private long diff;

  @Override
  public void prepare()
  {
    sum = 0l;
    begin = null;
    diff = 0l;
  }

  @Override
  public void process(
      Signal[] input, int inputLength, Signal[] output, int outputLength)
  {
    if (inputLength >= 2 && outputLength >= 1)
    {
      // reset signal
      boolean reset = input[0].isValid() && input[0].getBoolean();

      if (reset)
      {
        sum = 0l;
        diff = 0l;
        if (input[1].isValid() && input[1].getBoolean())
          begin = input[0].getTimestamp();
        else
          begin = null;
      }
      else
      {
        Date now = new Date();
        if (begin == null && input[1].isValid() && input[1].getBoolean())
        {
          begin = input[1].getTimestamp();
          diff = 0l;
        }
        else if (begin != null && input[1].isValid() && !input[1].getBoolean())
        {
          diff = input[1].getTimestamp().getTime() - begin.getTime();
          sum += diff;
          begin = null;
          diff = 0l;
        }
        else if (begin != null)
        {
          diff = now.getTime() - begin.getTime();
        }
      }
    }
    output[0] = Signal.getSignal((double)((sum + diff) / 1000l));
    output[0].setUnit("sec");
  }

}
