package control4j.modules.timers;

/*
 *  Copyright 2013 Jiri Lidinsky
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
import control4j.ProcessModule;

/**
 *  It realizes function: Timer On Delay
 */
public class PMTimerOnDelay extends ProcessModule
{

  /**
   *  Preset value for the timer. The unit is one processing cycle.
   */
  @ConfigItem
  public int delay;

  private int counter = 0;
  private boolean oldInputValue = false;

  /**
   *  It realizes Timer On Delay function. It means that when the input
   *  becams true the internal counter starts counting. After it reaches
   *  the preset value delay, the output is set to true. When the input
   *  becams false the internal counter is reset and the output is set
   *  to false.
   *
   *  <p>If the input signal is invalid, than the last valid value of
   *  this signal is used. If never valid, the false value is considered.
   *
   *  <p>Timestamp of the output signal is set to the current system time.
   *
   *  @param input
   *             an array of size one. The only element may not be null.
   *             The element is interpreted to be boolean signal.
   *
   *  @return an array of size one. The only element is boolean signal.
   */
  @Override
  protected Signal[] process(Signal[] input)
  {
    Signal[] result = new Signal[1];
    boolean inputValue;

    if (input[0].isValid())
      inputValue = input[0].getBoolean();
    else
      inputValue = oldInputValue;

    if (inputValue)
    {
      if (counter >= delay)
        result[0] = Signal.getSignal(true);
      else
      {
        result[0] = Signal.getSignal(false);
        counter++;
      }
    }
    else
    {
      counter = 0;
      result[0] = Signal.getSignal(false);
    }

    oldInputValue = inputValue;
    return result;
  }

}
