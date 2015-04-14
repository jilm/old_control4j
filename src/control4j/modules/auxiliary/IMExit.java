package control4j.modules.auxiliary;

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

import control4j.AMaxInput;
import control4j.Signal;
import control4j.Control;
import control4j.ConfigItem;
import control4j.InputModule;
import static control4j.tools.Logger.info;

/**
 *  Terminates the application as soon as the input signal is true.
 */
@AMaxInput(1)
public class IMExit extends InputModule
{
  /**
   *  The text that will be written to the std application log
   *  before the application will be terminated.
   */
  @ConfigItem(optional=true)
  public String message = "The application will be terminated.";

  /**
   *  Terminates the application as soon as the input signal is true.
   *  
   *  <p>Application is not terminated immediately. It is terminated
   *  after the current loop is finished. Even cycleEnd event is
   *  performed.
   *
   *  <p>This method accepts only one input signal. The input signal
   *  is interpreted as a boolean value. The termination of the 
   *  application is scheduled to the end of the loop at the moment 
   *  when the input signal is valid and has true value.
   *
   *  <p>The input may be omitted. The input array is empty, or it
   *  contains <code>null</code>. In such a case only one control
   *  loop is performed and the application is terminated.
   *  
   *  <p>The event of application termination is loged to the standard
   *  application log with the level info. Text of the log message
   *  may be specified in the message variable.
   *
   *  @param input
   *             an array of size one or an empty array. It may
   *             contain <code>null</code> value. If the size of the
   *             array is greater than one, redundant elements are
   *             just ignored.
   */
  @Override
  public void put(Signal[] input, int inputLength)
  {

    boolean condition;
    if (inputLength == 0 || input[0] == null)
      condition = true;
    else if (input[0].isValid() && input[0].getBoolean())
      condition = true;
    else
      condition = false;

    if (condition)
    {
      info(message);
      Control.exit();
    }
  }

}
