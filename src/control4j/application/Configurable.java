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

import static cz.lidinsky.tools.Validate.notBlank;

import java.util.HashMap;
import java.util.Map;
import control4j.IConfigBuffer;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  A common base for all of the objects that are configurable.
 *  Each configuration item consists of a key and value. The
 *  key must be unique acros the configurable object.
 *
 *  <p>This object contains two kinds of configuration items.
 *  Configuration that was given in the form of key and value,
 *  and the one that was given in the form of reference to
 *  some define object.
 *
 */
public abstract class Configurable extends DeclarationBase {

  /**
   *  An empty constructor.
   */
  public Configurable() { }

  /**
   *  Internal storage for the configuration.
   */
  private HashMap<String, Property> configuration
      = new HashMap<String, Property>();

  /**
   *  Returns true if and only if the key is already defined.
   */
  public boolean containsKey(String key) {
    if (key == null) {
      throw new IllegalArgumentException();
    } else {
      return configuration.containsKey(key);
    }
  }

  /**
   *  Puts given property into the internal buffer.
   */
  public Property putProperty(String key, String value) {
    if (containsKey(key)) {
      Property property = configuration.get(key);
      property.setValue(value);
      return property;
    } else {
      Property property = new Property();
      property.setValue(value);
      configuration.put(key, property);
      return property;
    }
  }

  /**
   *  Returns the property value which is asociated with given key. This is
   *  simply a shorthand for <code>getProperty(key).getValue()</code>.
   *
   *  @param key
   *             identification of the value that is required
   *
   *  @return the value which was stored under the given key
   *
   *  @throws CommonException
   *             if the parameter key is blank
   *
   *  @throws CommonException (NO_SUCH_ELEMENT)
   *             if there is not a property with given key
   */
  public String getValue(String key) {
    Property property = getProperty(key);
    if (property != null) {
      return property.getValue();
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.NO_SUCH_ELEMENT)
        .set("message", "There is not a property under the given key")
        .set("key", key);
    }
  }

  /**
   *  Returns property that is stored under the given key or <code>null</code>
   *  if there is no property with given key.
   *
   *  @param key
   *             required value identification
   *
   *  @return the value that is asociated with given key
   */
  public Property getProperty(String key) {
    return configuration.get(key);
  }

  /**
   *  Returns resolved configuration.
   */
  public IConfigBuffer getConfiguration() {
    ConfigBuffer buffer = new ConfigBuffer();
    for (Map.Entry<String, Property> entry : configuration.entrySet()) {
      buffer.put(entry.getKey(), entry.getValue().getValue());
    }
    return buffer;
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("configuration", configuration);
  }

  public void putConfiguration(Configurable source) {
    if (source != null) {
      this.configuration.putAll(source.configuration);
    }
  }

}
