package control4j.application;

/*
 *  Copyright 2013 Jiri Lidinsky
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
import control4j.IConfigBuffer;
import control4j.ConfigItemNotFoundException;
import control4j.ConfigItemTypeException;
import control4j.ConfigBufferTemplate;

public class ConfigBuffer extends ConfigBufferTemplate implements IConfigBuffer
{
  private HashMap<String, Property> buffer = new HashMap<String, Property>();

  public String getString(String key) throws ConfigItemNotFoundException
  {
    Property property = buffer.get(key);
    if (property == null)
    {
      String message = getItemNotFoundExceptionMessage(key);
      throw new ConfigItemNotFoundException(message);
    }
    return property.getValue();
  }
  
  /**
   *  Puts new config item into the buffer. Config item is identified
   *  by key. If there was a config item with the same key,
   *  previously contained value will be replaced by this one.
   *  
   *  @param property 
   *             key value paire of the new config item.
   */ 
  public void put(Property property)
  {
    buffer.put(property.getKey(), property);
  }
  
  public int size()
  {
    return buffer.size();
  }

}
