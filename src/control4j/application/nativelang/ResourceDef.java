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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
//import org.xml.sax.Attributes;

import control4j.application.Scope;

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
    check();
    return className;
  }

  ResourceDef setClassName(String className) {
    this.className = trim(notBlank(className, getMessage("msg004", "class",
        getDeclarationReferenceText())));
    return this;
  }

  /** Identification of this resource definition to be referenced. */
  private String name;
  private int scope;

  public String getName() {
    check();
    return name;
  }

  public void setName(String name) {
    this.name = trim(notBlank(name, getMessage("msg004", "name",
        getDeclarationReferenceText())));
  }

  public int getScope() {
    check();
    return scope;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  /**
   *  Transfer all of the settings into the given object.
   */
  public void translate(
      control4j.application.Resource resource, Scope localScope) {
    super.translate(resource, localScope);
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("className", className)
        .append("name", name)
        .append("scope", scope);
  }

  protected void check() {
    if (className == null) {
      throw new IllegalStateException(getMessage("msg002", "className",
          getDeclarationReferenceText()));
    }
    if (name == null) {
      throw new IllegalStateException(getMessage("msg002", "name",
          getDeclarationReferenceText()));
    }
  }

}
