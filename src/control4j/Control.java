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

package control4j;

import static control4j.tools.Logger.*;
import static control4j.tools.LogMessages.*;

import control4j.application.ErrorManager;
import control4j.application.Loader;
import control4j.application.Preprocessor;
import control4j.application.Sorter;
import control4j.tools.DeclarationReference;
import control4j.tools.Preferences;

import org.apache.commons.collections4.IteratorUtils;

/**
 *
 *  Entry point of the application.
 *
 *  <p>This object is a singleton.
 *
 */
public class Control {

  /**
   *  The entry point of the application.
   *  Expects the project filename as the last command-line argument.
   */
  public static void main(String[] args) throws Exception {

    info(getMessage("core01"));
    preferences = Preferences.getInstance();
    // process command line arguments
    preferences.parseCommandLineArgs(args);
    // runs the application
    Control control = new Control();
    control.run();
  }

  //---------------------------------------------------- Facade Access Methods.

  /**
   *  Returns required cycle period in ms. Returned value is taken
   *  from global configuration, item "cycle-period".
   *
   *  @return required cycle period in ms
   */
  public static int getCyclePeriod() {
    return controlLoop.getCyclePeriod();
  }

  /**
   *  Returns a time in ms when the last loop has been started.
   *
   *  @return a time in ms when the last loop has been started
   */
  public static long getCycleBeginningTime() {
    return controlLoop.getCycleBeginningTime();
  }

  /**
   *  Returns duration of the last loop in ms. During the first loop it
   *  returns zero. This method is only a facade for method
   *  {@link control4j.ControlLoop#getLastCycleDuration}
   *
   *  @return duration of the last cycle in ms
   *
   *  @see control4j.ControlLoop#getLastCycleDuration
   */
  public static long getLastCycleDuration() {
    return controlLoop.getLastCycleDuration();
  }

  /**
   *  Schedules termination of the application at the end of the current
   *  loop. This method is only a facade for method
   *  {@link control4j.ControlLoop#exit}
   *
   *  @see control4j.ControlLoop#exit
   */
  public static void exit() {
    controlLoop.exit();
  }

  //------------------------------------------------------------------ Private.

  /**
   *  The control loop object.
   */
  private static ControlLoop controlLoop = new ControlLoop();

  /**
   *  Preferences.
   */
  private static Preferences preferences;

  /**
   *  Prevents instantiation of the object.
   */
  private Control() { }

  /**
   *
   */
  private void run() throws Exception {

    String filename = preferences.getProject();
    java.io.File file = new java.io.File(filename);

    Preprocessor preprocessor = new Preprocessor();

    new Loader(preprocessor).load(file);

    controlLoop
      .addAll(
          IteratorUtils.asIterable(
            IteratorUtils.transformedIterator(
              new Sorter().addAll(preprocessor).iterator(),
              new Instantiator())))
      .setAll(preprocessor.getConfiguration());

    // cleen-up
    file = null;
    preprocessor = null;
    // run the control loop
    controlLoop.run();
  }

  /**
   *  Thread to cleen up when exiting
   */
  class CleenUp extends Thread {
    public void run() {
      info("Exitting the JControl application");
    }
  }

}
