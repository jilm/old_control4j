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

import static org.apache.commons.lang3.Validate.notBlank;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Contains declaration of a resource.
 *
 */
public class Resource extends Configurable {

  private String className;

  public Resource(String className) {
    this.className = className;
  }

  public Resource() {}

  public Resource setClassName(String className) {
    this.className = className;
    return this;
  }

  /**
   *  Returns a name of class that implements functionality
   *  of the resource.
   *
   *  @throws CommonException
   *             if the className is null or blank value
   */
  public String getClassName() {
    check();
    return className;
  }

  /**
   *  Check the internal consistency of the object. If it is OK, nothing
   *  happens, otherwise the exception is thrown. For this object, the
   *  className may not be blank.
   */
  public void check() {
    try {
      notBlank(className);
    } catch (Exception e) {
      throw new CommonException()
        .setCause(e)
        .set("message", "The class of the resource must be defined!")
        .set("reference", getDeclarationReferenceText());
    }
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("className", className);
  }

}
