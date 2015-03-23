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

import java.util.Calendar;
import control4j.Signal;
import control4j.OutputModule;
import control4j.IConfigBuffer;

/**
 *  A clock that produce impulses (ticks) at some predefined
 *  periods. It may be used to trigger periodic tasks.
 */
public class OMTickingClock extends OutputModule
{
  private int[] oldTime = new int[6];
  private int[] currentTime = new int[6];

  /**
   *  Initialize internal structures.
   *
   *  @param configuration
   *             not used, may be null
   */
  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    super.initialize(configuration);
    Calendar calendar = Calendar.getInstance();
    fillTimeValues(calendar, oldTime);
  }

  /**
   *  Produces inpulses with predetermined periods. It returns
   *  an array of size six. Returned signals are interpreted
   *  as boolean values. Duration of each tick is only one cycle.
   *  Periods are as follows:
   *  <ol>
   *    <li value="0"> ones a second
   *    <li> ones a minute
   *    <li> ones an hour
   *    <li> ones a day
   *    <li> ones a month
   *    <li> ones a year
   *  </ol>
   *
   *  @return an array of size six. Signals are invalid only
   *          during the first cycle after the application was
   *          started. Timestamp of the returned signal is set
   *          to the current system time.
   */
  @Override
  public void get(Signal[] output, int outputLength)
  {
    Calendar calendar = Calendar.getInstance();
    fillTimeValues(calendar, currentTime);
    boolean change = false;
    for (int i=currentTime.length-1; i>=0; i--)
    {
      if (oldTime[i] != currentTime[i]) change = true;
      if (outputLength > i)
        output[i] = Signal.getSignal(change);
      oldTime[i] = currentTime[i];
    }
  }

  private void fillTimeValues(Calendar calendar, int[] timeValues)
  {
    timeValues[0] = calendar.get(Calendar.SECOND);
    timeValues[1] = calendar.get(Calendar.MINUTE);
    timeValues[2] = calendar.get(Calendar.HOUR_OF_DAY);
    timeValues[3] = calendar.get(Calendar.DATE);
    timeValues[4] = calendar.get(Calendar.MONTH);
    timeValues[5] = calendar.get(Calendar.YEAR);
  }
}
