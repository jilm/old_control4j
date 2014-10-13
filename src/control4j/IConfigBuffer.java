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

/**
 *  Defines api for access to a configuration that was read
 *  from configuration file. Each configuration item is 
 *  identified by a unique key. Key is a String data type. 
 *  Value associated with a key mey be of various data types.
 */ 
public interface IConfigBuffer
{
  /**
   *  @param key identification of the configuration
   *    item.    
   *       
   *  @return value associated with given key
   *    transformed to integer number.
   *    
   *  @throws ConfigItemNotFoundException if
   *    configuration item with a given key
   *    was not specified in the config file.
   *    
   *  @throws ConfigItemTypeException if value
   *    cannot be trasformed to integer number.
   */                                 
  public int getInteger(String key) 
    throws ConfigItemNotFoundException, ConfigItemTypeException;

  /**
   *  @param key identification of the configuration
   *    item.    
   *       
   *  @return value associated with given key
   *    transformed to real number.
   *    
   *  @throws ConfigItemNotFoundException if
   *    configuration item with a given key
   *    was not specified in the config file.
   *    
   *  @throws ConfigItemTypeException if value
   *    cannot be trasformed to integer number.
   */                                 
  public double getFloat(String key) 
    throws ConfigItemNotFoundException, ConfigItemTypeException;

  /**
   *  @param key identification of the configuration
   *    item.    
   *       
   *  @return value associated with given key.
   *    
   *  @throws ConfigItemNotFoundException if
   *    configuration item with a given key
   *    was not specified in the config file.
   */                                 
  public String getString(String key) 
    throws ConfigItemNotFoundException;

  /**
   *  @param key identification of the configuration
   *    item.    
   *       
   *  @return value associated with given key
   *    transformed to boolean.
   *    
   *  @throws ConfigItemNotFoundException if
   *    configuration item with a given key
   *    was not specified in the config file.
   *    
   *  @throws ConfigItemTypeException if value
   *    cannot be trasformed to boolean.
   */                                 
  public boolean getBoolean(String key)
    throws ConfigItemNotFoundException, ConfigItemTypeException;

  /**
   *  @param key identification of the configuration
   *    item.
   *    
   *  @param defaultValue a value that is returned
   *    if and only if config. item with given key
   *    was not specified in the config. file.                
   *       
   *  @return value associated with given key
   *    transformed to integer or defaultValue if
   *    such config. item was not specified.   
   *    
   *  @throws ConfigItemTypeException if value
   *    was specified but cannot be trasformed to 
   *    integer number.
   */                                 
  public int getInteger(String key, int defaultValue) 
    throws ConfigItemTypeException;

  /**
   *  @param key identification of the configuration
   *    item.
   *    
   *  @param defaultValue a value that is returned
   *    if and only if config. item with given key
   *    was not specified in the config. file.                
   *       
   *  @return value associated with given key
   *    transformed to real number or defaultValue if
   *    such config. item was not specified.   
   *    
   *  @throws ConfigItemTypeException if value
   *    was specified but cannot be trasformed to 
   *    real number.
   */                                 
  public double getFloat(String key, double defaultValue) 
    throws ConfigItemTypeException;
  
  /**
   *  @param key identification of the configuration
   *    item.
   *    
   *  @param defaultValue a value that is returned
   *    if and only if config. item with given key
   *    was not specified in the config. file.                
   *       
   *  @return value associated with given key
   *    or defaultValue if such config. item 
   *    was not specified.   
   */                                 
  public String getString(String key, String defaultValue); 

  /**
   *  @param key identification of the configuration
   *    item.
   *    
   *  @param defaultValue a value that is returned
   *    if and only if config. item with given key
   *    was not specified in the config. file.                
   *       
   *  @return value associated with given key
   *    transformed to boolean or defaultValue if
   *    such config. item was not specified.   
   *    
   *  @throws ConfigItemTypeException if value
   *    was specified but cannot be trasformed to 
   *    boolean.
   */                                 
  public boolean getBoolean(String key, boolean defaultValue)
    throws ConfigItemTypeException;

}
