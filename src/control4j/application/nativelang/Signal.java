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

import java.util.ArrayList;
import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.ParseException;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Stands for a signal element.
 *
 */
public class Signal extends DescriptionBase
{

  public Signal() {}

  private String name;

  public String getName() {
    return name;
  }

  Signal setName(String name) {
    this.name = notBlank(name);
    return this;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  Signal setScope(int scope) {
    this.scope = scope;
    return this;
  }

  /**
   *
   */
  public void translate(
      control4j.application.Signal destination, Scope localScope)
  {
    // translate configuration
    super.translate(destination, localScope);

    // translate value for the time t-1
    if (isValueT_1Specified)
    {
      if (isValueT_1Valid)
        destination.setValueT_1(valueT_1);
      else
        destination.setValueT_1Invalid();
    }

    // translate tag objects
    if (tags != null)
      for (Tag tag : tags)
      {
        control4j.application.Tag destTag = new control4j.application.Tag();
        tag.translate(destTag, localScope);
        destination.putTag(tag.getName(), destTag);
      }

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
    isValueT_1Specified = true;
    isValueT_1Valid = true;
    valueT_1 = value;
  }

  /*
   *
   *     Tags
   *
   */

  private ArrayList<Tag> tags = new ArrayList<Tag>();

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("name", name)
        .append("scope", scope);
    // TODO:
  }

}
