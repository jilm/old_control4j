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

import control4j.application.Scope;

/**
 *
 *  Provides common interface for objects which contain
 *  configuration.
 *
 */
abstract class Configurable extends DeclarationBase
{

  private ArrayList<Property> properties;

  public void addProperty(Property property)
  {
    if (properties == null)
      properties = new ArrayList<Property>();
    properties.add(property);
  }

  public void translate(
      control4j.application.Configurable destination, Scope localScope)
  {
    if (properties != null)
      for (Property property : properties)
	if (property.isReference())
        {
	  destination.putProperty(property.getKey(), property.getHref(), 
	      resolveScope(property.getScope(), localScope));
        }
	else
	{
	  destination.putProperty(property.getKey(), property.getValue());
	}
  }

  protected static Scope resolveScope(int code, Scope localScope)
  {
    switch (code)
    {
      case 0:
	return Scope.getGlobal();
      case 1:
	return localScope;
      case 2:
        return localScope.getParent();
      default:
	throw new IllegalArgumentException();
    }
  }

}
