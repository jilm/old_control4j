package control4j;

/*
 *  Copyright 2013 Jiri Lidinsky
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

import static control4j.tools.LogMessages.*;

/**
 *  Template for classes that implements IConfigBuffer.
 *  This template provides methods for data types
 *  conversions. Classes inherited from this template
 *  must only override getString method.
 *  
 *  @see IConfigBuffer  
 */    
public abstract class ConfigBufferTemplate
{
  /**
   *  The only method that must be overriden to made
   *  the rest of the methods work. If config. item
   *  is not specified, the ConfigItemNotFoundException
   *  must be thrown. Message for this exception provides
   *  auxiliary method getItemNotFoundExceptionMessage.
   */
  abstract public String getString(String key) 
  throws ConfigItemNotFoundException;

  public int getInteger(String key) 
  throws ConfigItemNotFoundException, ConfigItemTypeException
  {
    String stringValue = getString(key);
    try
    {
      return Integer.parseInt(stringValue);
    }
    catch (NumberFormatException e)
    {
      String message = getMessage("cbt01", key, stringValue);
      throw new ConfigItemTypeException(message);
    }
  }

  public double getFloat(String key) 
  throws ConfigItemNotFoundException, ConfigItemTypeException
  {
    String stringValue = getString(key);
    try
    {
      return Double.parseDouble(stringValue);
    }
    catch (NumberFormatException e)
    {
      String message = getMessage("cbt02", key, stringValue);
      throw new ConfigItemTypeException(message);
    }
  }

  public String getString(String key, String defaultValue) 
  {
    try
    {
      return getString(key);
    }
    catch (ConfigItemNotFoundException e)
    {
      return defaultValue;
    }
  }

  public int getInteger(String key, int defaultValue) 
  throws ConfigItemTypeException
  {
    try
    {
      return getInteger(key);
    }
    catch (ConfigItemNotFoundException e)
    {
      return defaultValue;
    }
  }

  public double getFloat(String key, double defaultValue) 
  throws ConfigItemTypeException
  {
    try
    {
      return getFloat(key);
    }
    catch (ConfigItemNotFoundException e)
    {
      return defaultValue;
    }
  }

  /**
   *  Returns config item that is identified by parameter
   *  key converted to boolean. It expects that config
   *  item in text reprezentation contains either "true"
   *  or "false" values. Value must be lowercase.
   *  
   *  @param key identifier of the config item
   *  
   *  @return true if and only if text reprezentation
   *    of the item contains "true"; it returns false
   *    if and only if text reprezentation of the item
   *    contains "false".
   *    
   *  @throws ConfigItemNotFoundException if config item
   *    idenfied by key was not specified
   *    
   *  @throws ConfigItemTypeException if text representation
   *    contains neither "true" nor "false"                                                
   */
  public boolean getBoolean(String key) 
  throws ConfigItemNotFoundException, ConfigItemTypeException
  {
    String stringValue = getString(key);
    if (stringValue.equals("true")) 
      return true;
    if (stringValue.equals("false")) 
      return false;
    String message = getMessage("cbt03", key, stringValue);
    throw new ConfigItemTypeException(message);
  }
  
  public boolean getBoolean(String key, boolean defaultValue) 
  throws ConfigItemTypeException
  {
    try
    {
      return getBoolean(key);
    }
    catch (ConfigItemNotFoundException e)
    {
      return defaultValue;
    }
  }
  
  /**
   *  Returns message for use when ConfigItemNotFoundException
   *  is thrown. Those who overrides getString method are
   *  encouradged to use this method.
   *  
   *  @param key identifier of the config item that was failed
   *    to find and returned
   *    
   *  @return message that should be used when 
   *    ConfigItemNotFound exception is thrown
   */
  protected String getItemNotFoundExceptionMessage(String key)
  {
    return getMessage("cbt04", key);
  }

}
