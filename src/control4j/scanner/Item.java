package control4j.scanner;

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

import java.lang.reflect.Method;

/**
 *  One atomic value which contains result of the scanner process.
 */
public class Item
{
  /** A string identifier of the value */
  private String key;

  /** The obtained value, may be null */
  private Object value;

  /** A method to get the value, may be null ig the value is only writable */
  private Method getter;

  /** A method to set the value, may be null if the value is only readable */
  private Method setter;

  /** Scanned object */
  private Object object;

  /** Data type of the value */
  private Class valueClass;

  /**
   *  @param key
   *             an identifier of the item
   *
   *  @param object
   *             the scanned object
   */
  Item(String key, Object object)
  {
    this.key = key;
    this.object = object;
  }

  /**
   *  Return true if the value is readable.
   *
   *  @return true if the value is readable
   */
  public boolean isReadable()
  {
    return getter != null;
  }

  /**
   *  Return true if the value is writable, it means there is a method,
   *  which is annotated by setter annotation.
   *
   *  @return true if the value is writable
   */
  public boolean isWritable()
  {
    return setter != null;
  }

  /**
   *  Return value, which is saved in the internal cache.
   */
  public Object getValue()
  {
    try
    {
      if (value == null && getter != null) update();
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
    }
    catch (IllegalAccessException e)
    {
    }
    return value;
  }

  public void setValue(Object value)
  throws java.lang.reflect.InvocationTargetException
  {
    if (setter != null)
      try
      {
        setter.invoke(object, value);
	this.value = value;
      }
      catch (IllegalAccessException e)
      {
      }
  }

  public Class getValueClass()
  {
    return valueClass;
  }

  public String getKey()
  {
    return key;
  }

  public Object getObject()
  {
    return object;
  }

  void setSetter(Method setter)
  {
    if (setter == null)
    {
      this.setter = null;
      return;
    }
    if (this.setter != null)
      // thre are two or more setter methods for one key
      throw new IllegalArgumentException();
    // there must be exactly one paraameter
    Class[] parameters = setter.getParameterTypes();
    if (parameters.length != 1)
      throw new IllegalArgumentException();
    // and type of this parameter must conform with type of value, if already
    // defined
    if (valueClass != null)
    {
      if (!valueClass.isAssignableFrom(parameters[0]))
        throw new IllegalArgumentException();
    }
    else
      valueClass = parameters[0];
    this.setter = setter;
  }

  /**
   *  The getter method must be accessible, without arguments and must
   *  return value.
   */
  void setGetter(Method getter)
  {
    if (getter == null)
    {
      this.getter = null;
      this.value = null;
      return;
    }
    if (this.getter != null)
      // there are two or more getter methods with the same key
      throw new IllegalArgumentException();
    // Thre must be no parameters
    Class[] parameters = getter.getParameterTypes();
    if (parameters.length > 0)
      throw new IllegalArgumentException();
    // if classValue is already defined, it must correspond with return type
    Class returnedType = getter.getReturnType();
    if (valueClass != null)
    {
      if (!valueClass.isAssignableFrom(returnedType))
        throw new IllegalArgumentException();
    }
    else
      valueClass = returnedType;
    this.getter = getter;
  }

  /**
   *  Invoke the getter method, if thre is one. Gets returned value.
   *  Stores it in the internal buffer, and returns it.
   *
   *  @return a value which is returned by the getter method
   *
   *  @throws java.lang.reflect.InvocationTargetException
   *             the exception which was thrown by the setter method
   *
   *  @throws IllegalAccessException
   *             if the value is not readable
   *
   *  @see java.lang.reflect.Method#invoke
   */
  public Object update() 
  throws java.lang.reflect.InvocationTargetException, IllegalAccessException
  {
    if (getter != null)
      try
      {
        value = getter.invoke(object);
	return value;
      }
      catch (IllegalAccessException e)
      {
	// this should not happen, the getter should be public method
        assert false;
	return null;
      }
    else
    {
      // not readable
      throw new IllegalAccessException();
    }
  }

}
