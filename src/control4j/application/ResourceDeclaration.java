package control4j.application;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

public class ResourceDeclaration extends DeclarationBase
{
  private ConfigBuffer configuration;
  private String className;
  private String name;

  public ResourceDeclaration(String className, String name)
  {
    this.className = className;
    this.name = name;
    this.configuration = new ConfigBuffer();
  }

  public ConfigBuffer getConfiguration()
  {
    return configuration;
  }
  
  public void setConfigItem(Property property)
  {
    configuration.put(property);
  }

  public String getClassName()
  {
    return className;
  }

  public String getName()
  {
    return name;
  }

  /**
   *  Adds an information that this is a resource.
   */
  @Override
  public void setDeclarationReference(DeclarationReference reference)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("resource (name: ")
      .append(name)
      .append(')');
    if (reference != null)
    {
      sb.append(", declared");
      super.setDeclarationReference(reference.addText(sb.toString()));
    }
    else
    {
      reference = new DeclarationReference(sb.toString());
      super.setDeclarationReference(reference);
    }
  }

}
