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

import java.lang.reflect.Field;

import control4j.tools.DeclarationReference;
import static control4j.tools.LogMessages.*;

/**
 *  Module is one of the building blocks of the application.
 *
 */
public abstract class Module
{

  /**
   *  Reference to the place where the module was declared in
   *  human readable form.
   */
  private DeclarationReference declarationReference;

  /**
   *  Initialize a module. This method assign configuration items
   *  from the configuration parameter to the properties that are
   *  annotated by ConfigItem. If you don't want this functionality
   *  just override this method. Or you can override it, do some
   *  custom initialization and than call this to finish the
   *  initialization.
   *
   *  @param configuration 
   *             the configuration of the module.
   *
   *  @see ConfigurationHelper
   */
  public void initialize(IConfigBuffer configuration) 
  { 
    // assign configuration items
    ConfigurationHelper.assignConfiguration(
	this, configuration, declarationReference);
  }

  public void prepare()
  { }

  /**
   *  Returns a reference to the config file
   *  where the module was declared. The reference
   *  is in the human readable form and play a role in
   *  the exception and log messages.
   *
   *  @return the declaration reference, if not specified, returns an
   *       empty string
   */
  public String getDeclarationReference()
  {
    if (declarationReference != null)
      return declarationReference.toString();
    else
      return "";
  }

  /**
   *  Prints information about the module into the given writer.
   *  It serves mainly for debug purposes. This method writes only
   *  the declaration reference, if you thing that it would be useful
   *  to have more info about a particular module in the case of
   *  failure to find out what is going on, simply override this
   *  method.
   *
   *  @param writer
   *             a writer on which the dump will be printed
   */
  public void dump(java.io.PrintWriter writer)
  {
    writer.println("== MODULE ==");
    writer.println("Class: " + getClass().getName());
    writer.println(declarationReference.toString());
  }

}
