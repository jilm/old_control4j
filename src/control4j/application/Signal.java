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

  private HashMap<String, Tag> tags;

  public void putTag(String name, Tag tag)
  {
    if (tags == null) tags = HashMap<String, Tag>();
    tags.put(name, tag);
  }

}
