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
import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.IXmlHandler;
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

import static control4j.tools.Logger.*;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents a resource element inside the module.
 *  This object has two variants.
 *  <ol>
 *    <li>Resource fully described inside the module.
 *    <li>Resource which refers to some resource definition.
 *  </ol>
 *
 */
public class Resource extends Configurable {

  public Resource() {}

  private String key;

  public String getKey() {
    return key;
  }

  Resource setKey(String key) {
    this.key = key;
    return this;
  }

  private String className;

  public String getClassName() {
    return className;
  }

  Resource setClassName(String className) {
    this.className = trim(className);
    return this;
  }

  private String href;

  public String getHref() {
    return href;
  }

  Resource setHref(String href) {
    this.href = trim(href);
    isReference = href != null;
    return this;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  Resource setScope(int scope) {
    this.scope = scope;
    return this;
  }

  private boolean isReference;

  public boolean isReference() {
    return isReference;
  }

  public void translate(
      control4j.application.Resource destination, Scope localScope) {
    super.translate(destination, localScope);
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("key", key)
        .append("className", className)
        .append("href", href)
        .append("scope", scope)
        .append("isReference", isReference);
  }

}
