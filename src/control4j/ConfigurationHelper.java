package control4j;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import control4j.tools.DeclarationReference;

/**
 *  Help to configure modules or resources. The only thing the developer
 *  of some module or resource has to do is to annotate field with 
 *  ConfigItem annotation. And correct value will be assigned to it.
 *
 *  @see ConfigItem
 */
public class ConfigurationHelper
{

  /**
   *  Sets configuration of module or resource according to the given
   *  parameter. Each field of the configured object must be annotated
   *  with ConfigItem annotation.
   *
   *  @param object
   *             object which will be configured
   *
   *  @param configuration
   *             object that contains configuration for the object
   *
   *  @param declarationReference
   *             may be null. Will be used if there is some problem.
   *             It should contain a reference where the object comes
   *             from in human readable form.
   *
   *  @throws SystemException
   *
   *  @throws SyntaxErrorException
   *
   *  @see ConfigItem
   */
  public static void assignConfiguration(Object object, IConfigBuffer configuration, DeclarationReference declarationReference)
  {
    assignFields(object, configuration, declarationReference);
    callSetMethods(object, configuration, declarationReference);
  }

  public static void assignConfiguration(Object object, String key, String value, DeclarationReference declarationReference)
  {
    assignField(object, key, value, declarationReference);
    callSetMethod(object, key, value, declarationReference);
  }

  /**
   *
   */
  private static void assignField(Object object, String key, String value, DeclarationReference declarationReference)
  {
    // get all the public fields
    Field[] fields = object.getClass().getFields();
    // goes through them
    for (Field field : fields)
    {
      // find only those with the appropriate annotation
      ConfigItem annotation = field.getAnnotation(ConfigItem.class);
      if (annotation != null)
      {
        try
        {
          if (field.getType() == String.class)
            field.set(object, value);
          else if (field.getType() == int.class)
            field.setInt(object, Integer.parseInt(value));
          else if (field.getType() == double.class)
            field.setDouble(object, Double.parseDouble(value));
          else if (field.getType() == boolean.class)
            field.setBoolean(object, Boolean.parseBoolean(value));
          else
            throw new SystemException();
        }
	catch (NumberFormatException e)
	{
	  throw new SyntaxErrorException();
	}
	catch (IllegalAccessException e)
	{
	}
      }
    }
  }

  /**
   *
   */
  private static void assignFields(Object object, IConfigBuffer configuration, DeclarationReference declarationReference)
  {
    // get all the public fields
    Field[] fields = object.getClass().getFields();
    // goes through them
    for (Field field : fields)
    {
      // find only thouse with appropriate annotation
      ConfigItem annotation = field.getAnnotation(ConfigItem.class);
      if (annotation != null)
      {
        String key = annotation.key();
        if (key.length() == 0) key = field.getName();
        try
        {
          if (field.getType() == String.class)
            field.set(object, configuration.getString(key));
          else if (field.getType() == int.class)
            field.setInt(object, configuration.getInteger(key));
          else if (field.getType() == double.class)
            field.setDouble(object, configuration.getFloat(key));
          else if (field.getType() == boolean.class)
            field.setBoolean(object, configuration.getBoolean(key));
          else
            throw new SystemException();
        }
        catch (ConfigItemTypeException e)
        {
	  throw new SyntaxErrorException();
        }
        catch (ConfigItemNotFoundException e)
        {
          if (!annotation.optional())
	    throw new SyntaxErrorException();
        }
	catch (IllegalAccessException e)
	{
	}
      }
    }
  }

  /**
   *  Gets all of the public method of given object and calls all of them
   *  which are annotated.
   *
   *  @see ConfigItem
   */
  private static void callSetMethods(Object object, IConfigBuffer configuration, DeclarationReference declarationReference)
  {
    // get all the public methods of the object
    Method[] methods = object.getClass().getMethods();
    // goes through them
    for (Method method : methods)
    {
      // find only methods with configitem annotation
      ConfigItem annotation = method.getAnnotation(ConfigItem.class);
      if (annotation != null)
      {
        // get the key, if it is not present inside the annotation, take
	// the method name
        String key = annotation.key();
        if (key.length() == 0) key = method.getName();
	// get type of an argument
	Class<?>[] parameterTypes = method.getParameterTypes();
	if (parameterTypes.length != 1)
	  throw new SystemException();
        // call the method with appropriate parameter
	try
	{
	  if (parameterTypes[0] == String.class)
	    method.invoke(object, configuration.getString(key));
	  else if (parameterTypes[0] == int.class)
	    method.invoke(object, configuration.getInteger(key));
	  else if (parameterTypes[0] == double.class)
	    method.invoke(object, configuration.getFloat(key));
          else if (parameterTypes[0] == boolean.class)
	    method.invoke(object, configuration.getBoolean(key));
	  else
	    throw new SystemException();
        }
	catch(IllegalAccessException e)
	{
	  throw new SystemException();
	}
	catch(InvocationTargetException e)
	{
	  throw new SyntaxErrorException(e);
	}
	catch(ConfigItemNotFoundException e)
	{
          if (!annotation.optional())
	    throw new SyntaxErrorException();
	}
	catch(ConfigItemTypeException e)
	{
	  throw new SyntaxErrorException("Type cast exception");
	}
      }
    }
  }

  private static void callSetMethod(Object object, String key, String value, DeclarationReference declarationReference)
  {
    // get all the public methods of the object
    Method[] methods = object.getClass().getMethods();
    // goes through them
    for (Method method : methods)
    {
      // find only methods with configitem annotation
      ConfigItem annotation = method.getAnnotation(ConfigItem.class);
      if (annotation != null)
      {
	// get type of an argument
	Class<?>[] parameterTypes = method.getParameterTypes();
	if (parameterTypes.length != 1)
	  throw new SystemException();
        // call the method with appropriate parameter
	try
	{
	  if (parameterTypes[0] == String.class)
	    method.invoke(object, value);
	  else if (parameterTypes[0] == int.class)
	    method.invoke(object, Integer.parseInt(value));
	  else if (parameterTypes[0] == double.class)
	    method.invoke(object, Double.parseDouble(value));
          else if (parameterTypes[0] == boolean.class)
	    method.invoke(object, Boolean.parseBoolean(value));
	  else
	    throw new SystemException();
        }
	catch(IllegalAccessException e)
	{
	  throw new SystemException();
	}
	catch(InvocationTargetException e)
	{
	  throw new SyntaxErrorException(e);
	}
	catch(NumberFormatException e)
	{
	  throw new SyntaxErrorException();
	}
      }
    }
  }
 
  /**
   *  Prepares and returns an error message for case where object requires
   *  some config item but this item is not present in the configuration
   *  buffer.
   *
   *  @param object
   *             object to configure
   *
   *  @param key
   *             item key
   *
   *  @param declarationReference
   *             declaration reference to the object which was configured
   *             this will be added into the message. May be null.
   */
  private static String getConfigItemNotFoundMessage(Object object, String key, DeclarationReference declarationReference)
  {
    String message = getMessage("mod01");
    message = String.format(message, object.getClass().getName(), key);
    if (declarationReference != null)
    {
      message = message.concat("\n");
      message = message.concat(declarationReference.toString());
    }
    return message;
  }
  
  /**
   *  Returns an error message in case where some config item
   *  doesn't match the type of appropriate field of the object.
   *
   *  @param key
   *             a config key
   *
   */
  private static String getConfigItemTypeMessage(String key, String value, Class<?> expectedType, DeclarationReference declarationReference)
  {
    String message = "";
    if (expectedType == boolean.class)
      message = getMessage("mod04");
    else if (expectedType == double.class)
      message = getMessage("mod02");
    else if (expectedType == int.class)
      message = getMessage("mod03");
    else
      assert false : "Unsupported type of config item!";
    message = String.format(message, key, value);
    if (declarationReference != null)
    {
      message = message.concat("\n");
      message = message.concat(declarationReference.toString());
    }
    return message;
  }

}
