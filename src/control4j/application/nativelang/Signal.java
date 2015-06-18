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

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.StringUtils.trim;

import java.util.ArrayList;

import control4j.application.Scope;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Stands for a signal element.
 *
 */
public class Signal extends DescriptionBase implements IDefinition {

  public Signal() {}

  private String name;

  public String getName() {
    if (name == null) {
      throw new IllegalStateException("Name attribute may not be null\n"
          + getDeclarationReferenceText());
    }
    return name;
  }

  public void setName(String name) {
    this.name = trim(notBlank(name,
        "Name attribute may not be blank" + getDeclarationReferenceText()));
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  /*
   *
   *    Value for time t-1 specification
   *
   */

  private boolean isValueT_1Specified = false;
  private boolean isValueT_1Valid = false;
  private String valueT_1;

  void setInvalidDefaultValue() {
    isValueT_1Specified = true;
    isValueT_1Valid = false;
  }

  void setDefaultValue(String value) {
    valueT_1 = trim(notBlank(value, "Default value may not be blank.\n"
        + getDeclarationReference()));
    isValueT_1Specified = true;
    isValueT_1Valid = true;
  }

  /*
   *
   *     Tags
   *
   */

  private ArrayList<Tag> tags;

  public void add(Tag tag) {
    if (tags == null) {
      tags = new ArrayList<Tag>();
    }
    tags.add(notNull(tag));
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope)
        .append("isValueT_1Specified", isValueT_1Specified);
    if (isValueT_1Specified) {
      builder.append("isValueT_1Valid", isValueT_1Valid);
      if (isValueT_1Valid) {
        builder.append("valueT_1", valueT_1);
      }
    }
    builder.append(tags);
  }

}
