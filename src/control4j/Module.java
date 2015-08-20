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

import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;

import control4j.tools.DeclarationReference;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.reflect.ObjectMapDecorator;
import cz.lidinsky.tools.reflect.ObjectMapUtils;
import cz.lidinsky.tools.reflect.Setter;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.iterators.FilterIterator;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.Set;

/**
 *  Module is one of the main building blocks of the application.
 *  Modules reads data from technology and provides them for the further
 *  processing, performs processing, and writes results back to the
 *  technology.
 *
 *  <p>The modules can be divided, according to inputs and outputs, into
 *  three categories. Modules that provides only output for further processing,
 *  modules that take input provide same processing and returns output and
 *  the modules that only takes input and does not provide any output.
 *
 *  <p>To implement some functionality, do not override diretly this class.
 *  Override one of the {@link InputModule}, {@link ProcessModule} or
 *  {@link OutputModule} instead.
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
 *
 *    <li>Indexes from zero to some <em>n</em> are dedicated to
 *  terminals which are mandatory. Elements inside the
 *  input array may not contain <code>null</code> value.
 *  In other words, these input terminals must be connected.
 *  Concrete module implementation must eiter override
 *  a method <code>getMandatoryInputSize</code> to return
 *  the proper number of mandatory input terminals or the
 *  <code>AMinInput</code> annotation my be used to achive
 *  the correct functionality.
 *
 *    <li>Indexes from <em>n</em> to some <em>m</em> are used
 *  by terminals which are optional. Moreover, the <em>m</em>
 *  may not be known by the time of module creation, it
 *  means, that in such a case, it depends entirely on the
 *  application definition, how many input signals will be
 *  connected. Nevertheless the terminals which span into
 *  this category may left disconnected. In other words,
 *  the corresponding element in the input array may contain
 *  <code>null</code> value. Module implementation must
 *  either override the method <code>getIndexedInputSize</code>
 *  to return the proper number of totally supported indexed
 *  input terminals, or it may use <code>AMaxInput</code>
 *  for the same purpose.
 *
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
public abstract class Module implements IToStringBuildable {

  //------------------------------------------------------------ Configuration.

  /**
   *  Should be used to configure the module after the instance has
   *  been created. It is called by the
   *  {@link #initialize(control4j.application.Module)} method.
   *
   *  <p>The default implementation takes module configuration from
   *  the parameter and use annotated methods and fields of the
   *  module implementation to assign appropriate configuration values.
   *  If different behaviour is needed, override this method.
   *
   *  @param configuration
   *             configuration of the module.
   *
   *  @see Setter
   *  @see ObjectMapDecorator
   */
  protected void initialize(IConfigBuffer configuration) {
    // assign configuration items
    ObjectMapDecorator<String> objectMap
        = new ObjectMapDecorator<String>(String.class);
    objectMap.setSetterFilter(PredicateUtils.allPredicate(
        //ObjectMapUtils.getSetterSignatureCheckPredicate(),
        ObjectMapUtils.getHasAnnotationPredicate(Setter.class)));
    objectMap.setGetterFilter(PredicateUtils.falsePredicate());
    objectMap.setSetterFactory(
        ObjectMapUtils.stringSetterClosureFactory(true));
    objectMap.setDecorated(this);
    Set<String> keys = objectMap.keySet();
    // TODO:
    for (String key : keys) {
      try {
        objectMap.put(key, configuration.getString(key));
      } catch (ConfigItemNotFoundException e) {
        fine("ConfigItem missing: " + key);
      }
    }
  }

  /**
   *  Should be used to assign resources to the module after the instance
   *  has been created. It is called by the
   *  {@link #initialize(control4j.application.Module)} method.
   *
   *  <p>The default implementation uses {@link AResource} annotation
   *  to recognize the module field or method where to assign required
   *  resource. If different behaviour is necessary, override this method.
   *
   *  @param definition
   *             declaration informations of the module
   *
   *  @see AResource
   *  @see ObjectMapDecorator
   */
  protected void assignResources(control4j.application.Module definition) {
    // initialize the object map decorator
    ObjectMapDecorator<Object> objectMap
        = new ObjectMapDecorator<Object>(Object.class);
    objectMap.setSetterFilter(PredicateUtils.allPredicate(
        ObjectMapUtils.getSetterSignatureCheckPredicate(),
        ObjectMapUtils.hasAnnotationPredicate(AResource.class)));
    objectMap.setGetterFilter(PredicateUtils.falsePredicate());
    objectMap.setDecorated(this);
    Set<String> keys = objectMap.keySet();
    // get resource manager
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
      // TODO:
      throw new SyntaxErrorException(e);
    }
  }

  /**
   *  It should be used to configure module instance after it has been
   *  created. This method is called by the {@link Instantiator}.
   *
   *  <p>The default implementation calls methods
   *  {@link #initialize(IConfigBuffer)} to assign configuration and
   *  {@link #assignResources} to set appropriate resources. Moreover
   *  the declaration reference is taken from the definition. If
   *  different behaviour is needed, try to override these two
   *  methods first.
   *
   *  @param definition
   *             declaration informations of the module
   */
  public void initialize(control4j.application.Module definition) {
    // Take the declaration reference
    declarationReference = definition.getDeclarationReferenceText();
    // Assign all of the configuration items
    initialize(definition.getConfiguration());
    // Assign all of the resources
    assignResources(definition);
  }

  //---------------------------------------------------------- Input Treatment.

  /**
   *  Returns number of input terminals which belongs to the
   *  category of mandatory (not optional) terminals. These
   *  are terminals that must be connected to guarantee correct
   *  module functionality. This number is entirely the property
   *  of the module implementation.
   *
   *  <p>Default implementation returns a value that is specified
   *  by a <code>AMinInput</code> annotation. If not declared
   *  it returns zero.
   *
   *  @return number of mandatory input terminals required by
   *             the module
   *
   *  @see AMinInput
   */
  public int getMandatoryInputSize() {
    AMinInput minInputAnno = getClass().getAnnotation(AMinInput.class);
    if (minInputAnno == null) {
      return 0;
    } else {
      return minInputAnno.value(); // TODO: check that the value is positive
    }
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
  public int getIndexedInputSize() {
    AMaxInput maxInputAnno = getClass().getAnnotation(AMaxInput.class);
    if (maxInputAnno == null) {
      return getMandatoryInputSize();
    } else {
      return maxInputAnno.value(); // TODO: check that the value is positive
    }
  }

  /**
   *  Returns true iff this module supports a variable input.
   *  It means input where signal order is meaningless.
   *
   *  <p>The default implementation returns true iff the
   *  <code>AVariableInput</code> annotation is presented, otherwise
   *  it returns false.
   */
  public boolean isVariableInputSupported() {
    return getClass().getAnnotation(AVariableInput.class) != null;
  }

  /**
   *  Returns index of the first possible variable input.
   *
   *  @throws CommonException
   *             with code <code>UNSUPPORTED_OPERATION</code> if the module
   *             implementation doesn't support variable input
   */
  public int getVariableInputFirstIndex() {
    if (!isVariableInputSupported()) {
      throw new CommonException()
        .setCode(ExceptionCode.UNSUPPORTED_OPERATION)
        .set("message", "This module doesn't support variable input!")
        .set("module class", getClass().getName())
        .set("reference", getDeclarationReference());
    }
    return getIndexedInputSize();
  }

  /**
   *
   */
  public int getInputSize(int lastDeclaredIndex) {
    int mandatorySize = getMandatoryInputSize();
    if (lastDeclaredIndex < mandatorySize - 1)
      return mandatorySize;
    else
      return lastDeclaredIndex + 1;
  }

  //--------------------------------------------------------- Output Treatment.

  public int getIndexedOutputSize() {
    AOutputSize outputSizeAnno = getClass().getAnnotation(AOutputSize.class);
    if (outputSizeAnno == null)
      return 0;
    else
      return outputSizeAnno.value(); // TODO: check that the value is positive
  }

  public boolean isVariableOutputSupported() {
    return getClass().getAnnotation(AVariableOutput.class) != null;
  }

  /**
   *  Returns index of the first possible variable input.
   *
   *  @throws CommonException
   *             with code <code>UNSUPPORTED_OPERATION</code> if the module
   *             implementation doesn't support variable input
   */
  public int getVariableOutputFirstIndex() {
    if (!isVariableOutputSupported()) {
      throw new CommonException()
        .setCode(ExceptionCode.UNSUPPORTED_OPERATION)
        .set("message", "This module doesn't support variable output!")
        .set("module class", getClass().getName())
        .set("reference", getDeclarationReference());
    }
    return getIndexedOutputSize();
  }

  public int getOutputSize(int lastDeclaredIndex) {
    return Math.max(lastDeclaredIndex + 1, getIndexedOutputSize());
  }

  //----------------------------------------------------------- Initialization.

  /**
   *  This method is called by the {@link ControlLoop} instantly before
   *  the control loop is run. It is determined to the final initialization.
   *  The whole application is built and prepared for the execution.
   *
   *  <p>The default implementation do nothing.
   */
  public void prepare() { }

  //-------------------------------------------------------------------- Other.

  /**
   *  Reference to the place where the module was declared in the
   *  human readable form. It is mainly used in the error and warning
   *  messages to inform a user where to look for the source of a
   *  potential problem. This field is filled during the application
   *  document loading or processing.
   */
  private String declarationReference;

  /**
   *  Returns a reference to the place
   *  where the module was declared. The reference
   *  is in the human readable form and play a role in
   *  the error and log messages.
   *
   *  @return the declaration reference, if not specified, returns an
   *       empty string
   */
  public String getDeclarationReference() {
    if (declarationReference != null) {
      return declarationReference;
    } else {
      return "";
    }
  }

  /**
   *  Prints information about the module into the given writer.
   *  This method is called if some exception is catched while
   *  the control loop is runing, to write all of the available
   *  information that could be useful for debugging.
   *
   *  <p>The default implementation writes the string that is
   *  returned by the {@link #toString} method.
   *
   *  @param writer
   *             a writer on which the dump will be printed
   */
  public void dump(java.io.PrintWriter writer) {
    try {
      writer.println("== MODULE ==");
      writer.println(toString());
    } catch (Exception e) {
      // this is OK, the debug information will be just missing.
    }
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
