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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *
 *  An object which provides management of the resources. Which means
 *  it mainly provides an access to the resouces to modules.
 *
 *  <p>Resource is an instance of object which overrides class
 *  {@link control4j.resources.Resource}.
 *  
 *  <p>Each resource is identified by u unique identifier which is
 *  called name or id.
 *
 *  <p>This object is singleton, it is only one instance of it for the
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
public class ResourceManager implements Iterable<control4j.resources.Resource>
{
  
  /**
   *  A reference to the only instance of the resource manager.
   */
  private final static ResourceManager manager = new ResourceManager();

  /**
   *  Internal buffer for all of the resources.
   */
  private HashMap<String, control4j.resources.Resource> resources 
    = new HashMap<String, control4j.resources.Resource>();

  /**
   *
   */
  private ResourceManager()
  {
    java.lang.Runtime.getRuntime().addShutdownHook(new CleenUp());
  }

  /**
   *  Returns an instance of the ResourceManager class.
   *
   *  @return an instance ot the ResourceManager class
   */
  public static ResourceManager getInstance()
  {
    return manager;
  }

  /**
   *  Adds a resource into the internal buffer. Each resource is identified
   *  by a unique id.
   *
   *  @param id
   *             a unique idenfier of the resource
   *
   *  @param resource
   *             an object to add into the internal buffer
   *
   *  @throws SyntaxErrorException
   *             if there already is a resource with the same id
   *
   *  @throws NullPointerException
   *             if either of the parameters is null
   */
  void add(String id, control4j.resources.Resource resource)
  {
    if (id == null || resource == null)
      throw new NullPointerException();
    if (resources.containsKey(id))
    {
      throw new SyntaxErrorException();
    }
    else
    {
      resources.put(id, resource);
    }
  }

  /**
   *  Returns a resource with given id.
   *
   *  @param id
   *             id of the resource, you want to get
   *
   *  @return resource with given id
   *
   *  @throws NoSuchElementException
   *             if there is no such resource with given id in the buffer
   *
   *  @throws NullPointerException
   *             if id is null
   */
  public control4j.resources.Resource get(String id)
  {
    if (id == null)
      throw new NullPointerException();
    control4j.resources.Resource resource = resources.get(id);
    System.out.println(resource);
    if (resource == null)
    {
      throw new java.util.NoSuchElementException();
    }
    else
    {
      return resource;
    }
  }

  /**
   *  Allows to iterate over all of the resources.
   */
  public Iterator<control4j.resources.Resource> iterator()
  {
    return resources.values().iterator();
  }

  /**
   *  Helper method which assignes an appropriate resource object
   *  to all of the fieds of a given object that are annotated
   *  by a Resource annotation.
   *
   *  <p>For example if the given object contains such a field declaration:
   *  <br><code>@Resource private Console console;</code>
   *  <br>this method will find a value for key "console" in the
   *  configuration, and uses it to get resource with such an id
   *  and finally appends obtained resource into the field in the
   *  object.
   *
   *  <p>The key field of Resource annotation will be used as a key
   *  into the configuration buffer. If it is not used the name of
   *  the object field is used instead.
   *
   *  @param object
   *             the object which will be scanned for fields annotated
   *             by Resource annotation. The appropriate resources will
   *             be assigned to such fields.
   *
   *  @param configuration
   *             have to contain mapping resource key to resource id
   *
   *  @throws SyntaxErrorException
   *             <ul>
   *               <li>if configuration argument doesn't contains a value
   *             </ul>
   *
   *  @throws SystemException
   *
   *  @throws NullPointerException
   *             if eather of the arguments contains null 
   *
   *  @see control4j.Resource
   */
  public static void assignResources(Object object, IConfigBuffer configuration)
  {
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields)
    {
      Resource annotation = field.getAnnotation(Resource.class);
      if (annotation != null)
      {
        String key = annotation.key();
	if (key.length() == 0) key = field.getName();
	try
	{
	  String resourceName = configuration.getString(key);
	  control4j.resources.Resource resource = manager.get(resourceName);
	  boolean accessibility = field.isAccessible();
	  field.setAccessible(true);
          field.set(object, resource);
	  field.setAccessible(accessibility);
	}
	catch (ConfigItemNotFoundException e)
	{
	  // there is not a value in configuration for the resource field
	  throw new SyntaxErrorException("not found");
	}
	catch (java.util.NoSuchElementException e)
	{
	  // there is not a resource with given name 
	  throw new SyntaxErrorException("no such element");
	}
	catch (IllegalAccessException e)
	{
	  // a field in the object is not accessible
	  throw new SystemException();
	}
	catch (IllegalArgumentException e)
	{
	  // the specified object doesn't implement the field. Should not
	  // happen. Or if casting fails
          throw new SyntaxErrorException();
	}
      }
    }
    
  }

  private class CleenUp extends Thread
  {
    public void run()
    {
    }
  }
}
