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

import static org.apache.commons.lang3.Validate.notBlank;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Contains declaration of a resource.
 *
 */
public class Resource extends Configurable
{

  private String className;

  public Resource(String className)
  {
    this.className = notBlank(className,
        "Class name of the resource may not be blank!");
  }

  /**
   *  Returns a name of class that implements functionality
   *  of the resource.
   */
  public String getClassName()
  {
    return className;
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("className", className);
  }

}
