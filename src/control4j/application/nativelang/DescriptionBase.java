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

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Provides common interface for objects which contain description.
 *
 */
public abstract class DescriptionBase extends Configurable
{

  /** Contains description of the object. */
  protected String description;

  /**
   *  Sets the description.
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   *  Returns description that was previously assigned.
   *  May return null value.
   */
  public String getDescription()
  {
    return description;
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("description", description);
  }

}
