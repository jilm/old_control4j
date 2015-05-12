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
 *  Represents a property of some higher level object.
 *  This object has two variants.
 *  <ol>
 *    <li>Property which directly contains a value.
 *    <li>Property which refers to some define object.
 *  </ol>
 *
 */
public class Property extends DeclarationBase {

  public Property() {}

  private String key;

  public String getKey() {
    return key;
  }

  Property setKey(String key) {
    this.key = key;
    return this;
  }

  /** Value of the property. */
  private String value;

  /**
   *  Returns the value of the property.
   */
  public String getValue() {
    return value;
  }

  Property setValue(String value) {
    this.value = value;
    this.isReference = false;
    return this;
  }

  private String href;

  public String getHref() {
    return href;
  }

  Property setHref(String href) {
    this.value = href;
    this.isReference = true;
    return this;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  Property setScope(int scope) {
    this.scope = scope;
    return this;
  }

  private boolean isReference;

  public boolean isReference() {
    return isReference;
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("key", key)
        .append("value", value)
        .append("href", href)
        .append("scope", scope)
        .append("isReference", isReference);
  }

}
