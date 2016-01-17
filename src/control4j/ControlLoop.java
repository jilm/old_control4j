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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import control4j.tools.Tools;
import control4j.application.Property;

import static control4j.tools.Logger.*;
import static control4j.tools.LogMessages.*;
import cz.lidinsky.tools.CommonException;

import cz.lidinsky.tools.ToStringBuilder;

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
public class ControlLoop {

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
  ControlLoop() { }

  private ArrayList<ModuleCrate> modules = new ArrayList<ModuleCrate>();

  private int dataBufferSize;

  ControlLoop add(ModuleCrate module) {
    // Add module into the buffer
    modules.add(module);
    // Register as cycle listener
    if (module.getModule() instanceof ICycleEventListener) {
      addCycleEventListener((ICycleEventListener)module.getModule());
    }
    // Estimate total size of data buffer
    dataBufferSize = Math.max(dataBufferSize, module.getMaxSignalPointer());
    return this;
  }

  ControlLoop addAll(Iterable<ModuleCrate> modules) {
    for (ModuleCrate module : modules) {
      add(module);
    }
    return this;
  }

  private long cyclePeriod = 1000;

  private long cycleDelay = 400;

  ControlLoop set(String key, String value) {
    try {
      if (key == null || value == null) {
        throw new AssertionError(); // should not happen
      } else if (key.equals("cycle-period")) {
        cyclePeriod = Long.parseLong(value);
        if (cyclePeriod < 0) {
          throw new SyntaxErrorException()
            .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
            .set("message", "Positive number expected")
            .set("key", key)
            .set("value", value);
        }
      } else if (key.equals("cycle-delay")) {
        cycleDelay = Long.parseLong(value);
        if (cycleDelay < 0) {
          throw new SyntaxErrorException()
            .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
            .set("message", "Positive number expected")
            .set("key", key)
            .set("value", value);
        }
      } else {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.ILLEGAL_ARGUMENT)
          .set("message", "Unsupported config item")
          .set("key", key);
      }
    } catch (NumberFormatException e) {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.PARSE)
        .set("message", "Number expected")
        .set("key", key)
        .set("value", value);
    }
    return this;
  }

  ControlLoop setAll(Map<String, Property> configuration) {
    for (Map.Entry<String, Property> entry : configuration.entrySet()) {
      set(entry.getKey(), entry.getValue().getValue());
    }
    return this;
  }

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
  void run() {

    info("Runnig control loop...");
    dataBuffer = new DataBuffer(dataBufferSize + 1);
    ModuleCrate executedModule = null; // for dump purposes
    // prepare for execution
    try {
      ResourceManager.getInstance().prepare();
      for (ModuleCrate module : modules) {
        module.prepare();
      }
    } catch (Exception e) {
      dump(e, null);
      throw e;
    }

    // The control loop !
    while (true)

      try {
        cycleStartTime = System.currentTimeMillis();
        // erase data buffer
        dataBuffer.clear();
        fireCycleStartEvent();
        // start cycle delay
        Tools.sleep(cycleDelay);
        fireProcessingStartEvent();
        // module execution
        fine("Start of module processing");
        for (ModuleCrate module : modules) {
          executedModule = module;
          module.execute(dataBuffer);
        }
        fireCycleEndEvent();
        // terminate the program, if requst was received
        if (exit) System.exit(0);
        // wait for next turn
        while (true)
        {
          long cycleDuration = System.currentTimeMillis() - cycleStartTime;
          long sleepTime = cyclePeriod - cycleDuration;
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
        //String message = getMessage("BrokenCycle");
        //message = String.format(message, e.getMessage());
        //warning(message);
        warning(new CommonException()
                  .setCause(e)
                  .set("message", "The scan was not finished because of exception!")
                  .set("module", executedModule)
                .toString());
        //dump(e, executedModule.getModule()); // TODO:
      }
  }

  /**
   *  Returns required cycle period in ms. This value is taken
   *  from global configuration, item "cycle-period".
   *
   *  @return required cycle period in ms
   */
  int getCyclePeriod() {
    return (int)cyclePeriod;
  }

  /**
   *  Returns a system time in ms when the last cycle was started.
   *
   *  @return a system time in ms when the last cycle was started
   */
  long getCycleBeginningTime() {
    return cycleStartTime;
  }

  /**
   *  Terminates the control loop and the whole application. Application
   *  is not terminated immediately. It is terminated after the current
   *  loop is finished. This method is not thread safe.
   */
  void exit() {
    exit = true;
  }

  /**
   *  Duration of the last cycle in ms. It returns zero during the first
   *  cycle.
   *
   *  @return duration of the last cycle in ms
   */
  public long getLastCycleDuration() {
    return lastCycleDuration;
  }

  //-------------------------------------------------------------- Cycle Events

  private ArrayList<ICycleEventListener> cycleListeners
    = new ArrayList<ICycleEventListener>();

  void addCycleEventListener(ICycleEventListener listener) {
    if (listener != null) {
      cycleListeners.add(listener);
    }
  }

  private void fireCycleStartEvent() {
    for (ICycleEventListener listener : cycleListeners) {
      listener.cycleStart();
    }
  }

  private void fireCycleEndEvent() {
    for (ICycleEventListener listener : cycleListeners) {
      listener.cycleEnd();
    }
  }

  private void fireProcessingStartEvent() {
    for (ICycleEventListener listener : cycleListeners) {
      listener.processingStart();
    }
  }

  //---------------------------------------------------------------------- Dump

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
  protected void dump(Exception cause, Module module)
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
      writer.println("----- Modules ------");
      for (ModuleCrate crate : modules) {
        writer.println(crate.getModule().toString());
      }
      //ModuleManager.getInstance().dump(writer);
      writer.println("----- Executed module ------");
      if (module != null) {
        writer.println(module.toString());
      }
      writer.println(
          new ToStringBuilder()
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
