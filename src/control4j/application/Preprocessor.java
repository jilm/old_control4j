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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.collections4.CollectionUtils.emptyCollection;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.List;
import java.text.MessageFormat;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.collections4.ListUtils;

import cz.lidinsky.tools.ToStringMultilineStyle;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.graph.Graph;
import cz.lidinsky.tools.graph.IGraph;

import static control4j.tools.Logger.*;

import control4j.SyntaxErrorException;
import control4j.tools.DuplicateElementException;

/**
 *
 *  Preprocessing of the application.
 *
 */
public class Preprocessor {

  //---------------------------------------------------------- Public Interface

  /**
   *  Empty constructor
   */
  public Preprocessor() { }

  /** Holds actual scope during the process of translation. */
  private Scope scopePointer = Scope.getGlobal();

  public Scope getScopePointer() {
    return scopePointer;
  }

  public void startScope() {
    scopePointer = new Scope(scopePointer);
  }

  public void endScope() {
    scopePointer = scopePointer.getParent();
  }

  /**
   *  Adds a definition into the internal buffer.
   */
  public void putDefinition(String name, Scope scope, String value) {
    if (definitions == null) {
      definitions = new ScopeMap<ValueObject>();
    }
    try {
      definitions.put(name, scope, new ValueObject(value));
    } catch (DuplicateElementException e) {
      ErrorManager.newError()
        .set(ErrorRecord.DUPLICATE_DEFINITION_ERROR)
        .set(ErrorRecord.NAME_CODE, name)
        .set(ErrorRecord.SCOPE_CODE, scope.toString());
    }
  }

  public void putResource(String name, Scope scope, Resource resource) {
    // TODO:
    if (resources == null) {
      resources = new ScopeMap<Resource>();
    }
    try {
      resources.put(name, scope, resource);
    } catch (DuplicateElementException e) {
      ErrorManager.newError()
        .set(ErrorRecord.DUPLICATE_DEFINITION_ERROR)
        .set(ErrorRecord.NAME_CODE, name)
        .set(ErrorRecord.SCOPE_CODE, scope.toString())
        .set(ErrorRecord.REFERENCE1_CODE,
            resources.get(name, scope).getDeclarationReferenceText())
        .set(ErrorRecord.REFERENCE2_CODE,
            resource.getDeclarationReferenceText());
    }
  }

  public void putBlock(String name, Scope scope, Block block) {
    if (blocks == null) {
      blocks = new ScopeMap<Block>();
    }
    try {
      blocks.put(name, scope, block);
    } catch (DuplicateElementException e) {
      // TODO:
    }
  }

  /**
   *  Puts given signal into the internal data structure.
   *  A unique order number is assigned to the signal (index).
   *
   *  @param name
   *             an identifier of the signal which serves
   *             for referencing it
   *
   *  @param scope
   *             a scope under which the signal was defined
   *
   *  @param signal
   *             a signal object to store
   *
   *  @throws IllegalArgumentException
   *             if the name is empty string or a blank string
   *
   *  @throws NullPointerException
   *             if either of the parameters is <code>null</code>
   *             value
   *
   *  @throws DuplicateElementException
   *             the pair name and scope must be unique across
   *             the whole application. If it isn't, this exception
   *             is thrown
   */
  public void putSignal(String name, Scope scope, Signal signal) {
    String trimmedName = notBlank(name).trim();
    notNull(scope);
    notNull(signal);
    if (signals == null) {
      signals = new ScopeMap<Signal>();
      signalIndexes = new ArrayList<Triple<String, Scope, Signal>>();
    }
    try {
    signals.put(name, scope, signal);
    signalIndexes.add(
        new ImmutableTriple<String, Scope, Signal>(name, scope, signal));
    } catch (DuplicateElementException e) {
      // TODO:
    }
  }

  /**
   *  Adds a module definition into the internal buffer.
   */
  public void addModule(Module module) {
    if (modules == null) modules = new ArrayList<Module>();
    modules.add(module);
  }

  public void addResourceRef(
      String href, Scope scope, Resource resource) {
    resourceReferences.add(
      new ReferenceDecorator<Resource, Object>(href, scope, null, resource));
  }

  public void addModuleInput(String href, Scope scope, Input input) {
    moduleInputs.add(
        new ReferenceDecorator<Input, Object>(href, scope, null, input));
  }

  public void addModuleOutput(String href, Scope scope, Output output) {
    moduleOutputs.add(
        new ReferenceDecorator<Output, Object>(href, scope, null, output));
  }

  /**
   *  Adds a given use object into the internal buffer.
   *
   *  @param use
   *             an object to add
   *
   *  @param scope
   *             a scope under which the use object was defined
   */
  public void add(Use use, Scope scope) {
    notNull(use);
    notNull(scope);
    uses.add(new ImmutablePair<Use, Scope>(use, scope));
  }

  public void addPropertyReference(
      String href, Scope scope, Property property) {
    propertyRefs.add(
        new ReferenceDecorator<Property, Object>(href, scope, null, property));
  }

  public void addInputTag(Module module, String tag) {
    // TODO:
    inputTags.add(new ImmutablePair<Module, String>(module, tag));
  }

  public void addOutputTag(Module module, String tag) {
    // TODO:
    outputTags.add(new ImmutablePair<Module, String>(module, tag));
  }

  public Property putProperty(String key, String value)
      throws DuplicateElementException {
    if (configuration.containsKey(key)) {
      throw new DuplicateElementException();
    } else {
      Property property = new Property();
      property.setValue(value);
      configuration.put(key, property);
      return property;
    }
  }

  public Property putProperty(String key, Property property)
      throws DuplicateElementException {
    if (configuration.containsKey(key)) {
      throw new DuplicateElementException();
    } else {
      configuration.put(key, property);
      return property;
    }
  }

  //--------------------------------------------------------------- Collections

  /** Global Configuration. */
  private HashMap<String, Property> configuration
    = new HashMap<String, Property>();

  /** Define objects. */
  private ScopeMap<ValueObject> definitions;

  /** Resource definitions. */
  private ScopeMap<Resource> resources;

  /** Block definitions */
  private ScopeMap<Block> blocks;

  /** A data structure for name and scope signal look up. */
  private ScopeMap<Signal> signals;

  /** A data structure for indexed signal look up. */
  private ArrayList<Triple<String, Scope, Signal>> signalIndexes;

  /** An array of module definitions. */
  private ArrayList<Module> modules;

  private ArrayList<ReferenceDecorator<Resource, Object>> resourceReferences
      = new ArrayList<ReferenceDecorator<Resource, Object>>();

  private ArrayList<ReferenceDecorator<Input, Object>> moduleInputs
      = new ArrayList<ReferenceDecorator<Input, Object>>();

  private ArrayList<ReferenceDecorator<Output, Object>> moduleOutputs
      = new ArrayList<ReferenceDecorator<Output, Object>>();

  private ArrayList<Pair<Use, Scope>> uses
      = new ArrayList<Pair<Use, Scope>>();

  protected ArrayList<ReferenceDecorator<Property, Object>> propertyRefs
      = new ArrayList<ReferenceDecorator<Property, Object>>();

  protected ArrayList<Pair<Module, String>> inputTags
      = new ArrayList<Pair<Module, String>>();

  protected ArrayList<Pair<Module, String>> outputTags
      = new ArrayList<Pair<Module, String>>();

  //---------------------------------------------------- Private Access Methods

  /**
   *  Returns a value of definition with given name and scope.
   */
  protected String getDefinition(String name, Scope scope) {
    if (definitions == null) {
      throw new NoSuchElementException();
    }
    return definitions.get(name, scope).getValue();
  }

  protected Resource getResource(String name, Scope scope) {
    if (resources == null) {
      throw new NoSuchElementException();
    }
    return resources.get(name, scope);
  }

  /**
   *  Returns a block definition with given name which is under
   *  the given scope.
   *
   *  @throws NoSuchElementException
   *             if such a blok is not present in the internal
   *             buffer
   */
  protected Block getBlock(String name, Scope scope) {
    if (blocks == null)
      throw new NoSuchElementException();
    return blocks.get(name, scope);
  }

  /** ??? */
  protected Collection<Block> getBlocks() {
    if (blocks == null) {
      return emptyCollection();
    } else {
      return blocks.values();
    }
  }

  /**
   *  Returns the index of the given signal.
   *
   *  @param signal
   *
   *  @return an order number of the given signal
   *
   *  @throws NullPointerException
   *             if the parameter is <code>null</code> value
   *
   *  @throws NoSuchElementException
   *             if the given signal is not present in the internal
   *             buffer
   */
  protected int getSignalIndex(Signal signal)
  {
    notNull(signal);
    if (signalIndexes == null) // TODO:
      throw new NoSuchElementException();
    for (int i = 0; i < signalIndexes.size(); i++)
    {
      if (signalIndexes.get(i).getRight() == signal)
        return i;
    }
    throw new NoSuchElementException(); // TODO:
  }

  /**
   *  Returns the signal with the given name that was defined
   *  under the given scope or some of the parent scope.
   *
   *  @throws NullPointerException
   *
   *  @throws NoSuchElementException
   */
  protected Signal getSignal(String name, Scope scope)
  {
    notNull(name);
    notNull(scope);
    if (signals == null)  // TODO:
      throw new NoSuchElementException();
    return signals.get(name, scope);
  }

  /**
   *  Returns the signal with the given order number (index).
   *
   *  @throws IndexOutOfBoundsException
   *             if the index if out of range
   */
  protected Triple<String, Scope, Signal> getSignal(int index)
  {
    if (signalIndexes == null)  // TODO:
      throw new IndexOutOfBoundsException();
    return signalIndexes.get(index);
  }

  /**
   *  Returns number of signal objects inside the internal buffer.
   */
  public int getSignalsSize()
  {
    if (signalIndexes == null) return 0;
    return signalIndexes.size();
  }

  public Module getModule(int index)
  {
    if (modules == null) {} // TODO
    return modules.get(index);
  }

  public int getModulesSize()
  {
    if (modules == null) return 0;
    return modules.size();
  }

  public void removeModules()
  {
    modules.clear();
  }

  public List<ReferenceDecorator<Resource, Object>> getResourceReferences() {
    return ListUtils.unmodifiableList(resourceReferences);
  }

  public int getUseObjectsSize()
  {
    return uses.size();
  }

  public Pair<Use, Scope> getUse(int index)
  {
    return uses.get(index);
  }

  public void removeUse(int index)
  {
    uses.remove(index);
  }

  public int indexOf(Use use, Scope scope)
  {
    for (int i=0; i<uses.size(); i++)
    {
      Pair<Use, Scope> u = uses.get(i);
      if (u.getKey().equals(use) && u.getValue().equals(scope))
        return i;
    }
    throw new NoSuchElementException();
  }

  public int indexOf(Use use, int startIndex)
  {
    for (int i=startIndex; i<uses.size(); i++)
      if (uses.get(i).getKey().equals(use))
        return i;
    throw new NoSuchElementException();
  }

  //---------------------------------------------------------------- Processing

  /**
   *  Entry point of the preprocessor. Given application
   *  serves as the input and output of this method.
   */
  public void process() {

    // Block expansion
    while (uses.size() > 0) {
      try {
        // get use to substitute
        Pair<Use, Scope> use = uses.get(0);
        // get referenced block
        Block block
          = getBlock(use.getLeft().getHref(), use.getLeft().getScope());
        // set appropriate scope
        scopePointer = new Scope(use.getRight());
        // set appropriate field for cycle detection
        // TODO:
        // expand block
        block.expand(use.getLeft(), this);
        // remove substuted use
        uses.remove(0);
      } catch (NoSuchElementException e) {
        // block definition for given use is missing
      }
    }

    // resource substitution
    // for all of the modules, if there is a resource reference
    // find it and substitude in place of the reference
    for (ReferenceDecorator<Resource, Object> reference : resourceReferences) {
      try {
        Resource resource
            = getResource(reference.getHref(), reference.getScope());
        reference.getDecorated().putConfiguration(resource);
      } catch (NoSuchElementException e) {
        // TODO:
      }
    }

    // property substitution
    for (ReferenceDecorator<Property, Object> reference : propertyRefs) {
      try {
        String value = getDefinition(reference.getHref(), reference.getScope());
        reference.getDecorated().setValue(value);
      } catch (NoSuchElementException e) {
        // TODO:
      }
    }

    // tagged signals
    for (int j = 0; j < getSignalsSize(); j++) {
      Signal signal = getSignal(j).getRight();
      Set<String> tagNames = signal.getTagNames();
      if (!tagNames.isEmpty()) {
        String name = getSignal(j).getLeft();
        Scope scope = getSignal(j).getMiddle();
        for (Pair<Module, String> tag : inputTags) {
          if (tagNames.contains(tag.getRight())) {
            Input input = new Input();
            addModuleInput(name, scope, input);
            tag.getLeft().putInput(input);
          }
        }
        for (Pair<Module, String> tag : outputTags) {
          if (tagNames.contains(tag.getRight())) {
            Output output = new Output();
            addModuleOutput(name, scope, output);
            tag.getLeft().putOutput(output);
          }
        }
      }
    }

    // resolve module inputs and outputs
    for (ReferenceDecorator<Input, Object> inputRef : moduleInputs) {
      try {
        Signal signal = getSignal(inputRef.getHref(), inputRef.getScope());
        int pointer = getSignalIndex(signal);
        inputRef.getDecorated().setPointer(pointer);
        // TODO: copy signal properties
      } catch (NoSuchElementException e) {
        // TODO: Signal was not defined!
      }
    }

    // resolve module outputs
    for (ReferenceDecorator<Output, Object> outputRef : moduleOutputs) {
      try {
        Signal signal = getSignal(outputRef.getHref(), outputRef.getScope());
        int pointer = getSignalIndex(signal);
        outputRef.getDecorated().setPointer(pointer);
        // TODO: copy signal properties
      } catch (NoSuchElementException e) {
        // TODO: Signal was not defined!
      }
    }

    // -----------------------

  }

  /**
   *  Only for debug purposes.
   */
  public static void main(String[] args) throws Exception {
    java.io.File file = new java.io.File(args[0]);
    Loader loader = new Loader();
    Application application = loader.load(file);
    Preprocessor preprocessor = new Preprocessor();
    preprocessor.process();
    ToStringBuilder sb = new ToStringMultilineStyle();
    System.out.println(sb.toString());
  }

}
