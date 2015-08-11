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

package control4j.application;

import static control4j.tools.Logger.*;

import control4j.tools.DeclarationReference;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.collections4.Closure;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 *
 *  Provides a unified way to process and manage error messages that
 *  arise during the application loading and preprocessing.
 *
 *  <p>This object is a singleton.
 *
 */
public class ErrorManager {

  //------------------------------------------------- Singleton Implementation.

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

  //--------------------------------------------------------- Public Interface.

  /**
   *  Stores the error into the internal buffer. To report such postphoned
   *  errors use method printErrorsAndExit.
   */
  public static ErrorRecord newError() {
    ErrorRecord error = new ErrorRecord();
    ErrorManager manager = getInstance();
    if (manager.errors == null) {
      manager.errors = new ArrayDeque<ErrorRecord>();
    }
    manager.errors.push(error);
    return error;
  }

  public static void printAndExit() {
    if (getInstance().printErrors()) {
      System.exit(1);
    }
  }

  public static <T> ErrorManager forAllDo(
      Iterator<T> collection, Closure<T> closure) {
    if (collection != null && closure != null) {
      while (collection.hasNext()) {
        try {
          T element = collection.next();
          closure.execute(element);
        } catch (Exception e) {
          newError().setCause(e);
        }
      }
    }
    return getInstance();
  }

  //------------------------------------------------------------------ Private.

  /** The list of fatal errors. */
  private ArrayDeque<ErrorRecord> errors;

  /**
   *  Print all of the error messages.
   *
   *  @return true if there was at least one error in the buffer
   */
  private boolean printErrors() {
    boolean nonEmpty = false;
    if (errors != null) {
      while (!errors.isEmpty()) {
        System.out.println(errors.pop().toString());
        nonEmpty = true;
      }
    }
    return nonEmpty;
  }

}
