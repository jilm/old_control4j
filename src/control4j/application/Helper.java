package control4j.application;

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

import java.util.Set;
import java.util.HashMap;

class Helper
{

  static void toString(
      HashMap<String, Reference> map, String indent, StringBuilder sb)
  {
    Set<String> keys = map.keySet();
    for (String key : keys)
      sb.append(indent)
	.append(key)
	.append('=')
	.append(map.get(key).toString())
	.append("\n");
  }

  static void objectToString(HashMap<String, ? extends ObjectBase> map, 
      String indent, StringBuilder sb)
  {
    Set<String> keys = map.keySet();
    for (String key : keys)
    {
      sb.append(indent)
	.append(key)
	.append('=');
      map.get(key).toString(indent, sb);
    }
  }

}
