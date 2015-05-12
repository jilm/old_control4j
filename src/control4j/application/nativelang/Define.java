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
 *  Represents define element
 *
 */
public class Define extends DeclarationBase {

  public Define() {}

  private String name;

  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  void setScope(int scope) {
    this.scope = scope;
  }

  private String value;

  public String getValue() {
    return value;
  }

  void setValue(String value) {
    this.value = value;
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope)
        .append("value", value);
  }

}
