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

package control4j.application;

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.CommonException;
import control4j.Instantiator;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringMultilineStyle;

import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.collections4.CollectionUtils;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedSubgraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *  Performs a toplogical sort of the modules.
 *
 */
public class Sorter implements IToStringBuildable {

  /**
   *  Creates an empty object.
   */
  public Sorter() { }

  //---------------------------------------------------------- Public Interface

  /**
   *  Adds a module.
   */
  void add(Module module) {
    dirty = true;
    graph.addVertex(module);
    addEdges(module);
  }

  /** Global configuration. */
  private HashMap<String, Property> configuration
      = new HashMap<String, Property>();

  /**
   *  Sets global configuration.
   */
  public void set(String key, Property value) {
    configuration.put(key, value);
    // TODO:
  }

  private boolean dirty = false;

  /**
   *  Provides a topological sort of the modules.
   */
  public void process(Instantiator handler) {

    if (!isResolved()) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "The graph is still not complete!");
    }

    while (dirty) {
      // break cycles
      breakFeedback();
    }

    // perform sorting
    TopologicalOrderIterator<Module, DefaultEdge> topolIter
        = new TopologicalOrderIterator<Module, DefaultEdge>(graph);
    while (topolIter.hasNext()) {
      handler.instantiate(topolIter.next());
    }

    ErrorManager.print();

  }

  //--------------------------------------------------- The Graph Manipulation.

  /**
   *  Graph is used to provide topological sort. Vertices are modules and
   *  edges are signal connections between modules.
   */
  private DefaultDirectedGraph<Module, DefaultEdge> graph
        = new DefaultDirectedGraph<Module, DefaultEdge>(DefaultEdge.class);

  /**
   *  A set that contains vertices that are still not resolved. Because
   *  of missing source module.
   */
  private Set<Module> unresolved = new HashSet<Module>();

  private boolean isResolved() {
    return unresolved.isEmpty();
  }

  /**
   *  Adds all of the incoming and otgoing edges of that module. If there
   *  is some input unresolved (the source module is not in the graph yet)
   *  than this module is temporarily stored in the unresolved set.
   */
  private void addEdges(Module module) {
    // place the module into the output index
    for (Output output : module.getOutput()) {
      if (output.isConnected()) {
        setSourceModule(output.getPointer(), module);
      }
    }
    // place the module into the unresolved set
    unresolved.add(module);
    // try to resolve all of the unresolved modules
    Set<Module> temp = unresolved;
    unresolved = new HashSet<Module>();
    for (Module target : temp) {
      for (Input input : target.getInput()) {
        // add edge into the graph
        if (input.isConnected()) {
          try {
            Module source = getSourceModule(input.getPointer());
            graph.addEdge(source, target);
          } catch (Exception e) {
            // the source module is still not available
            unresolved.add(target);
          }
        }
      }
    }
  }

  //------------------------------------------------------------- Signal Index.

  /**
   *  Map signal pointers to modules which provides it. Indexes of the
   *  array corespond to the position of the signal and the element
   *  of the array contains a module that provides it.
   */
  private Module[] signalIndex;

  /**
   *  Returns a module which is the source of the signal with the given
   *  pointer number.
   *
   *  @throws CommonException
   *              if there is no module for given pointer in the index
   *
   *  @throws CommonException
   *              if the module under the requested index is null
   *
   *  @throws IndexOutOfBoundsException
   *              if the given index is not within internal index buffer bounds
   */
  private Module getSourceModule(int signalPointer) {
    if (signalIndex == null) {
      throw new CommonException()
        .setCode(ExceptionCode.INDEX_OUT_OF_BOUNDS)
        .set("message", "Signal index has not been created yet!")
        .set("signalPointer", signalPointer);
    } else {
      return notNull(signalIndex[signalPointer]);
    }
  }

  /**
   *  Place given module into the signal index buffer under the given position.
   *  The buffer is realocated automaticaly to be large enough.
   *
   *  @throws CommonException
   *             if there already is a module under the given position
   *
   */
  private void setSourceModule(int signalPointer, Module module) {
    // create buffer is necessary
    if (signalIndex == null) {
      signalIndex = new Module[signalPointer + 1];
    }
    // realocate buffer if necessary
    if (signalIndex.length <= signalPointer) {
      Module[] newIndex = new Module[signalPointer + 1];
      System.arraycopy(signalIndex, 0, newIndex, 0, signalIndex.length);
      signalIndex = newIndex;
    }
    // store the module
    signalIndex[signalPointer] = module;
  }

  /**
   *  Returns total number of signals.
   */
  private int getSignalCount() {
    return signalIndex == null ? 0 : signalIndex.length;
  }

  /**
   *  Fill-in the signal to module map.
   */
  protected void addToSignalIndex(Module module) {

    try {
      // add output with given index
      for (int i = 0; i < module.getOutputSize(); i++) {
        if (module.getOutput(i) != null) {
          int out = module.getOutput(i).getPointer();
          if (getSourceModule(out) == null) {
            setSourceModule(out, module);
          } else {
            // more interconnected outputs
            throw new CommonException()
              .setCode(ExceptionCode.DUPLICATE_ELEMENT)
              .set("message", "Modules output interconnect")
              .set("signal", i)
              .set("module1",
                  getSourceModule(out).getDeclarationReferenceText())
              .set("module2", module.getDeclarationReferenceText())
              .set("method", "addToSignalIndex")
              .set("class", getClass().getName());
          }
        }
      }
    } catch (SyntaxErrorException e) {
      ErrorManager.newError()
        .setCause(e)
        .setCode(ErrorCode.SIGNAL_INDEX);
    }
  }

  //------------------------------------------------------- Feedback Treatment.

  /**
   *  Breaks a feedback, cycle in the directed graph, which contain a
   *  particular vertex, module.
   */
  private void breakFeedback() {

    CycleDetector<Module, DefaultEdge> cycleDetector
      = new CycleDetector<Module, DefaultEdge>(graph);
    while (cycleDetector.detectCycles()) {
      // find cycles
      DirectedSimpleCycles<Module, DefaultEdge> cycleFinder
        = new TarjanSimpleCycles<Module, DefaultEdge>(graph);
      List<List<Module>> cycles = cycleFinder.findSimpleCycles();
      // go through all of the cycles a break them
      for (List<Module> cycle : cycles) {
        for (Module srcModule : cycle) {
          Collection<Module> destModules
            = Graphs.successorListOf(graph, srcModule);
          destModules = CollectionUtils.intersection(cycle, destModules);
          for (Module destModule : destModules) {
            if (hasEdgeDefaultValue(srcModule, destModule)) {
              breakEdge(srcModule, destModule);
              return;
            }
          }
        }
        // The cycle is not breakable
        throw new CommonException()
          .setCode(ExceptionCode.CYCLIC_DEFINITION)
          .set("message", "The graph contains unbreakable cycle!");
      }
    }

  }

  /**
   *  Returns true if and only if all of the signals that are connected
   *  from source module to the target module has default values defined.
   *  If there is no such connection between the given modules, it returns
   *  false.
   */
  private boolean hasEdgeDefaultValue(Module source, Module target) {
    boolean connected = false; // if there is connection between modules
    for (Output output : source.getOutput()) {
      for (Input input : target.getInput()) {
        if (output.isConnected() && input.isConnected()
            && output.getPointer() == input.getPointer()) {
          connected = true;
          if (!input.getSignal().isValueT_1Specified()) {
            return false;
          }
        }
      }
    }
    return connected ? true : false;
  }

  /**
   *  Breaks the direct connection between two modules.
   */
  private void breakEdge(Module source, Module target) {
    // remove the broken edge from the graph
    graph.removeEdge(source, target);
    for (Output output : source.getOutput()) {
      for (Input input : target.getInput()) {
        if (output.isConnected() && input.isConnected()
            && output.getPointer() == input.getPointer()) {
          // if the signal connects the given modules, break it
          // Create a shared handover place.
          MutableObject<control4j.Signal> sharedSignal
            = new MutableObject<control4j.Signal>();
          sharedSignal.setValue(
              input.getSignal().isValueT_1Valid()
              ? control4j.Signal.getSignal(input.getSignal().getValueT_1())
              : control4j.Signal.getSignal());
          // Create new signal place for the source module
          int brokenPointer = getSignalCount();
          output.setPointer(brokenPointer);
          // Create new output module.
          FeedbackModule outModule = new FeedbackModule(
              FeedbackModule.OUTPUT_CLASSNAME, sharedSignal);
          Output brokenOutput = new Output();
          brokenOutput.setPointer(input.getPointer());
          outModule.putOutput(0, brokenOutput);
          add(outModule);
          // Create new input module
          FeedbackModule inModule = new FeedbackModule(
              FeedbackModule.INPUT_CLASSNAME, sharedSignal);
          Input brokenInput = new Input();
          brokenInput.setPointer(brokenPointer);
          brokenInput.setSignal(input.getSignal());
          add(inModule);
        }
      }
    }
  }

  //-------------------------------------------------------------------- Other.

  @Override
  public String toString() {
    return new ToStringMultilineStyle()
      .append(this)
      .toString();
  }

  public void toString(ToStringBuilder sb) {
    sb.append("configuration", configuration)
      .append("graph", graph);
  }

}
