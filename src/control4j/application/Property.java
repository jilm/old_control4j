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

/**
 *  A crate class to hold on configuration items of modules and resources.
 *  It contains key, value paire together with declaration reference.
 */
public class Property extends DeclarationBase
{
  private String key;
  private String value;

  @Deprecated
  public Property(String key, String value, String declarationReference)
  {
    this.key = key;
    this.value = value;
    setDeclarationReference(declarationReference);
  }

  public Property(String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  public String getKey()
  {
    return key;
  }

  public String getValue()
  {
    return value;
  }

  @Override
  protected DeclarationReference getThisObjectIdentification()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("property (key: ")
      .append(key)
      .append("; value: ")
      .append(value)
      .append(')');
    return new DeclarationReference(sb.toString());
  }

}
