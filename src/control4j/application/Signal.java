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


import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.Validate;
import cz.lidinsky.tools.CommonException;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;


/**
 *
 *  Keeps signal definition.
 *
 *  <p>This object provides following informations for use by the modules:
 *
 *  <ul>
 *
 *    <li> <em>Label</em> is the text identification which is intended mainly
 *    for MMI clients.
 *
 *  </ul>
 *
 */
public class Signal extends Configurable {

  private final String name;

  /**
   *  Crate a new empty signal definition object. The parameter name must be
   *  the name specified inside the application document.
   */
  public Signal(String name) {
    try {
    this.name = Validate.notBlank(name);
    } catch (Exception e) {
      throw new CommonException()
        .set("message", "The name of the signal may not be blank!")
        .set("name", name);
    }
  }

  //----------------------------------------------------- Default Signal Value.

  private boolean isValueT_1 = false;
  private boolean isValueT_1Valid = false;
  private String valueT_1;

  public void setValueT_1Invalid() {
    isValueT_1 = true;
    isValueT_1Valid = false;
    valueT_1 = null;
  }

  public void setValueT_1(String value) {
    isValueT_1 = true;
    isValueT_1Valid = true;
    valueT_1 = value;
  }

  public boolean isValueT_1Specified() {
    return isValueT_1;
  }

  public boolean isValueT_1Valid() {
    return isValueT_1Valid;
  }

  public double getValueT_1() {
    return Double.parseDouble(valueT_1);
  }

  //--------------------------------------------------------------------- Tags.

  private HashMap<String, Tag> tags = new HashMap<String, Tag>();

  public void putTag(String name, Tag tag) {
    tags.put(name, tag);
  }

  public Set<String> getTagNames() {
    return tags.keySet();
  }

  public Tag getTag(String name) {
    return tags.get(name);
  }

  //-------------------------------------------------------------------- Label.

  private String label;

  /**
   *  Returns the label, which is short textual identification of the signal
   *  which is dedicated mainly for use by the MMI clients and programs. If the
   *  label has not been set yet, or if it has been set to blank value, the
   *  name is returned instead.
   */
  public String getLabel() {
    return StringUtils.isBlank(label) ? name : label;
  }

  /**
   *  Sets the label property of the signal.
   */
  public void setLabel(String label) {
    this.label = label;
  }

  //--------------------------------------------------------------------- Unit.

  private String unit;

  /**
   *  Returns unit of the signal.
   */
  public String getUnit() {
    return unit == null ? "" : unit;
  }

  /**
   *  Sets the unit of the signal.
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  //-------------------------------------------------------------------- Other.

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("isValueT_1", isValueT_1)
        .append("isValueT_1Valid", isValueT_1Valid)
        .append("valueT_1", valueT_1)
        .append("tags", tags);
  }

}
