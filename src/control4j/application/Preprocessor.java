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
import java.util.Iterator;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.List;
import java.text.MessageFormat;
import java.util.Deque;
import java.util.ArrayDeque;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.collections4.ListUtils;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringMultilineStyle;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.graph.Graph;
import cz.lidinsky.tools.graph.IGraph;

import static control4j.tools.Logger.*;

import control4j.SyntaxErrorException;
import control4j.ExceptionCode;
import control4j.tools.DuplicateElementException;

/**
 *
 *  Application preprocessing.
 *
 */
public class Preprocessor implements Iterable<Module>, IToStringBuildable {

  /**
   *  Empty constructor
   */
  public Preprocessor() { }

  //---------------------------------------------------------- Public Interface

  public Iterator<Module> iterator() {
    process();
    return modules.iterator();
  }

  public Map<String, Property> getConfiguration() {
    return configuration;
  }

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
   *  Adds a definition into the internal buffer. A definition is a named
   *  value which may be referenced by the property object. This method
   *  doesn't throw exception, rather it create a record in the error
   *  manager.
   *
   *  @param name
   *             identification of the definition. May not be a null not
   *             empty value. Must be a unique idetifier under the given
   *             scope.
   *
   *  @param scope
   *             scope of the definition. May not be a null value.
   *
   *  @param value
   *             value of the definition. May not be a null value.
   *
   */
  public void putDefinition(String name, Scope scope, String value) {
    try {
      // lazy buffer
      if (definitions == null) {
        definitions = new ScopeMap<ValueObject>();
      }
      // insert a definition into the buffer
      definitions.put(name, scope, new ValueObject(value));
    } catch (SyntaxErrorException e) {
      ErrorManager.newError()
        .setCause(e)
        .setCode(ErrorCode.DEFINITION);
    }
  }

  /**
   *  Adds a resource definition into the internal buffer. The resource
   *  may be referenced from inside a module. Each resource definition
   *  is identified by a name and scope.
   *
   *  @param name
   *             must be a unique idetifer under the given scope. May
   *             not be blank.
   *
   *  @param scope
   *             a scope, may not be a null value
   *
   *  @param resource
   *             a resource definition, may not be null value
   */
  public void putResource(String name, Scope scope, Resource resource) {
    // lazy buffer
    if (resources == null) {
      resources = new ScopeMap<Resource>();
    }
    // put resource
    try {
      resources.put(name, scope, resource);
    } catch (SyntaxErrorException e) {
      ErrorManager.newError()
        .setCause(e)
        .setCode(ErrorCode.RESOURCE_DEFINITION);
    }
  }

  /**
   *  Adds a block definition into the internal buffer. The block may be
   *  referenced by the use object. Each block is identified by a name
   *  and scope.
   */
  public void putBlock(String name, Scope scope, Block block) {
    // put definition into the buffer
    try {
      blocks.put(name, scope, block);
      blockGraph.addVertex(block);
    } catch (SyntaxErrorException e) {
      ErrorManager.newError()
        .setCause(e)
        .setCode(ErrorCode.BLOCK_DEFINITION);
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
    // lazy buffer
    if (signals == null) {
      signals = new ScopeMap<Signal>();
      signalIndexes = new ArrayList<Triple<String, Scope, Signal>>();
    }
    try {
      signals.put(name, scope, signal);
      signalIndexes.add(
          new ImmutableTriple<String, Scope, Signal>(name, scope, signal));
    } catch (SyntaxErrorException e) {
      ErrorManager.newError()
        .setCause(e)
        .setCode(ErrorCode.SIGNAL_DEFINITION);
    }
  }

  /**
   *  Adds a module definition. ???
   */
  public void addModule(Module module) {
    modules.push(module);
  }

  /**
   *  Adds a reference to some resource definition.
   */
  public void addResourceRef(
      String href, Scope scope, Resource resource) {
    resourceRefs.push(
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
    // cycle detection
    try {
      if (blockExpand != null) {
        Block block = blocks.get(use.getHref(), use.getScope());
        blockGraph.addEdge(blockExpand, block);
      }
    } catch (SyntaxErrorException e) {
      // block missing, but this will be detected later.
    }
  }

  public void addPropertyReference(
      String href, Scope scope, Property property) {
    propertyRefs.push(
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
  private ScopeMap<Block> blocks = new ScopeMap<Block>();

  /** A graph that is used to detect cycles between blocks. */
  private DirectedGraph<Block, DefaultEdge> blockGraph
    = new DefaultDirectedGraph<Block, DefaultEdge>(DefaultEdge.class);

  /** A data structure for name and scope signal look up. */
  private ScopeMap<Signal> signals;

  /** A data structure for indexed signal look up. */
  private ArrayList<Triple<String, Scope, Signal>> signalIndexes;

  /** Module definitions. */
  private Deque<Module> modules = new ArrayDeque<Module>();

  private Deque<ReferenceDecorator<Resource, Object>> resourceRefs
      = new ArrayDeque<ReferenceDecorator<Resource, Object>>();

  private ArrayList<ReferenceDecorator<Input, Object>> moduleInputs
      = new ArrayList<ReferenceDecorator<Input, Object>>();

  private ArrayList<ReferenceDecorator<Output, Object>> moduleOutputs
      = new ArrayList<ReferenceDecorator<Output, Object>>();

  /** Block references. */
  private Deque<Pair<Use, Scope>> uses = new ArrayDeque<Pair<Use, Scope>>();

  protected Deque<ReferenceDecorator<Property, Object>> propertyRefs
      = new ArrayDeque<ReferenceDecorator<Property, Object>>();

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

  //---------------------------------------------------------------- Processing

  /**
   *  A block which is currently expanded or null.
   *  It is for cycle detection.
   */
  private Block blockExpand;

  /**
   *  Entry point of the preprocessor. Given application
   *  serves as the input and output of this method.
   */
  public void process() {

    // Block expansion
    while (!uses.isEmpty()) {
      try {
        // get use to substitute
        Pair<Use, Scope> use = uses.pop();
        // get referenced block
        Block block
          = blocks.get(use.getLeft().getHref(), use.getLeft().getScope());
        // set appropriate scope
        scopePointer = new Scope(use.getRight());
        // set appropriate field for cycle detection
        blockExpand = block;
        // expand block
        block.expand(use.getLeft(), this);
        // detect cycles
        CycleDetector<Block, DefaultEdge> cycleDetector
          = new CycleDetector<Block, DefaultEdge>(blockGraph);
        if (cycleDetector.detectCycles()) {
          throw new SyntaxErrorException()
            .setCode(ExceptionCode.CYCLIC_DEFINITION)
            .set("block", block);
        }
      } catch (SyntaxErrorException e) {
        // block definition for given use is missing
        ErrorManager.newError()
          .setCause(e)
          .setCode(ErrorCode.BLOCK_EXPANSION);
      }
    }

    // resource substitution
    // for all of the modules, if there is a resource reference
    // find it and substitude in place of the reference
    while (!resourceRefs.isEmpty()) {
      try {
        ReferenceDecorator<Resource, Object> reference = resourceRefs.pop();
        Resource resource
          = resources.get(reference.getHref(), reference.getScope());
        reference.getDecorated().putConfiguration(resource);
      } catch (SyntaxErrorException e) {
        ErrorManager.newError()
          .setCause(e)
          .setCode(ErrorCode.RESOURCE_SUBSTITUTION);
      }
    }

    // property substitution
    while (!propertyRefs.isEmpty()) {
      try {
        ReferenceDecorator<Property, Object> reference = propertyRefs.pop();
        String value = getDefinition(reference.getHref(), reference.getScope());
        reference.getDecorated().setValue(value);
      } catch (SyntaxErrorException e) {
        ErrorManager.newError()
          .setCause(e)
          .setCode(ErrorCode.PROPERTY_SUBSTITUTION);
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
        inputRef.getDecorated().setSignal(signal);
        inputRef.getDecorated().putProperty("signal-name", inputRef.getHref());
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

    // Send configuration
    for (String key : configuration.keySet()) {
      //handler.set(key, configuration.get(key));
    }

    // Sends all of the modules
    //while (!modules.isEmpty()) {
      //handler.add(modules.pop());
    //}

    // print errors
    ErrorManager.print();

  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringMultilineStyle();
    toString(builder);
    return builder.toString();
  }

  public void toString(ToStringBuilder builder) {
    builder.append("configuration", configuration)
      .append("definitions", definitions)
      .append("resources", resources)
      .append("blocks", blocks)
      .append("blockGraph", blockGraph)
      .append("signals", signals)
      .append("signalIndexes", signalIndexes)
      .append("modules", modules)
      .append("resourceRefs", resourceRefs)
      .append("moduleInputs", moduleInputs)
      .append("moduleOutputs", moduleOutputs)
      .append("uses", uses)
      .append("propertyRefs", propertyRefs)
      .append("inputTags", inputTags)
      .append("outputTags", outputTags);
  }

}
