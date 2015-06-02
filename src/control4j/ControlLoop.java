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

import java.util.LinkedList;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import control4j.tools.Tools;

import static control4j.tools.Logger.*;
import static control4j.tools.LogMessages.*;

import cz.lidinsky.tools.ToStringMultilineStyle;

/**
 *
 *  This is the runtime engine of the control4j application.
 *  After it is run, it enters into an infinite loop in which
 *  it repeatedly execute all of the modules.
 *
 *  <p>Duration of one cycle is fixed and may be specified by
 *  configuration item cycle-period. If the processing of the
 *  cycle is longer than specified the warnig is logged.
 *
 */
public class ControlLoop
{

  /** The buffer for signals which serves as interchange point between
      outputs and inputs of the modules. */
  private DataBuffer dataBuffer;

  private long cycleStartTime;

  /**
   *  A flag that indicates that the request was received to terminate
   *  the program. Program will be terminated at the end of the loop.
   */
  private boolean exit = false;

  /**
   *  Duration of the last cycle in ms.
   */
  private long lastCycleDuration = 0l;

  /**
   *  It does nothing.
   */
  ControlLoop()
  { }

  private Application application;

  /**
   *  Runs the infinite control loop. Following steps are performed:
   *
   *  <ol>
   *    <li> Creates {@link control4j.DataBuffer} instance.
   *    <li> Runs a {@link control4j.resources.Resource#prepare}
   *         method for all of the resources.
   *    <li> Runs a {@link control4j.Module#prepare} method for
   *         all of the modules.
   *    <li> Enters an infinite loop.
   *    <li> Note the cycle start time.
   *    <li> Clear the data buffer.
   *    <li> Trigger cycleStartEvent event for all of the registered
   *         listeners.
   *    <li> Wait for time specified by start-cycle-delay.
   *    <li> Trigger processingStartEvent event for all of the registered
   *         listeners.
   *    <li> Execute all of the modules.
   *    <li> Trigger cycleEndEvent event for all of the registered listeners.
   *    <li> Terminate the program if requested, see {@link #exit}.
   *    <li> Wait for new cycle. The duration of the cycle
   *         must be cycle-period.
   *  </ol>
   *
   *  @see control4j.ICycleEventListener
   */
  void run(Application application)
  {

    this.application = application;
    info("Runnig control loop...");
    dataBuffer = new DataBuffer(application.dataBufferSize);
    // prepare for execution
    ResourceManager.getInstance().prepare();
    application.prepare();

    // The control loop !
    while (true)
      try
      {
        cycleStartTime = System.currentTimeMillis();
        // erase data buffer
        dataBuffer.clear();
        application.fireCycleStartEvent();
        // start cycle delay
        Tools.sleep(application.getStartCycleDelay());
        application.fireProcessingStartEvent();
        // module execution
        fine("Start of module processing");
        for (Pair<OutputModule, int[]> module : application.outputModules)
          execute(module.getLeft(), module.getRight());
        for (Triple<ProcessModule, int[], int[]> module
            : application.processModules)
          execute(module.getLeft(), module.getMiddle(), module.getRight());
        for (Pair<InputModule, int[]> module : application.inputModules)
          execute(module.getLeft(), module.getRight());
        application.fireCycleEndEvent();
        // terminate the program, if requst was received
        if (exit) System.exit(0);
        // wait for next turn
        while (true)
        {
          long cycleDuration = System.currentTimeMillis() - cycleStartTime;
          long sleepTime = application.getCyclePeriod() - cycleDuration;
          if (sleepTime > 0)
            Tools.sleep(sleepTime);
          else if (sleepTime < -100)
          {
            // if the current cycle was longer than value
            // specified in cycleDuration, print a log message
            String message = getMessage("LongCycle");
            message = String.format(message, cycleDuration);
            warning(message);
            break;
          }
          else break;
        }
        lastCycleDuration = System.currentTimeMillis() - cycleStartTime;
      }
      catch (Exception e)
      {
        // if an exception arise during the processing some
        // of the module, the cycle is not completed and
        // problem is logged.
        String message = getMessage("BrokenCycle");
        message = String.format(message, e.getMessage());
        warning(message);
        dump(e);
      }
  }

  protected void execute(OutputModule module, int[] map)
  {
    Signal[] output = getOutputArray(map.length);
    module.get(output, map.length);
    dataBuffer.put(output, map);
  }

  protected void execute(ProcessModule module, int[] inputMap, int[] outputMap)
  {
    Signal[] input = dataBuffer.get(inputMap);
    Signal[] output = getOutputArray(outputMap.length);
    module.process(input, inputMap.length, output, outputMap.length);
    dataBuffer.put(output, outputMap);
  }

  protected void execute(InputModule module, int[] map)
  {
    Signal[] input = dataBuffer.get(map);
    module.put(input, map.length);
  }

  private Signal[] inputArray;

  protected Signal[] getInputArray(int size)
  {
    if (inputArray == null)
      inputArray = new Signal[size];
    else if (inputArray.length < size)
      inputArray = new Signal[size];
    return inputArray;
  }

  private Signal[] outputArray;

  protected Signal[] getOutputArray(int size)
  {
    if (outputArray == null)
      outputArray = new Signal[size];
    else if (outputArray.length < size)
      outputArray = new Signal[size];
    return outputArray;
  }

  /**
   *  Returns required cycle period in ms. This value is taken
   *  from global configuration, item "cycle-period".
   *
   *  @return required cycle period in ms
   */
  int getCyclePeriod()
  {
    return application.getCyclePeriod();
  }

  /**
   *  Returns a system time in ms when the last cycle was started.
   *
   *  @return a system time in ms when the last cycle was started
   */
  long getCycleBeginningTime()
  {
    return cycleStartTime;
  }

  /**
   *  Terminates the control loop and the whole application. Application
   *  is not terminated immediately. It is terminated after the current
   *  loop is finished. This method is not thread safe.
   */
  void exit()
  {
    exit = true;
  }

  /**
   *  Duration of the last cycle in ms. It returns zero during the first
   *  cycle.
   *
   *  @return duration of the last cycle in ms
   */
  public long getLastCycleDuration()
  {
    return lastCycleDuration;
  }

  /** True, if the dump file should be created, false otherwise. */
  protected boolean dump = true;

  /**
   *  Create a file which contains all of the available information
   *  that could be useful to find a problem. This method is called
   *  during the runtime if an exception was thrown.
   *
   *  <p>To prevent a colaps of the system, potentialy caused by dump
   *  files flood, the dump file is created only ones.
   *
   *  @param cause
   *             an exception which interrupted control loop, may be
   *             null
   */
  protected void dump(Exception cause)
  {
    java.io.PrintWriter writer = null;
    if (dump)
    try
    {
      // create dump file
      String tempDir = System.getProperty("java.io.tmpdir");
      String filename = "control4j_"
          + java.util.UUID.randomUUID().toString() + ".dump";
      java.io.File dumpFile = new java.io.File(tempDir, filename);
      writer = new java.io.PrintWriter(dumpFile);
      // write a timestamp
      writer.println("This is a control4j dump file.");
      writer.println(new java.util.Date().toString());
      // write system information
      writer.println(System.getProperty("java.version"));
      writer.println(System.getProperty("java.vendor"));
      writer.println(System.getProperty("os.name"));
      writer.println(System.getProperty("os.arch"));
      writer.println(System.getProperty("os.version"));
      // write the exception
      if (cause != null)
      {
        writer.println("EXCEPTION: " + cause.getClass().getName());
        writer.println(cause.getMessage());
        cause.printStackTrace(writer);
      }
      // write data buffer
      dataBuffer.dump(writer);
      // write the resources
      ResourceManager.getInstance().dump(writer);
      // write the modules
      //ModuleManager.getInstance().dump(writer);
      //
      writer.println(
	  new ToStringMultilineStyle()
	      .append(application)
	      .toString());
      info("The dump file was created: " + dumpFile.getAbsolutePath());
      dump = false;
    }
    catch (java.io.IOException e)
    {
      catched(getClass().getName(), "dump", e);
      warning("Cannot create dump file");
    }
    finally
    {
      Tools.close(writer);
    }
  }

}
