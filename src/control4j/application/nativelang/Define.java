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
import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.StringUtils.trim;

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
    if (name == null || value == null) {
      throw new IllegalStateException("A name or a value attribute missing!"
          + getDeclarationReferenceText());
    }
    return name;
  }

  Define setName(final String name) {
    this.name = trim(notBlank(name,
        "Name attribute may not be empty\n" + getDeclarationReferenceText()));
    return this;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  Define setScope(final int scope) {
    inclusiveBetween(0, 1, scope, "Scope attribute must be between 0 and 1\n"
        + getDeclarationReferenceText());
    this.scope = scope;
    return this;
  }

  private String value;

  public String getValue() {
    if (name == null || value == null) {
      throw new IllegalStateException("A name or a value attribute missing!"
          + getDeclarationReferenceText());
    }
    return value;
  }

  Define setValue(final String value) {
    this.value = notNull(value, "Value attribute may not be null value\n"
        + getDeclarationReferenceText());
    return this;
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope)
        .append("value", value);
  }

}
