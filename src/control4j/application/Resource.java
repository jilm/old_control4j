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
    this.className = className;
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
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Resource Definition{class=");
    sb.append(className);
    sb.append(", configuration={");
    sb.append(super.toString());
    sb.append("}");
    return sb.toString();
  }

  @Override
  void toString(String indent, StringBuilder sb)
  {
    sb.append("\n")
      .append(indent)
      .append("class = ")
      .append(className)
      .append("\n");
    super.toString(indent, sb);
  }

}
