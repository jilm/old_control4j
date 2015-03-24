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
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

import control4j.ConfigBufferTemplate;
import control4j.ConfigItemNotFoundException;
import control4j.IConfigBuffer;
import control4j.tools.DuplicateElementException;

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
public abstract class Configurable extends DeclarationBase
{

  /** A set that contains all of the keys. It is used for
      duplicate elements detection. */ 
  private HashSet<String> configKeys;

  /**
   *  Returns true if and only if the key is already defined.
   */
  private boolean containsKey(String key)
  {
    if (key == null)
      throw new IllegalArgumentException();
    if (configKeys == null)
      return false;
    else
      return configKeys.contains(key);
  }

  /**
   *  Adds a given key into the internal buffer.
   */
  private void addKey(String key)
  {
    if (key == null)
      throw new IllegalArgumentException();
    if (configKeys == null)
      configKeys = new HashSet<String>();
    configKeys.add(key);
  }

  /** Contains configuration. */
  private ConfigBuffer configuration;

  /** An empty configuration buffer. */
  protected static EmptyConfiguration emptyConfiguration;

  /**
   *  Puts given configuration item into the internal
   *  configuration buffer.
   */
  private void putConfigItem(String key, String value)
  {
    if (key == null || value == null)
      throw new IllegalArgumentException();
    if (configuration == null)
      configuration = new ConfigBuffer();
    configuration.put(key, value);
  }

  /**
   *  Returns either the configuration or empty configuration
   *  object. The result is not meant to add elements.
   */
  private IConfigBuffer getConfigurationBuffer()
  {
    if (configuration != null)
      return configuration;
    else
    {
      if (emptyConfiguration == null)
	emptyConfiguration = new EmptyConfiguration();
      return emptyConfiguration;
    }
  }

  /**
   *  Puts given property into the internal buffer.
   *
   *  @throws DuplicateElementException
   *             if there already is a config item with given key
   *             under this object
   */
  public void putProperty(String key, String value)
  throws DuplicateElementException
  {
    // detect duplicate keys
    if (containsKey(key))
      throw new DuplicateElementException();
    // store the configuration
    addKey(key);
    putConfigItem(key, value);
  }

  /**
   *  Returns resolved configuration.
   */
  public IConfigBuffer getConfiguration()
  {
    return getConfigurationBuffer();
  }

  /** Configuration in the form of references to some definition. */
  private HashMap<String, Reference> references;

  private static Set<String> emptySet;

  /**
   *  Puts configuration in the form of reference into the
   *  internal buffer.
   */
  private void putReference(String key, Reference value)
  {
    if (key == null || value == null)
      throw new IllegalArgumentException();
    // lazy data storage creation
    if (references == null)
      references = new HashMap<String, Reference>();
    // store the configuration
    references.put(key, value);
  }

  /**
   *  Remove reference config item with given key from the internal
   *  buffer.
   */
  private void removeReference(String key)
  {
    if (key == null)
      throw new IllegalArgumentException();
    if (references == null)
      throw new NoSuchElementException();
    if (!references.containsKey(key))
      throw new NoSuchElementException();
    references.remove(key);
  }

  /**
   *  Puts given property into the internal buffer.
   *  this method accepts a property in the form of
   *  reference to some definition.
   *
   *  @throws DuplicateElementException
   *             if there already is a config item with given key
   *             under this object
   */
  public void putProperty(String key, String href, Scope scope)
  throws DuplicateElementException
  {
    // detect duplicate keys
    if (containsKey(key))
      throw new DuplicateElementException();
    // store the configuration
    addKey(key);
    putReference(key, new Reference(href, scope));
  }

  /**
   *  Returns a set of all the configuration items which were
   *  inserted in the form of reference.
   */
  public Set<String> getReferenceConfigKeys()
  {
    if (references == null)
    {
      if (emptySet == null)
	emptySet = new HashSet<String>();
      return emptySet;
    }
    else
    {
      return references.keySet();
    }
  }

  public Reference getReferenceConfigItem(String key)
  {
    if (references == null)
      throw new NoSuchElementException();
    if (!references.containsKey(key))
      throw new NoSuchElementException();
    return references.get(key);
  }

  public void resolveConfigItem(String key, String value)
  {
    removeReference(key);
    putConfigItem(key, value);
  }

  /**
   *
   *  An empty configuration collection.
   *
   */
  protected static class EmptyConfiguration 
  extends ConfigBufferTemplate
  implements IConfigBuffer
  {

    /**
     *  Always throws ConfigItemNotFoundException exception.
     */
    public String getString(String key)
    throws ConfigItemNotFoundException
    {
      throw new ConfigItemNotFoundException(key);
    }

  }

}
