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
import javax.swing.JComponent;
import control4j.gui.components.*;

/**
 *  Creates instances of new components. This class is singleton.
 */
class ComponentFactory
{

  private static final ComponentFactory instance = new ComponentFactory();

  private final HashMap<String, Class<? extends JComponent>> components
    = new HashMap<String, Class<? extends JComponent>>();

  private ComponentFactory()
  {
    components.put("Circle", Circle.class);
    components.put("Quantity", Quantity.class);
    components.put("Label", Label.class);
    components.put("Panel", Panel.class);
    components.put("Titled Panel", TitledPanel.class);
    components.put("Box Panel", Box.class);
    components.put("Rectangle", Rectangle.class);
    components.put("Triangle", Triangle.class);
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

  public JComponent createInstance(String name)
  {
    try
    {
      return components.get(name).newInstance();
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
