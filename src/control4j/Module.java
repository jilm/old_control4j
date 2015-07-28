package control4j;

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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.lang.reflect.Field;
import java.lang.reflect.AccessibleObject;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.reflect.ObjectMapDecorator;
import cz.lidinsky.tools.reflect.ObjectMapUtils;
import cz.lidinsky.tools.reflect.Setter;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.iterators.FilterIterator;

import control4j.tools.DeclarationReference;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

/**
 *  Module is one of the main building blocks of the application.
 *  A module gets input and provides output.
 *
 *  <p>Number of input signals (terminals) and input terminal
 *  meaning depends entirely on the module implementation. Each
 *  module gets all of its input in one array, during the runtime,
 *  so each input terminal has a zero-based index to distinguish
 *  it. Because the number of input terminals doesn't have to be
 *  known during the module creation, and moreover there may be
 *  modules for which the order of input signals is meaningless,
 *  the input terminals must be organized inside the input array
 *  as follows:
 *  <ol>
 *    <li>Indexes from zero to some <em>n</em> are dedicated to
 *        terminals which are mandatory. Elements inside the
 *        input array may not contain <code>null</code> value.
 *        In other words, these input terminals must be connected.
 *        Concrete module implementation must eiter override
 *        a method <code>getMandatoryInputSize</code> to return
 *        the proper number of mandatory input terminals or the
 *        <code>AMinInput</code> annotation my be used to achive
 *        the correct functionality.
 *    <li>Indexes from <em>n</em> to some <em>m</em> are used
 *        by terminals which are optional. Moreover, the <em>m</em>
 *        may not be known by the time of module creation, it
 *        means, that in such a case, it depends entirely on the
 *        application definition, how many input signals will be
 *        connected. Nevertheless the terminals which span into
 *        this category may left disconnected. In other words,
 *        the corresponding element in the input array may contain
 *        <code>null</code> value. Module implementation must
 *        either override the method <code>getIndexedInputSize</code>
 *        to return the proper number of totally supported indexed
 *        input terminals, or it may use <code>AMaxInput</code>
 *        for the same purpose.
 *    <li>The third category of input is index-less input. If
 *        this kind of input is supported by the module, than the
 *        method <code>isVariableInputSupported</code> must return
 *        <code>true</code>. Same functionality may be achieved
 *        thanks to the <code>AVariableInput</code> annotation.
 *        The only condition that must be satisfied is that number
 *        of input terminals that belongs to the previous two
 *        categoris is bounded. In such a case, index-less signals
 *        will ocupy input array indexes starting from <em>m</em>.
 *  </ol>
 *
 *  @see AVariableInput
 *  @see AMinInput
 *  @see AMaxInput
 */
public abstract class Module implements IToStringBuildable
{

  /**
   *  Reference to the place where the module was declared in
   *  human readable form.
   */
  private DeclarationReference declarationReference;

  /**
   *  Initialize a module. This method assign configuration items
   *  from the configuration parameter to the properties that are
   *  annotated by ConfigItem. If you don't want this functionality
   *  just override this method. Or you can override it, do some
   *  custom initialization and than call this to finish the
   *  initialization.
   *
   *  @param configuration
   *             the configuration of the module.
   *
   *  @see ConfigurationHelper
   */
  protected void initialize(IConfigBuffer configuration) {
    // assign configuration items
    ObjectMapDecorator<String> objectMap
        = new ObjectMapDecorator<String>(String.class);
    objectMap.setSetterFilter(PredicateUtils.allPredicate(
        ObjectMapUtils.getSetterSignatureCheckPredicate(),
        ObjectMapUtils.getHasAnnotationPredicate(Setter.class)));
    objectMap.setGetterFilter(PredicateUtils.falsePredicate());
    objectMap.setSetterFactory(
        ObjectMapUtils.stringSetterClosureFactory(true));
    objectMap.setDecorated(this);
    Set<String> keys = objectMap.keySet();
    for (String key : keys) {
      try {
        objectMap.put(key, configuration.getString(key));
      } catch (ConfigItemNotFoundException e) {
        fine("ConfigItem missing: " + key);
      }
    }
  }

  /**
   *
   *
   */
  protected void assignResources(control4j.application.Module definition) {
    ObjectMapDecorator<Object> objectMap
        = new ObjectMapDecorator<Object>(Object.class);
    objectMap.setSetterFilter(PredicateUtils.allPredicate(
        ObjectMapUtils.getSetterSignatureCheckPredicate(),
        ObjectMapUtils.hasAnnotationPredicate(AResource.class)));
    objectMap.setGetterFilter(PredicateUtils.falsePredicate());
    objectMap.setDecorated(this);
    Set<String> keys = objectMap.keySet();
    ResourceManager resourceManager = ResourceManager.getInstance();
    try {
      if (keys.size() == 1) {
        objectMap.put(
            keys.iterator().next(),
            resourceManager.getResource(definition.getResource()));
      } else {
        for (String key : keys) {
          objectMap.put(
              key, resourceManager.getResource(definition.getResource(key)));
        }
      }
    } catch (Exception e) {
      throw new SyntaxErrorException(e);
    }
  }

  /**
   *  Assign values into the configuration fields, calls configuration
   *  methods and assign resources. This method calls an
   *  {@link #initialize(IConfigBuffer)} method to assign configuration
   *  items. If you need different behaviour, override this method.
   */
  public void initialize(control4j.application.Module definition) {
    // Assign all of the configuration items
    initialize(definition.getConfiguration());
    // Assign all of the resources
    assignResources(definition);
  }

  /**
   *  Checks that it is possible to connect a signal into
   *  the input with given index.
   *
   *  <p>Implicite implementation uses annotations to determine
   *  which index may be used to connect a signal.
   *
   *  @param index
   *
   *  @param inputSize
   *
   *  @throws
   *
   *  @see AMaxInput
   *  @see AMinInput
   *  @see AVariableInput
   */
  protected void checkConnectedInput(int index, int inputSize)
  {
    if (index >= inputSize)
      throw new IllegalArgumentException();  // TODO:
  }

  protected void checkDisconnectedInput(int index, int inputSize)
  {
    if (index < getMandatoryInputSize())
      throw new IllegalArgumentException(); // TODO:
  }

  /**
   *  Returns number of input terminals which belongs to the
   *  category of mandatory (not optional) terminals. These
   *  are terminals that must be connected to guarantee correct
   *  module functionality. This number is entirely the property
   *  of the module implementation.
   *
   *  <p>This implementation returns a value that is specified
   *  by a <code>AMinInput</code> annotation. If not declared
   *  it returns zero.
   *
   *  @return number of mandatory input terminals required by
   *             the module
   *
   *  @see AMinInput
   */
  public int getMandatoryInputSize()
  {
    AMinInput minInputAnno = getClass().getAnnotation(AMinInput.class);
    if (minInputAnno == null)
      return 0;
    else
      return minInputAnno.value(); // TODO: check that the value is positive
  }

  /**
   *  Returns number of indexed input terminals which are supported
   *  by this module.
   *
   *  <p>This implementation returns a value that is specified by
   *  the <code>AMaxInput</code> annotation. If not declared, it
   *  returns the number of mandatory input terminals.
   *
   *  @return total number of indexed input terminals supported
   *             by this module
   *
   *  @see AMaxInput
   */
  public int getIndexedInputSize()
  {
    AMaxInput maxInputAnno = getClass().getAnnotation(AMaxInput.class);
    if (maxInputAnno == null)
      return getMandatoryInputSize();
    else
      return maxInputAnno.value(); // TODO: check that the value is positive
  }

  public int getIndexedOutputSize()
  {
    AOutputSize outputSizeAnno = getClass().getAnnotation(AOutputSize.class);
    if (outputSizeAnno == null)
      return 0;
    else
      return outputSizeAnno.value(); // TODO: check that the value is positive
  }

  /**
   *  Returns true iff this module supports a variable input.
   *  It means input where signal order is meaningless.
   */
  public boolean isVariableInputSupported()
  {
    return getClass().getAnnotation(AVariableInput.class) != null;
  }

  public boolean isVariableOutputSupported()
  {
    return getClass().getAnnotation(AVariableOutput.class) != null;
  }

  /**
   *  Returns index of the first possible variable input.
   */
  public int getVariableInputFirstIndex()
  {
    if (!isVariableInputSupported())
      throw new UnsupportedOperationException();
    return getIndexedInputSize();
  }

  public int getVariableOutputFirstIndex()
  {
    if (!isVariableOutputSupported())
      throw new UnsupportedOperationException();
    return getIndexedOutputSize();
  }

  /**
   *
   */
  public int getInputSize(int lastDeclaredIndex)
  {
    int mandatorySize = getMandatoryInputSize();
    if (lastDeclaredIndex < mandatorySize - 1)
      return mandatorySize;
    else
      return lastDeclaredIndex + 1;
  }

  public int getOutputSize(int lastDeclaredIndex)
  {
    return Math.max(lastDeclaredIndex + 1, getIndexedOutputSize());
  }

  public void prepare() { }

  /**
   *  Returns a reference to the config file
   *  where the module was declared. The reference
   *  is in the human readable form and play a role in
   *  the exception and log messages.
   *
   *  @return the declaration reference, if not specified, returns an
   *       empty string
   */
  public String getDeclarationReference()
  {
    if (declarationReference != null)
      return declarationReference.toString();
    else
      return "";
  }

  /**
   *  Prints information about the module into the given writer.
   *  It serves mainly for debug purposes. This method writes only
   *  the declaration reference, if you thing that it would be useful
   *  to have more info about a particular module in the case of
   *  failure to find out what is going on, simply override this
   *  method.
   *
   *  @param writer
   *             a writer on which the dump will be printed
   */
  public void dump(java.io.PrintWriter writer)
  {
    writer.println("== MODULE ==");
    writer.println("Class: " + getClass().getName());
    writer.println(declarationReference.toString());
  }

  public void putResource(final String key, Resource resource)
  {
    List<Field> resourceFields
        = FieldUtils.getFieldsListWithAnnotation(
        getClass(), AResource.class);
    Iterator<Field> resourceFieldsIter
        = new FilterIterator(resourceFields.iterator(),
        new Predicate<Field>()
        {
          public boolean evaluate(Field field)
          {
            return field.getAnnotation(AResource.class).key().equals(key);
          }
        }
    );
    boolean assigned = false;
    while (resourceFieldsIter.hasNext())
    {
      try
      {
        Field resourceField = resourceFieldsIter.next();
        FieldUtils.writeField(resourceField, this, resource, true);
        assigned = true;
      }
      catch (IllegalAccessException e)
      {
        // TODO:
        catched(getClass().getName(), "putResource", e);
      }
    }
    // TODO: Non existent key field
    if (!assigned)
      warning("The resource was not assigned, key: " + key);
  }

  @Override
  public String toString() {
    return new ToStringBuilder()
      .append(this)
      .toString();
  }

  public void toString(ToStringBuilder sb) {
    sb.append("declarationReference", declarationReference);
  }

}
