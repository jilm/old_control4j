package control4j.application.nativelang;

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

/**
 *
 *  Represents a property of some higher level object.
 *  Property is simply the String value. This object
 *  is immutable.
 *
 */
public class Property extends DeclarationBase
{

  /** Value of the property. */
  protected String value;

  /**
   *  Sets the value of the property.
   */
  public Property(String value)
  {
    this.value = value;
  }

  /**
   *  Returns the value of the property.
   */
  public String getValue()
  {
    return value;
  }

}
