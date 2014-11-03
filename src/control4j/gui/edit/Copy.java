package control4j.gui.edit;

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
import java.util.Map;
import java.util.Collection;
import control4j.scanner.Scanner;
import control4j.scanner.Item;

/**
 *  Provides a copy of the given component.
 */
public class Copy
{

  public static Object copy(Object model)
  {
    try
    {
      // create an object of the same class
      Class _class = model.getClass();
      Constructor constructor = _class.getConstructor();
      Object copy = constructor.newInstance();
      // make a copy of all the methods annotated as setters and getters
      Scanner scanner = new Scanner();
      Map<String, Item> modelScan = scanner.scanObject(model);
      Map<String, Item> copyScan = scanner.scanObject(copy);
      Collection<Item> copyItems = copyScan.values();
      for (Item copyItem : copyItems)
        if (copyItem.isWritable())
	  copyItem.setValue(modelScan.get(copyItem.getKey()).getValue());
      // return the copy
      return copy;
    }
    catch (NoSuchMethodException e)
    {
    }
    catch (InstantiationException e)
    {
      // this should not happen
      assert false;
    }
    catch (IllegalAccessException e)
    {
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
    }
    return null;
  }

}
