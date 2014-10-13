package control4j.tools;

/*
 *  Copyright 2013 Jiri Lidinsky
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

import java.util.logging.Handler;
import java.util.logging.Level;

/**
 *  Provides log functions to the application. As a backend use
 *  standard java logger. Name of the logger is 
 *  cz.lidinsky.jcontrol
 */     
public class Logger
{
  private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("cz.lidinsky.control4j");

  private Logger() {}

  public static void config(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.CONFIG, sourceClass, sourceMethod, message);
  }
  
  public static void fine(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.FINE, sourceClass, sourceMethod, message);
  }
  
  public static void finer(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.FINER, sourceClass, sourceMethod, message);
  }
  
  public static void finest(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.FINEST, sourceClass, sourceMethod, message);
  }
  
  public static void info(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.INFO, sourceClass, sourceMethod, message);
  }

  public static void severe(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.SEVERE, sourceClass, sourceMethod, message);
  }

  public static void warning(String sourceClass, String sourceMethod, String message)
  {
    logger.logp(Level.WARNING, sourceClass, sourceMethod, message);
  }

  public static void config(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    config(sourceClass, sourceMethod, message);
  }
  
  public static void fine(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    fine(sourceClass, sourceMethod, message);
  }

  public static void finer(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    finer(sourceClass, sourceMethod, message);
  }

  public static void finest(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    finest(sourceClass, sourceMethod, message);
  }

  public static void info(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    info(sourceClass, sourceMethod, message);
  }

  public static void severe(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    severe(sourceClass, sourceMethod, message);
  }

  public static void warning(String message)
  {
    Thread thread = Thread.currentThread();
    StackTraceElement[]	stackTrace = thread.getStackTrace();
    String sourceClass = stackTrace[stackTrace.length-1].getClassName();
    String sourceMethod = stackTrace[stackTrace.length-1].getMethodName();
    warning(sourceClass, sourceMethod, message);
  }
  
  public static void throwing(String sourceClass, String sourceMethod, Throwable thrown)
  {
    logger.throwing(sourceClass, sourceMethod, thrown);
  }
  
  public static void catched(String className, String methodName, Throwable thrown)
  {
    logger.logp(Level.WARNING, className, methodName, "CATCHED: " + thrown.getMessage());
  }
  
  public static void entering(String sourceClass, String sourceMethod)
  {
    logger.entering(sourceClass, sourceMethod);
  }
  
  public static void exiting(String sourceClass, String sourceMethod)
  {
    logger.exiting(sourceClass, sourceMethod);
  }

  public static java.util.logging.Logger getLogger()
  {
    return logger;
  }

}
