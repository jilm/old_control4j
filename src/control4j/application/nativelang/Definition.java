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

import java.text.MessageFormat;

/**
 *
 *  An immutable object which holds content of the define XML element.
 *  It serves for defining values which may be than referenced by
 *  the property elements. It is simply a crate object.
 *
 */
public class Definition extends DeclarationBase
{

  /** A value. */
  private String value;

  /**
   *  Assign given parameters.
   *
   *  @param value
   *             a value
   *
   *  @throws NullPointerException
   *             if some of the given parameters has null value
   */
  public Definition(String value)
  {
    if (value == null)
      throw new NullPointerException();
    this.value = value;
  }

  /**
   *  Returns value.
   *
   *  @return value
   */
  public String getValue()
  {
    return value;
  }

  /**
   *  Returns a text which contains a class name, name, value and scope
   *  information.
   */
  @Override
  protected String getDefaultObjectIdentification()
  {
    return MessageFormat.format(
	"An instance of {0} class; value: {1}",
	this.getClass().getName(), value);
  }

  /**
   *  Returns a text which contains a class name, name, value and scope
   *  information.
   */
  @Override
  public String toString()
  {
    return MessageFormat.format(
	"An instance of {0} class; value: {1}",
	this.getClass().getName(), value);
  }

}
