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

import java.util.logging.Level;
import java.util.logging.Logger;

import control4j.Signal;
import control4j.Resource;
import control4j.ConfigItem;
import control4j.InputModule;
import control4j.IConfigBuffer;
import control4j.resources.ITextWriter;

/**
 *  Prints a preset text message to the log. Print is controlled
 *  by the input signal. This functionality is intended to log 
 *  abnormal and limit events rather than for regular listings.
 *
 *  <p>As a background the standard java logger is used. You can
 *  choose the logger and logging level.
 *
 *  @see java.util.logging.Logger
 */
public class IMLogMessage extends InputModule
{

  /**
   *  Message that will be logged.
   */
  @ConfigItem 
  public String message;

  /**
   *  Name of the logger. If not assigned, the standard application
   *  log is used.
   */
  @ConfigItem(key="logger", optional=true)
  public String loggerName = null;

  /**
   *  Level of the logged message as lower-case string.
   *  e.g.: info, warning, severe, ...
   *  If you assign an unknown level string, the info level
   *  will be used. Default value is info.
   *
   *  @see java.util.logging.Level
   */
  @ConfigItem(optional=true)
  public String level = "info";

  private Level logLevel;
  private Logger logger;
  private boolean lastTriggerInput = false;  // helps to detect the rising edge

  /**
   *  Initialize the logger. If the logger with given name have not
   *  existed yet, it is created. If the given log level is not a
   *  valid log level name, the info level is silently used instead.
   *
   *  @param configuration
   *             not used and may be <code>null</code>
   */
  @Override
  protected void initialize(IConfigBuffer configuration)
  {
    // initialize log level
    if (level.equals("config"))
      logLevel = Level.CONFIG;
    else if (level.equals("fine"))
      logLevel = Level.FINE;
    else if (level.equals("finer"))
      logLevel = Level.FINER;
    else if (level.equals("finest"))
      logLevel = Level.FINEST;
    else if (level.equals("info"))
      logLevel = Level.INFO;
    else if (level.equals("severe"))
      logLevel = Level.SEVERE;
    else if (level.equals("warning"))
      logLevel = Level.WARNING;
    else
      // unsupported log level
      logLevel = Level.INFO;

    // initialize the logger
    if (loggerName == null)
      logger = control4j.tools.Logger.getLogger();
    else
      logger = Logger.getLogger(loggerName);
  }

  /**
   *  Prints a preset text message to the log with given name.
   *  The logging is triggered by the risig edge on the
   *  input signal with zero index.
   *
   *  @param input
   *             must be an array of size one. An array
   *             element with zero index may not be <code>null</code>. 
   *             The input signal is interpreted as boolean and
   *             the logging function is triggered by the rising edge 
   *             on this signal.
   */
  @Override
  public void put(Signal[] input, int inputLength)
  {
    // detect rising edge on input signal
    boolean trigger = false;
    if (input[0].isValid())
    {
      boolean triggerInput = input[0].getBoolean();
      trigger = !lastTriggerInput && triggerInput;
      lastTriggerInput = triggerInput;
    }
    // log message
    if (trigger)
    {
      logger.logp(logLevel, getClass().getName(), "put", message);
    }
  }
}
