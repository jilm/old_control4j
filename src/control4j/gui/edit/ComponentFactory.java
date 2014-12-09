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
import control4j.gui.components.*;
import control4j.gui.GuiObject;

/**
 *
 *  Creates instances of new components. This class is singleton.
 *
 */
class ComponentFactory
{

  private static final ComponentFactory instance = new ComponentFactory();

  private final HashMap<String, String> components
    = new HashMap<String, String>();

  private ComponentFactory()
  {
    components.put("Circle", "control4j.gui.components.Circle");
    components.put("Quantity", "control4j.gui.components.Quantity");
    components.put("Label", "control4j.gui.components.Label");
    components.put("Panel", "control4j.gui.components.Panel");
    components.put("Titled Panel", "control4j.gui.components.TitledPanel");
    components.put("Box Panel", "control4j.gui.components.Box");
    components.put("Rectangle", "control4j.gui.components.Rectangle");
    components.put("Triangle", "control4j.gui.components.Triangle");
    components.put("Box Filler", "control4j.gui.components.BoxFiller");
    components.put("Graph", "control4j.gui.components.Graph");
    components.put("Pump", "control4j.gui.components.Pump");
    components.put("Three-way valve", "control4j.gui.components.ThreeWayValve");
    components.put("Valve", "control4j.gui.components.Valve");
    components.put("Line", "control4j.gui.components.Line");
  }

  /**
   *  Returns instance of the component factory
   */
  public static ComponentFactory getInstance()
  {
    return instance;
  }

  /**
   *  Returns a list of all the components in human readable form.
   */
  public String[] getComponentList()
  {
    String[] list = new String[components.size()];
    list = components.keySet().toArray(list);
    return list;
  }

  public GuiObject createInstance(String name)
  {
    try
    {
      return (GuiObject)Class.forName(components.get(name))
	.newInstance();
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
    catch (ClassNotFoundException e)
    {
      assert false;
    }
    return null;
  }

}
