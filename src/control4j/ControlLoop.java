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
//import control4j.application.SignalManager;
import control4j.tools.Tools;
import static control4j.tools.Logger.*;
import static control4j.tools.LogMessages.*;

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

  /** Duration of the whole cycle in ms */
  private int cyclePeriod = 1000;
  private int startCycleDelay = 200;
  private long cycleStartTime;
  private LinkedList<ICycleEventListener> eventListeners 
    = new LinkedList<ICycleEventListener>();

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

  /**
   *  Specify duration of the one cycle loop in ms.
   *
   *  @param period
   *             period of one cycle loop in ms. Must be
   *             greater than zero
   */
  @ConfigItem(key="cycle-period", optional=true)
  public void setCyclePeriod(int period)
  {
    if (period > 0)
      cyclePeriod = period;
    // else !!! TODO
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
  void run()
  {
    info("Runnig control loop...");
    //ModuleManager modules = null; //= ModuleManager.getInstance();
    int signals = 0; // =  SignalManager.getInstance().size();
    dataBuffer = new DataBuffer(signals);
    // prepare for execution
    for (control4j.resources.Resource resource : ResourceManager.getInstance())
      resource.prepare();
    //for (Module module : modules)
      //module.prepare();

    // The control loop !
    while (true)
      try
      {
        cycleStartTime = System.currentTimeMillis();
        // erase data buffer
        dataBuffer.clear();
        notifyOfCycleStartEvent();
        // start cycle delay
	Tools.sleep(startCycleDelay);
        notifyOfProcessingStartEvent();
        // module execution
	fine("Start of module processing");
	//for (Module module : modules)
	//{
	  //module.execute(dataBuffer);
	//}
        notifyOfCycleEndEvent();
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
	String message = getMessage("BrokenCycle");
	message = String.format(message, e.getMessage());
	warning(message);
	dump(e);
      }
  }

  /**
   *  Returns required cycle period in ms. This value is taken 
   *  from global configuration, item "cycle-period".
   *
   *  @return required cycle period in ms
   */
  int getCyclePeriod()
  {
    return cyclePeriod;
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
   *  Register an object listener to subscribe to cycle end
   *  and start events.
   *
   *  @param listener an object that will be notified of cycle
   *                  start and end events
   */
  void registerCycleEventListener(ICycleEventListener listener)
  {
    eventListeners.add(listener);
  }

  /**
   *  Calls cycleStart method of all objects that are registered
   *  to be notified of cycle events.
   */
  private void notifyOfCycleStartEvent()
  {
    for (ICycleEventListener listener : eventListeners)
      listener.cycleStart();
  }

  private void notifyOfProcessingStartEvent()
  {
    for (ICycleEventListener listener : eventListeners)
      listener.processingStart();
  }

  /**
   *  Calls cycleEnd method of all the objects which are registered
   *  to be notified of cycle events.
   */
  private void notifyOfCycleEndEvent()
  {
    for (ICycleEventListener listener : eventListeners)
      listener.cycleEnd();
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
