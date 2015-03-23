package control4j.modules.auxiliary;

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

import java.util.Calendar;
import control4j.Signal;
import control4j.OutputModule;

/**
 *
 *  Returns actual system time.
 *
 */
public class OMClock extends OutputModule
{

  /**
   *  Returns actual local system time. It returns an array of size
   *  six with following meaning:
   *  <ol>
   *    <li value="0"> second
   *    <li> minute
   *    <li> hour
   *    <li> day
   *    <li> month
   *    <li> year
   *  </ol>
   *
   *  @return an array of size six. Returned signals have meaning
   *          which is mentioned above. Signals are always valid
   *          with timestamp set to actual system time.
   */
  @Override
  public void get(Signal[] output, int outputLength)
  {
    Calendar calendar = Calendar.getInstance();
    out(0, Signal.getSignal((double)calendar.get(Calendar.SECOND)), 
	output, outputLength);
    out(1, Signal.getSignal((double)calendar.get(Calendar.MINUTE)),
	output, outputLength);
    out(2, Signal.getSignal((double)calendar.get(Calendar.HOUR_OF_DAY)),
	output, outputLength);
    out(3, Signal.getSignal((double)calendar.get(Calendar.DATE)),
	output, outputLength);
    out(4, Signal.getSignal((double)calendar.get(Calendar.MONTH) + 1),
	output, outputLength);
    out(5, Signal.getSignal((double)calendar.get(Calendar.YEAR)),
	output, outputLength);
  }

  private void out(int index, Signal signal, Signal[] output, int outputLength)
  {
    if (index < outputLength)
      output[index] = signal;
  }

}
