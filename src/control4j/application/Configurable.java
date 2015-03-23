package control4j.application;

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

import java.util.HashMap;
import control4j.IConfigBuffer;

/**
 *
 *  A common base for all of the objects that are configurable.
 *
 */
public abstract class Configurable extends DeclarationBase
{

  /** Contains configuration. */
  private ConfigBuffer configuration;

  /**
   *  Puts given property into the internal buffer.
   */
  public void putProperty(String key, String value)
  {
    if (configuration == null)
      configuration = new ConfigBuffer();
    configuration.put(key, value);
    // TODO duplicite keys
  }

  /** Configuration in the form of references to some definition. */
  private HashMap<String, Reference> references;

  /**
   *  Puts given property into the internal buffer.
   *  this method accepts a property in the form of
   *  reference to some definition.
   */
  public void putProperty(String key, String href, Scope scope)
  {
    if (references == null)
      references = new HashMap<String, Reference>();
    references.put(key, new Reference(href, scope));
    // TODO duplicite keys
  }

  public IConfigBuffer getConfiguration()
  {
    return configuration;
  }

}
