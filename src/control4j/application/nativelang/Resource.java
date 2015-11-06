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
import static org.apache.commons.lang3.StringUtils.isBlank;
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

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents a resource element inside the module.  This object has two
 *  variants.
 *  <ol>
 *    <li>Resource fully described inside the module.
 *    <li>Resource which refers to some resource definition.
 *  </ol>
 *  For the first variant the class name must be specified, for the second one
 *  the href and scope are mandatory fields.
 *
 */
public class Resource extends Configurable implements IReference {

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
    check();
    return className;
  }

  Resource setClassName(String className) {
    this.className = trim(className);
    return this;
  }

  private String href;

  public String getHref() {
    check();
    return href;
  }

  public void setHref(String href) {
    this.href = trim(href);
  }

  private int scope;

  public int getScope() {
    check();
    return scope;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  private boolean isReference;

  public boolean isReference() {
    check();
    return isReference;
  }

  /**
   *  Check inner consistency of the object. It means that either the class
   *  name or the href may not be blank. If everything is OK, nothing happens.
   *
   *  @throws CommonException
   *             if either both class name and href field contain blank values
   *             or both of them are not blank
   */
  public void check() {
    if (isBlank(href) && isBlank(className)) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "Either href or class name properties must be defined!")
        .set("reference", getDeclarationReferenceText());
    } else if (!isBlank(href) && !isBlank(className)) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "Both, href and class name are specified!")
        .set("reference", getDeclarationReferenceText())
        .set("href", href)
        .set("class name", className);
    } else {
      isReference = !isBlank(href);
    }
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
