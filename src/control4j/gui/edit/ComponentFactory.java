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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import control4j.gui.components.*;
import control4j.gui.GuiObject;
import static control4j.tools.Logger.*;

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

  private static final String filename = "guicomponents.csv";

  private static final String delimiter = ",";

  private ComponentFactory()
  {
    try
    {
      loadComponentList();
    }
    catch (IOException e)
    {
      catched("ComponentFactory", "init", e);
    }
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

  /**
   *  Loads component list from a file.
   */
  private void loadComponentList() throws IOException
  {
    InputStream is = ComponentFactory.class.getResourceAsStream(filename);
    if (is == null) return;  // TODO
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line = reader.readLine();
    while (line != null)
    {
      String[] items = line.split(delimiter);
      components.put(items[0], items[1]);
      line = reader.readLine();
    }
    reader.close();
  }

}
