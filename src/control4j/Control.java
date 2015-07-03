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

import java.util.*;
import java.io.*;
import control4j.tools.DeclarationReference;
import control4j.tools.Preferences;
import control4j.application.Preprocessor;
import control4j.application.Loader;
import control4j.application.Sorter;
import control4j.application.ErrorManager;
import static control4j.tools.Logger.*;
import static control4j.tools.LogMessages.*;

public class Control
{
  private static ControlLoop controlLoop = new ControlLoop();
  private static Preferences preferences;

  private Control()
  { }

  /**
   *  Expects the name of the project filename as the last
   *  argument.
   */
  public static void main(String[] args) throws Exception
  {
    info(getMessage("core01"));
    preferences = Preferences.getInstance();

    // process command line arguments
    preferences.parseCommandLineArgs(args);

    Control control = new Control();
    control.run();
  }

  /**
   *  Returns required cycle period in ms. Returned value is taken
   *  from global configuration, item "cycle-period".
   *
   *  @return required cycle period in ms
   */
  public static int getCyclePeriod()
  {
    return controlLoop.getCyclePeriod();
  }

  /**
   *  Returns a time in ms when the last cycle was started.
   *
   *  @return a time in ms when the last cycle was started
   */
  public static long getCycleBeginningTime()
  {
    return controlLoop.getCycleBeginningTime();
  }

  /**
   *  Schedules termination of the application at the end of the current
   *  loop. This method is only a facade for method
   *  {@link control4j.ControlLoop#exit}
   *
   *  @see control4j.ControlLoop#exit
   */
  public static void exit()
  {
    controlLoop.exit();
  }

  /**
   *  Returns duration of the last cycle in ms. During the first cycle it
   *  returns zero. This method is only a facade for method
   *  {@link control4j.ControlLoop#getLastCycleDuration}
   *
   *  @return duration of the last cycle in ms
   *
   *  @see control4j.ControlLoop#getLastCycleDuration
   */
  public static long getLastCycleDuration()
  {
    return controlLoop.getLastCycleDuration();
  }

  /*--------------------------------------------------------------
     Private methods
  --------------------------------------------------------------*/

  /**
   *
  private Application loadApplication(Project project, File parentPath)
  {
    int fatalErrors = 0;
    fine(getMessage("core06"));
    ReaderFactory factory = ReaderFactory.getInstance();
    Application application = new Application();
    for (ApplicationFilename file : project.getFilenames())
    {
      // create file object
      File applicationFile = new File(file.getFilename());
      if (!applicationFile.isAbsolute())
        applicationFile = new File(parentPath, file.getFilename());
      try
      {
        // get an appropriate reader for the application file
        IApplicationReader reader = factory.getReader(file.getType());
        // create an input stream
        InputStream inputStream = new FileInputStream(applicationFile);
        DeclarationReference fileReference = new DeclarationReference();
        fileReference.setFile(applicationFile.getAbsolutePath());
        reader.load(inputStream, application, fileReference);
        inputStream.close();
      }
      catch (FileNotFoundException e)
      {
        severe(getMessage("core07", applicationFile.getAbsolutePath()));
        fatalErrors ++;
      }
      catch (IOException e)
      {
        StringBuilder message = new StringBuilder();
        message.append(getMessage("core08"))
               .append('n')
               .append(e.getMessage());
        severe(message.toString());
        fatalErrors ++;
      }
    }
    if (fatalErrors > 0) exit(1);
    return application;
  }
   */

  /**
   *
   */
  private void run() throws Exception {

    String filename = preferences.getProject();
    java.io.File file = new java.io.File(filename);

    Sorter sorter = new Sorter();

    Preprocessor preprocessor = new Preprocessor(sorter);

    new Loader(preprocessor)
      .load(file);

    preprocessor.process();

    Instantiator instantiator = new Instantiator(controlLoop);
    sorter.process(instantiator);
    ErrorManager.print();
    controlLoop.run();
  }

  /**
   *
   */
  /*
  private Project loadProject(File projectFile)
  {
    info(getMessage("core02", projectFile.getAbsolutePath()));
    try
    {
      InputStream inputStream = new FileInputStream(projectFile);
      control4j.project.Reader reader = new control4j.project.Reader();
      Project project = reader.load(inputStream);
      inputStream.close();
      fine(getMessage("core05"));
      return project;
    }
    catch (FileNotFoundException e)
    {
      severe(getMessage("core03", projectFile.getAbsolutePath()));
      exit(1);
      return null;
    }
    catch (IOException e)
    {
      StringBuilder message = new StringBuilder();
      message.append(getMessage("core04"));
      message.append('\n');
      message.append(e.getMessage());
      severe(message.toString());
      exit(1);
      return null;
    }
  }
  */

  /**
   *
   */
  private void exit(int param)
  {
    System.exit(param);
  }

  /**
   *
   *      Thread to cleen up when exiting
   *
   */
  class CleenUp extends Thread
  {
    public void run()
    {
      info("Exitting the JControl application");
    }
  }

}
