package control4j.application;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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
  private HashMap<String, String> buffer = new HashMap<String, String>();

  public String getString(String key) throws ConfigItemNotFoundException
  {
    String value = buffer.get(key);
    if (value == null)
    {
      String message = getItemNotFoundExceptionMessage(key);
      throw new ConfigItemNotFoundException(message);
    }
    return value;
  }
  
  /**
   *  Puts new config item into the buffer. Config item is identified
   *  by key. If there was a config item with the same key,
   *  previously contained value will be replaced by this one.
   *  
   */ 
  public void put(String key, String value)
  {
    buffer.put(key, value);
  }
  
  public int size()
  {
    return buffer.size();
  }

  @Override
  public String toString()
  {
    return buffer.toString();
  }

  void toString(String indent, StringBuilder sb)
  {
    java.util.Set<String> keys = buffer.keySet();
    for (String key : keys)
      sb.append(indent)
	.append(key)
	.append('=')
	.append(buffer.get(key))
	.append("\n");
  }

}
