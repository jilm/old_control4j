package control4j.modules;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
import control4j.ProcessModule;
import control4j.Signal;
import control4j.IConfigBuffer;

/**
 *  Measure an operation time of some device.
 *  It expects two scalar input signals which are interpreted as a
 *  boolean signals. First one is for signalization of the operation,
 *  the module in fact measure a duration for which the signal is
 *  in high state. The second one is the reset signal.
 */
public class PMOperationTime extends ProcessModule
{
  private long[] sum;
  private Date[] begin;
  private long[] diff;

  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    int size = getNumberOfAssignedOutputs();
    sum = new long[size];
    begin = new Date[size];
    diff = new long[size];
    for (int i=0; i<size; i++)
    {
      sum[i] = 0l;
      begin[i] = null;
      diff[i] = 0l;
    }
  }

  @Override
  public Signal[] process(Signal[] input)
  {
    if (input[0].isValid() && input[0].getBoolean())
    {
      for (int i=0; i<sum.length; i++)
      {
        sum[i] = 0l;
	diff[i] = 0l;
	if (input[i+1].isValid() && input[i+1].getBoolean())
          begin[i] = input[0].getTimestamp();
        else
	  begin[i] = null;
      }
    }
    else
    {
      Date now = new Date();
      for (int i=0; i<sum.length; i++)
      {
        if (begin[i] == null && input[i+1].isValid() && input[i+1].getBoolean())
	{
	  begin[i] = input[i+1].getTimestamp();
	  diff[i] = 0l;
	}
        else if (begin[i] != null && input[i+1].isValid() && !input[i+1].getBoolean())
	{
	  diff[i] = input[i+1].getTimestamp().getTime() - begin[i].getTime();
	  sum[i] += diff[i];
	  begin[i] = null;
	  diff[i] = 0l;
	}
	else if (begin[i] != null)
	{
	  diff[i] = now.getTime() - begin[i].getTime();
	}
      }
    }
    Signal[] result = new Signal[sum.length];
    for (int i=0; i<result.length; i++)
    {
      result[i] = Signal.getSignal((double)((sum[i] + diff[i]) / 1000l));
      result[i].setUnit("sec");
    }
    return result;
  }
}
