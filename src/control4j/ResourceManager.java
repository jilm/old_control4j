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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

import cz.lidinsky.tools.CommonException;

/**
 *
 *  An object which provides management of the resources.
 *
 *  <p>Resource is an instance of object which overrides class
 *  {@link control4j.resources.Resource}.
 *
 *  <p>Each resource is identified by a unique identifier which is
 *  called name or id.
 *
 *  <p>This object is singleton, there is only one instance of it for the
 *  whole application. You can use method {@link #getInstance} to
 *  obtain the instance.
 *
 *  <p>Internal buffer of ResourceManager is filled by the ApplicationBuilder
 *  object.
 *
 *  @see control4j.resources.Resource
 *  @see control4j.ApplicationBuilder
 *
 */
public class ResourceManager
{

  /**
   *  A reference to the only instance of the resource manager.
   */
  private static ResourceManager manager;

  /**
   *  Returns an instance of the ResourceManager class.
   *
   *  @return an instance ot the ResourceManager class
   */
  public static ResourceManager getInstance()
  {
    if (manager == null)
      manager = new ResourceManager();
    return manager;
  }

  /**
   *  Register the manager to receive a shutdown notification.
   */
  private ResourceManager()
  {
    java.lang.Runtime.getRuntime().addShutdownHook(new CleenUp());
  }

  /**
   *  Internal buffer for all of the resources.
   */
  private ArrayList<Resource> resources = new ArrayList<Resource>();

  /**
   *  Returns a resource that satisfies given criteria. If such a resource
   *  already exists (it was requested earlier) this method returns it. If
   *  doesn't it creates and returns new instance.
   */
  public Resource getResource(control4j.application.Resource definition)
      throws InstantiationException,
             IllegalAccessException,
             ClassNotFoundException {

    // if the instance of requested resource already exists, return it
    for (Resource resource : resources) {
      if (resource.isEquivalent(definition)) {
        return resource;
      }
    }

    try {
      // if not, create new instance
      Resource resource
        = (Resource)(Class.forName(definition.getClassName()).newInstance());
      resource.initialize(definition);
      resources.add(resource);
      return resource;
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "A new resource instance could not be created!");
    }
  }

  void prepare()
  {
    for (Resource resource : resources)
    {
      resource.prepare();
    }
  }

  /**
   *  Prints a status of resources into the given writer. Serves mainly
   *  for debug purposes. This method simply calls a <code>dump</code>
   *  method of each resource instance.
   *
   *  @param writer
   *             a writer where the resource status will be printed
   *
   *  @see control4j.resources.Resource#dump
   */
  void dump(java.io.PrintWriter writer)
  {
    // TODO:
  }

  private class CleenUp extends Thread
  {
    public void run()
    {
    }
  }
}
