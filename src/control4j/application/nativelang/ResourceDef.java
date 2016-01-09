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

import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static control4j.tools.LogMessages.getMessage;


import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Resource definition.
 *
 */
public class ResourceDef extends DescriptionBase implements IDefinition {

  public ResourceDef() {}

  /**
   *  Initialize fields of this object.
   */
  public ResourceDef(String className, String name, int scope) {
    setClassName(className);
    setName(name);
    setScope(scope);
  }

  /** Name of the java class that implements functionality of
      the resource */
  private String className;

  /**
   *  Returns the name of the java class that implements functionality
   *  of the resource.
   */
  public String getClassName() {
    //check();
    return className;
  }

  ResourceDef setClassName(String className) {
    this.className = className;
    return this;
  }

  /** Identification of this resource definition to be referenced. */
  private String name;
  private int scope;

  public String getName() {
    //check();
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getScope() {
    //check();
    return scope;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("className", className)
        .append("name", name)
        .append("scope", scope);
  }

  protected void check() {
    if (isBlank(className)) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message",
            "Class name of the resource def. may not be blank!")
        .set("name", name)
        .set("reference", getDeclarationReferenceText());
    }
    if (isBlank(name)) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "Name property of the resource def. may not be blank!")
        .set("class name", className)
        .set("reference", getDeclarationReferenceText());
    }
  }

}
