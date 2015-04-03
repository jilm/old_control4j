package control4j.application;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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
import java.util.Set;

import control4j.tools.DeclarationReference;

/**
 *
 *  Definition of the signal.
 *
 */
public class Signal extends Configurable
{

  /**
   *  Crate a new empty signal definition object.
   */
  public Signal()
  { }

  /*
   *
   *     Value for time T-1
   *
   */

  private boolean isValueT_1 = false;
  private boolean isValueT_1Valid = false;
  private String valueT_1;

  public void setValueT_1Invalid()
  {
    isValueT_1 = true;
    isValueT_1Valid = false;
    valueT_1 = null;
  }

  public void setValueT_1(String value)
  {
    isValueT_1 = true;
    isValueT_1Valid = true;
    valueT_1 = value;
  }

  /*
   *
   *    Tags
   *
   */

  private HashMap<String, Tag> tags = new HashMap<String, Tag>();

  public void putTag(String name, Tag tag)
  {
    tags.put(name, tag);
  }

  public Set<String> getTagNames()
  {
    return tags.keySet();
  }

  public Tag getTag(String name)
  {
    return tags.get(name);
  }


  void toString(String indent, StringBuilder sb)
  {
    sb.append("\n");
    String indent2 = indent + "  ";

    // write configuration
    super.toString(indent, sb);

    // print T-1 value
    if (isValueT_1)
    {
      if (isValueT_1Valid)
	sb.append(indent)
	  .append("Value for time T-1=")
	  .append(valueT_1)
	  .append("\n");
      else
	sb.append(indent)
	  .append("Value for time T-1 is invalid\n");
    }

    // write tags
    if (tags != null && tags.size() > 0)
    {
      sb.append(indent).append("Tags\n");
      Helper.objectToString(tags, indent2, sb);
    }
  }

}
