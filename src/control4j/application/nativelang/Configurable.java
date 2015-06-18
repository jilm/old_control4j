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
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.ArrayList;
import java.util.Collection;

import control4j.application.Scope;
import control4j.tools.DuplicateElementException;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringStyle;

/**
 *
 *  Provides common interface for objects which contain
 *  configuration.
 *
 */
abstract class Configurable
extends DeclarationBase implements IToStringBuildable {

  private ArrayList<Property> properties;

  public void addProperty(Property property) {
    notNull(property);
    if (properties == null) {
      properties = new ArrayList<Property>();
    }
    properties.add(property);
  }

  public Collection<Property> getConfiguration() {
    return emptyIfNull(properties);
  }

  public void put(Property property) {
    addProperty(property);
  }

  @Override
  public void toString(ToStringBuilder builder) {
    builder.append("properties", properties);
  }

}
