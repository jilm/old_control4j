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

import control4j.ExceptionCode;
import control4j.SyntaxErrorException;
import control4j.Instantiator;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringMultilineStyle;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 *  Performs a toplogical sort of the modules.
 *
 */
public class Sorter implements IToStringBuildable {

  //---------------------------------------------------------- Public Interface

  /**
   *  Creates an empty object.
   */
  public Sorter() { }

  void add(Module module) {
    graph.addVertex(module);
    addToSignalIndex(module);
  }

  private HashMap<String, Property> configuration
      = new HashMap<String, Property>();

  public void set(String key, Property value) {
    configuration.put(key, value);
    // TODO:
  }

  private DirectedAcyclicGraph<Module, DefaultEdge> graph
        = new DirectedAcyclicGraph<Module, DefaultEdge>(DefaultEdge.class);

  /**
   *  Provides a topological sort of the modules.
   */
  public void process(Instantiator handler) {

    // send configuration
    for (String key : configuration.keySet()) {
      handler.set(key, configuration.get(key).getValue());
      // TODO:  exception handling
    }

    // create graph
    initGraph();

    // detect a cycle
    CycleDetector<Module, DefaultEdge> cycleDetector
        = new CycleDetector<Module, DefaultEdge>(graph);
    System.out.println(cycleDetector.findCycles());

    // perform sorting
    TopologicalOrderIterator<Module, DefaultEdge> topolIter
        = new TopologicalOrderIterator<Module, DefaultEdge>(graph);
    while (topolIter.hasNext()) {
      handler.instantiate(topolIter.next());
    }

    ErrorManager.print();

  }

  /** Mapping signals to modules which provides it. Indexes of the
      array corespond to the position of the signal and the element
      of the array contains a module that provides it. */
  private Module[] signalIndex;

  private Module getSourceModule(int signalPointer) {
    if (signalIndex == null) {
      return null;
    } else if (signalIndex.length <= signalPointer) {
      return null;
    } else {
      return signalIndex[signalPointer];
    }
  }

  private void setSourceModule(int signalPointer, Module module) {
    if (signalIndex == null) {
      signalIndex = new Module[signalPointer + 1];
    }
    if (signalIndex.length <= signalPointer) {
      Module[] newIndex = new Module[signalPointer + 1];
      System.arraycopy(signalIndex, 0, newIndex, 0, signalIndex.length);
      signalIndex = newIndex;
    }
    signalIndex[signalPointer] = module;
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
            throw new SyntaxErrorException()
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

  private ArrayList<Triple<Module, Module, Integer>> delayedEdges;

  /**
   *  Creates and returns a directed graph where vertices are
   *  modules and edges represents modules connections.
   */
  protected void initGraph() {

    delayedEdges = new ArrayList<Triple<Module, Module, Integer>>();

    // add edges into the graph
    for (Module module : graph.vertexSet()) {
      // add edges for all of the module input
      addEdges(module);
    }

    // add edges with default value
    for (Triple<Module, Module, Integer> edge : delayedEdges) {
      try {
        // try to add it to the graph
        graph.addEdge(edge.getLeft(), edge.getMiddle());
      } catch (IllegalArgumentException e) {
        // treated cycle detected
        // TODO
        System.out.println("treated cycle detected");
      }
    }

  }

  /**
   *
   */
  protected void addEdges(Module module) {

    // for all of the input signals of the module
    for (int i = 0; i < module.getInputSize(); i++) {
      Input input = module.getInput(i);
      if (input != null) {
        Module sourceModule = getSourceModule(input.getPointer());
        if (sourceModule != null) {
          // TODO: if the signal has default value specified  postphone the edge
          graph.addEdge(sourceModule, module);
        } else {
          // TODO: there is no output for the input !
        }
      }
    }
  }

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
