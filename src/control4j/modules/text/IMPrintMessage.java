package control4j.modules.text;

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
import control4j.AResource;
import control4j.ConfigItem;
import control4j.InputModule;
import control4j.IConfigBuffer;
import control4j.resources.ITextWriter;

/**
 *  Prints a preset text message on the given text device.
 *  Print can be controlled by the input signal.
 */
public class IMPrintMessage extends InputModule
{
  /**
   *  Text device, the message will be printed on.
   */
  @AResource(key="text-device")
  public ITextWriter textDevice;

  /**
   *  Message that will be printed on the given text device.
   */
  @ConfigItem 
  public String message;

  /**
   *  Prints a preset text message on the given text device.
   *  The text is printed in every cycle in which the input
   *  with zero index is valid and interpreted as boolean
   *  <code>true</code>.
   *
   *  @param input
   *             must be an array of size one. An array
   *             element may not be <code>null</code>. The
   *             text message is printed in every cycle in
   *             which this element is valid and interpreted
   *             as <code>true</code>.
   */
  @Override
  public void put(Signal[] input, int inputLength)
  {
    if (input[0].isValid() && input[0].getBoolean())
      textDevice.println(message);
  }
}
