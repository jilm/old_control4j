package control4j.application.gui;

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
import java.util.Collection;

public class ComponentDeclaration extends Configurable
{
  private String className;
  private HashMap<String, String> signals = new HashMap<String, String>();

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public void setSignal(String key, String name)
  {
    signals.put(key, name);
  }

  public String getSignal(String key)
  {
    return signals.get(key);
  }

  public Collection<String> getSignalNames()
  {
    return signals.values();
  }

}
