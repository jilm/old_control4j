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

import java.lang.reflect.Field;
import control4j.application.ModuleDeclaration;
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
  protected void initialize(IConfigBuffer configuration) 
  { 
    // assign configuration items
    ConfigurationHelper.assignConfiguration(this, configuration, declarationReference);
    // assign resources
    ResourceManager.getInstance().assignResources(this, configuration);
  }

  /**
   *  Initialize internal structures of the module which are mandatory.
   *  If you (a module developer) want to influence the module configuration,
   *  override the {@link #initialize(IConfigBuffer)} method.
   */
  protected abstract void initialize(ModuleDeclaration declaration);

  public void prepare()
  { }

  /**
   *  Method that do some preparation work, like collection of input signals
   *  and than it calls the execution method of the appropriate module 
   *  implementation. Do not override this method!
   *
   *  @param data
   *             a buffer where to get input signals and where to
   *             return the output signals
   */
  abstract void execute(DataBuffer data);

  /**
   *  Creates and returns module instance. Returned instance is
   *  initialized according to the declaration argument.
   *
   *  <ol>
   *    <li> Creates an instance of the appropriate class.
   *    <li> Sets the information about the declaration reference.
   *    <li> Calles the {@link #initialize(ModuleDeclaration)} method.
   *    <li> If module implements a CycleEventListener interface,
   *         the module is registered to obtain these events.
   *  </ol>
   *
   *  @param declaration
   *             module declaration
   *
   *  @return module instance
   *
   *  @throws SyntaxErrorException
   *             if class doesn't exist
   *
   *  @throws ModuleImplementationException
   *             if implementation of the module is not correct
   */
  public static Module getInstance(ModuleDeclaration declaration)
  {
    String className = declaration.getClassName();
    try
    {
      // get module class
      Class<Module> moduleClass = (Class<Module>)(Class.forName(className));
      // get module instance
      Module module = moduleClass.newInstance();
      // set the declaration reference
      module.declarationReference = declaration.getDeclarationReference();
      // initialize the module
      module.initialize(declaration);
      // register for cycle events listen
      if (module instanceof ICycleEventListener)
        Control.registerCycleEventListener((ICycleEventListener)module);
      return module;
    }
    catch (ClassNotFoundException e)
    {
      throw new SyntaxErrorException("Class was not found");
    }
    // if this class represents an abstrac class, interface, an array ...
    catch (InstantiationException e)
    {
      String message = getMessage("module01", className, e.getMessage());
      throw new ModuleImplementationException(message);
    }
    // constructor is not accessible
    catch (IllegalAccessException e)
    {
      String message = getMessage("module02", className, e.getMessage());
      throw new ModuleImplementationException(message);
    }
  }

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

}
