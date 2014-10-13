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

//import java.util.Iterator;
import java.lang.reflect.Method;

public class MethodIterator implements Iterable<Method>
{

  private Object object;
  private Class annotationClass;
  private Class valueClass;

  /**
   *  Iterate throught all of the methods of given object which are
   *  annotated by the given annotation and which gets or sets given
   *  data type.
   */
  public MethodIterator(Object object, Class annotationClass, Class valueClass)
  {
    this.object = object;
    this.annotationClass = annotationClass;
    this.valueClass = valueClass;
  }

  public java.util.Iterator<Method> iterator()
  {
    return new Iterator();
  }

  private class Iterator implements java.util.Iterator<Method>
  {
    private Method[] methods;
    private int index = 0;

    Iterator()
    {
      methods = object.getClass().getMethods();
      findNext();
    }

    public boolean hasNext()
    {
      return index < methods.length;
    }

    private void findNext()
    {
      while (index < methods.length)
      {
        Object annotation = methods[index].getAnnotation(annotationClass);
	if (annotation != null)
	{
	  if (valueClass == null)
	    break;
	  else
	  {
	    Class[] params = methods[index].getParameterTypes();
	    if (valueClass.isAssignableFrom(params[0]))
	      break;
	  }  
	}
        index++;
      }
    }

    public Method next()
    {
      Method result = methods[index];
      index++;
      findNext();
      return result;
    }

    public void remove()
    {
    }

  }
  private class SetterIterator implements java.util.Iterator<Method>
  {
    private Method[] methods;
    private int index = 0;

    SetterIterator()
    {
      methods = object.getClass().getMethods();
      findNext();
    }

    public boolean hasNext()
    {
      return index < methods.length;
    }

    private void findNext()
    {
      while (index < methods.length)
      {
        Setter annotation = methods[index].getAnnotation(Setter.class);
	if (annotation != null)
	{
	  if (valueClass == null)
	    break;
	  else
	  {
	    Class[] params = methods[index].getParameterTypes();
	    if (valueClass.isAssignableFrom(params[0]))
	      break;
	  }  
	}
        index++;
      }
    }

    public Method next()
    {
      Method result = methods[index];
      index++;
      findNext();
      return result;
    }

    public void remove()
    {
    }

  }

}
