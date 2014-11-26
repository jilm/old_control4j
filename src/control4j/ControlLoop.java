package control4j;

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

import java.util.LinkedList;
import control4j.application.SignalManager;
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
 *  cycle is longer than specified the warnig is loged. 
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
  public ControlLoop()
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
    // else !!!
  }
  
  /**
   *  Runs the infinite control loop.
   *
   *  <ol>
   *    <li> Write the cycle start time.
   *    <li> Clear the data buffer.
   *    <li> Send notification of cycle start.
   *    <li> Wait for time specified by start-cycle-delay.
   *    <li> Send notification of processing start.
   *    <li> Execute all of the modules.
   *    <li> Send notification of cycle end.
   *    <li> Terminate the program if request was sent.
   *    <li> Wait for new cycle. The duration of the cycle
   *         must be cycle-period.
   *  </ol>
   */
  void run()
  {
    info("Runnig control loop...");
    ModuleManager modules = ModuleManager.getInstance();
    int signals = SignalManager.getInstance().size();
    dataBuffer = new DataBuffer(signals);
    // prepare for execution
    for (control4j.resources.Resource resource : ResourceManager.getInstance())
      resource.prepare();
    for (Module module : modules)
      module.prepare();

    while (true)
    {
      cycleStartTime = System.currentTimeMillis();
      // erase data buffer
      dataBuffer.clear();
      notifyOfCycleStartEvent();
      // start cycle delay
      try { Thread.sleep(startCycleDelay); } catch (InterruptedException e) {}

      notifyOfProcessingStartEvent();
      // module execution
      try
      {
	fine("Start of module processing");
	for (Module module : modules)
	{
	  module.execute(dataBuffer);
	}
      }
      catch (RuntimeModuleException e)
      {
	// if an exception arise during the processing some
	// of the module, the cycle is not completed and
	// problem is logged.
	String message = getMessage("BrokenCycle");
	message = String.format(message, e.getMessage());
	warning(message);
      }
      notifyOfCycleEndEvent();
      // terminate the program, if requst was received
      if (exit)
        System.exit(0);
      // wait for next turn
      try
      {
	long cycleDuration = System.currentTimeMillis() - cycleStartTime;
	long sleepTime = cyclePeriod - cycleDuration;
	if (sleepTime > 0)
          Thread.sleep(sleepTime);
        else
	{
	  // if the current cycle was longer than value
	  // specified in cycleDuration, print a log message
	  String message = getMessage("LongCycle");
	  message = String.format(message, cycleDuration);
	  warning(message);
	}
	lastCycleDuration = System.currentTimeMillis() - cycleStartTime;
      } 
      catch (Exception e) 
      {
	String message = getMessage("WaitingException", e.getMessage());
	warning(message);
      }
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
   *  Duration of the last cycle in ms. During the first cycle it returns zero.
   *
   *  @return duration of the last cycle in ms
   */
  public long getLastCycleDuration()
  {
    return lastCycleDuration;
  }

}
