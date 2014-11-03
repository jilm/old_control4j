package control4j.resources;

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

import control4j.Control;
import control4j.IConfigBuffer;
import control4j.ICycleEventListener;
import control4j.application.ResourceDeclaration;
import control4j.ConfigurationHelper;
import control4j.SystemException;
import control4j.SyntaxErrorException;
import control4j.tools.DeclarationReference;

/**
 *
 *  It is used as a base class for classes that provides access to some
 *  limited resource, mostly hardware. For example I/O card, timer, etc.
 *  These reserce objects than may be used by modules.
 *
 */
public abstract class Resource
{
  /** 
   *  Must indicate where is the place where this resource object was
   *  declared. It shoud be human readable information.
   *  This field may contain null value!
   */
  private DeclarationReference declarationReference;

  /**
   *  Content of return object should describe a place where this
   *  resource object was declared. It should contain human readable
   *  information.
   *
   *  @return a declaration reference. May return null!
   */
  public DeclarationReference getDeclarationReference()
  {
    return declarationReference;
  }

  /**
   *  This method is used for initialization of the resource.
   *  It assignes all of the fields and methods that are
   *  annotated by ConfigItem annotation. The author of
   *  derived resource class may override this method to
   *  do custom initialization or to suppress the authomatic
   *  field assigment.
   *
   *  <p>This method is called by createInstance method right after 
   *  the instance is created.
   *
   *  <p>For all of the exceptions that may be thrown by this
   *  method see ConfigurationHelper object.
   *
   *  @param configuration
   *             contains configuration which was declared in the 
   *             application file
   *
   *  @see ConfigurationHelper#assignConfiguration
   */
  protected void initialize(IConfigBuffer configuration)
  {
    ConfigurationHelper.assignConfiguration(this, configuration, declarationReference);
  }

  public void prepare()
  { }

  /**
   *  It takes a class name from the argument and creates an instance
   *  of such a class. After that it assignes declarationReference
   *  field and calls initialize method. If the class implements 
   *  ICycleEventListener it subscribe created instance to get this
   *  events.
   *
   *  @throws NullPointerException
   *             if declaration contains null value
   *
   *  @throws SystemException
   *
   *  @throws SyntaxErrorException
   *
   *  @throws ClassNotFoundException
   *             if class with given name was not found
   *
   *  @see #initialize
   *  @see control4j.ICycleEventListener
   */
  public static Resource createInstance(ResourceDeclaration declaration)
  throws ClassNotFoundException
  {
    if (declaration == null) throw new NullPointerException();
    String className = declaration.getClassName();
    try
    {
      Class<Resource> resourceClass = (Class<Resource>)Class.forName(className);
      Resource resource = resourceClass.newInstance();
      resource.declarationReference = declaration.getDeclarationReference();
      IConfigBuffer configuration = declaration.getConfiguration();
      resource.initialize(configuration);
      
      // register for cycle events listen
      if (resource instanceof ICycleEventListener)
        Control.registerCycleEventListener((ICycleEventListener)resource);
      
      return resource;
    }
    catch (ClassCastException e)
    {
      throw new SystemException();
    }
    catch (InstantiationException e)
    {
      throw new SystemException();
    }
    catch (IllegalAccessException e)
    {
      throw new SystemException();
    }
  }

}
