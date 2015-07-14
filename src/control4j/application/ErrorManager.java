package control4j.application;

/*
 *  Copyright 2015 Jiri Lidinsky
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

import control4j.tools.DeclarationReference;
import static control4j.tools.Logger.*;
import java.util.LinkedList;

/**
 *
 *  Provides a unified way to process and manage error messages.
 *
 *  <p>This class is singleton. There is only one instance of this
 *  object.
 *
 */
public class ErrorManager {

  //-------------------------------------------------- Singleton Implementation

  /** Error manager instance. */
  private static ErrorManager instance;

  /**
   *  An empty constructor; doesn't allow to create instances.
   */
  private ErrorManager() { }

  /**
   *  Use this method to get instance of the error manager.
   */
  public static ErrorManager getInstance() {
    if (instance == null) instance = new ErrorManager();
    return instance;
  }

  //----------------------------------------------------------- Errors Handling

  /** The list of fatal errors. */
  private LinkedList<ErrorRecord> errors;

  /**
   *  Adds an error into the buffer.
   */
  public void addError(String message) { }

  public static ErrorRecord newError() {
    ErrorRecord error = new ErrorRecord();
    ErrorManager manager = getInstance();
    if (manager.errors == null) {
      manager.errors = new LinkedList<ErrorRecord>();
    }
    manager.errors.add(error);
    return error;
  }

  /**
   *  Print all of the error messages.
   */
  public void printErrors() {
    if (errors != null) {
      for (ErrorRecord error : errors) {
        System.out.println(error.toString());
      }
    }
  }

  //--------------------------------------------------------- Warnings Handling

  /** The list of warnings. */
  private LinkedList<String> warnings;

  /**
   *  Adds a warning into the buffer.
   */
  public void addWarning(String message)
  {
    if (warnings == null)
      warnings = new LinkedList<String>();
    warnings.add(message);
  }

  /**
   *  Print all of the warning messages.
   */
  public void printWarnings() {
    if (warnings != null) {
      for (String message : warnings) {
        warning(message);
      }
    }
  }

  //------------------------------------------------------------- Other Methods

  /**
   *  Print both the warnings and the errors. If there is at least
   *  one error in the buffer, it exits the application.
   */
  public static void print() {
    getInstance().printWarnings();
    getInstance().printErrors();
    if (getInstance().errors != null && getInstance().errors.size() > 0) {
      System.exit(1);
    }
  }

  public void clean()
  {
  }

}
