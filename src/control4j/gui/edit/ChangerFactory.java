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

import java.util.HashMap;
import control4j.gui.changers.*;

/**
 *  Creates instances of new changers. This class is singleton.
 */
class ChangerFactory
{

  private static final ChangerFactory instance = new ChangerFactory();

  private final HashMap<String, Class<? extends Changer>> changers
    = new HashMap<String, Class<? extends Changer>>();

  private ChangerFactory()
  {
    changers.put("LED", Led.class);
    changers.put("Quantity", SetQuantity.class);
    changers.put("Move", Move.class);
    changers.put("Set text", SetText.class);
  }

  /**
   *  Returns instance of the component factory
   */
  public static ChangerFactory getInstance()
  {
    return instance;
  }

  /**
   *  Returns a list of all the changer objects in human readable form.
   */
  public String[] getList()
  {
    String[] list = new String[changers.size()];
    list = changers.keySet().toArray(list);
    return list;
  }

  public Changer createInstance(String name)
  {
    try
    {
      return changers.get(name).newInstance();
    }
    catch (InstantiationException e)
    {
      // should not happen
      assert false;
    }
    catch (IllegalAccessException e)
    {
      // should not happen as well
      assert false;
    }
    return null;
  }

}
