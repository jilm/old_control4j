package control4j;

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

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

public class Resource
{

  public static Resource getResource(
      String className, IConfigBuffer configuration)
      throws ClassNotFoundException, NoSuchMethodException,
             IllegalAccessException, InvocationTargetException
  {
    Class resourceClass = Class.forName(className);
    return getResource(resourceClass, configuration);
  }

  public static Resource getResource(
      Class resourceClass, IConfigBuffer configuration)
      throws NoSuchMethodException, IllegalAccessException,
             InvocationTargetException
  {
    Resource resource = (Resource)MethodUtils.invokeStaticMethod(
        resourceClass, "getInstance", configuration);
    return resource;
  }

}
