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
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class Scanner
{

  public Map<String, Object> scan(Object object) throws Exception
  {
    Method[] methods = object.getClass().getMethods();
    Map<String, Object> result = new HashMap<String, Object>();
    for (Method method : methods)
    {
      Getter annotation = method.getAnnotation(Getter.class);
      if (annotation != null)
      {
        Object value = method.invoke(object);
        if (value instanceof Number || value instanceof String)
        {
          result.put(annotation.key(), value);
        }
        else
        {
	  Map<String, Object> descendant = scan(value);
	  if (descendant.size() == 0)
	    result.put(annotation.key(), value);
          else
            result.put(annotation.key(), descendant);
        }
      }
    }
    return result;
  }

  /**
   *
   */
  public static Map<String, Item2> scanClass(Class _class)
  {
    Map<String, Item2> result = new HashMap<String, Item2>();
    Method[] methods = _class.getMethods();
    for (Method method : methods)
    {
      Getter getterAnno = method.getAnnotation(Getter.class);
      if (getterAnno != null)
      {
        String key = getterAnno.key();
	Item2 item = result.get(key);
	if (item == null)
	{
	  item = new Item2(key);
	  result.put(key, item);
        }
	item.setGetter(method);
      }
      Setter setterAnno = method.getAnnotation(Setter.class);
      if (setterAnno != null)
      {
        String key = setterAnno.key();
	Item2 item = result.get(key);
	if (item == null)
	{
	  item = new Item2(key);
	  result.put(key, item);
	}
	item.setSetter(method);
      }
    }
    return result;
  }

  /**
   *
   */
  public static Map<String, Item> scanObject(Object object)
  {
    Map<String, Item> result = new HashMap<String, Item>();
    Method[] methods = object.getClass().getMethods();
    for (Method method : methods)
    {
      Getter getterAnno = method.getAnnotation(Getter.class);
      if (getterAnno != null)
      {
        String key = getterAnno.key();
	Item item = result.get(key);
	if (item == null)
	{
	  item = new Item(key, object);
	  result.put(key, item);
        }
	item.setGetter(method);
      }
      Setter setterAnno = method.getAnnotation(Setter.class);
      if (setterAnno != null)
      {
        String key = setterAnno.key();
	Item item = result.get(key);
	if (item == null)
	{
	  item = new Item(key, object);
	  result.put(key, item);
	}
	item.setSetter(method);
      }
    }
    return result;
  }

  /**
   *
   */
  public void printMap(Map<String, Object> map)
  {
    Set<String> keys = map.keySet();
    for (String key : keys)
    {
      Object value = map.get(key);
      if (value instanceof Number || value instanceof String)
      {
        System.out.println(key + ": " + value.toString());
      }
      else
      {
        System.out.println(key + ":");
        printMap((Map<String, Object>)value);
      }
    }
  }

  /**
   *
   */
  public static Method getSetter(Object object, String key)
  {
    Method[] methods = object.getClass().getMethods();
    for (Method method : methods)
    {
      Setter annotation = method.getAnnotation(Setter.class);
      if (annotation != null && annotation.key().equals(key))
        return method;
    }
    return null;
  }

  /**
   *  If the given method is annotated by Setter or Getter annotations,
   *  it returns the key. Otherwise returns null.
   */
  public static String getKey(Method method)
  {
    Getter getter = method.getAnnotation(Getter.class);
    if (getter != null)
      return getter.key();
    Setter setter = method.getAnnotation(Setter.class);
    if (setter != null)
      return setter.key();
    return null;
  }

}

