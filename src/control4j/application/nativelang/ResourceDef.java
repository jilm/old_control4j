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

import java.util.ArrayList;
import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.IXmlHandler;
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Resource definition.
 *
 */
public class ResourceDef extends DescriptionBase
{

  public ResourceDef() {}

  /** Name of the java class that implements functionality of
      the resource */
  private String className;

  /**
   *  Returns the name of the java class that implements functionality
   *  of the resource.
   */
  public String getClassName() {
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
    return name;
  }

  ResourceDef setName(String name) {
    this.name = name;
    return this;
  }

  public int getScope() {
    return scope;
  }

  ResourceDef setScope(int scope) {
    this.scope = scope;
    return this;
  }

  /**
   *  Initialize fields of this object.
   */
  public ResourceDef(
       String className, String name, int scope)
  {
    this.className = className;
    this.name = name;
    this.scope = scope;
  }

  /**
   *  Transfer all of the settings into the given object.
   */
  public void translate(
      control4j.application.Resource resource, Scope localScope)
  {
    super.translate(resource, localScope);
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("className", className)
        .append("name", name)
        .append("scope", scope);
  }

}
