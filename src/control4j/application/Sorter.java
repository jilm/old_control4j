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
import java.util.List;

/**
 *
 *  Performs a toplogical sort of the modules.
 *
 */
public class Sorter {

  /**
   *  Creates an empty object.
   */
  public Sorter() { }

  /** Processed application. */
  private Application application;

  /**
   *  Provides a topological sort of the modules.
   */
  public void process(Application application) {

    this.application = application;

    // initialize internal structures
    init();

    // create graph
    DirectedAcyclicGraph<Module, DefaultEdge> graph
        = new DirectedAcyclicGraph<Module, DefaultEdge>(DefaultEdge.class);
    initGraph(graph);

    // detect a cycle
    CycleDetector<Module, DefaultEdge> cycleDetector
        = new CycleDetector<Module, DefaultEdge>(graph);
    System.out.println(cycleDetector.findCycles());

    // perform sorting
    TopologicalOrderIterator<Module, DefaultEdge> topolIter
        = new TopologicalOrderIterator<Module, DefaultEdge>(graph);
    application.removeModules();
    while (topolIter.hasNext()) {
      application.addModule(topolIter.next());
    }

  }

  /** Mapping signals to modules which provides it. Indexes of the
      array corespond to the position of the signal and the element
      of the array corespond to the position of the module which
      provides it. This is auxiliary array. */
  private int[] signalModuleMap;

  /**
   *  Fill-in the signal to module map.
   */
  protected void init() {

    signalModuleMap = new int[application.getSignalsSize()];
    Arrays.fill(signalModuleMap, -1);

    for (int i = 0; i < application.getModulesSize(); i++) {
      Module module = application.getModule(i);
      // add output with given index
      for (int j = 0; j < module.getOutputSize(); j++) {
        if (module.getOutput(j) != null) {
          int out = module.getOutput(j).getPointer();
          if (signalModuleMap[out] < 0) {
            signalModuleMap[out] = i;
          } else {
            // TODO more interconnected outputs
          }
        }
      }
    }
  }

  private ArrayList<Triple<Module, Module, Integer>> delayedEdges;

  /**
   *  Creates and returns a directed graph where vertices are
   *  modules and edges represents modules connections.
   */
  protected void initGraph(DirectedGraph<Module, DefaultEdge> graph) {

    delayedEdges = new ArrayList<Triple<Module, Module, Integer>>();

    // add vertices into the graph
    for (int i = 0; i < application.getModulesSize(); i++) {
      graph.addVertex(application.getModule(i));
    }

    // add edges into the graph
    for (int i = 0; i < application.getModulesSize(); i++) {
      Module module2 = application.getModule(i);
      // for all of the input with given index
      //addEdges(graph, module2, module2.getFixedInputMap()); TODO:
      // for all of the input with variable index
      //addEdges(graph, module2, module2.getVariableInputMap()); TODO:
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
  protected void addEdges(
      DirectedGraph graph, Module module2, List<Integer> inputMap) {

    // for all of the input signals of the module2
    for (int in : inputMap) {
      if (in >= 0) {
        if (signalModuleMap[in] >= 0) {

          Module module1 = application.getModule(signalModuleMap[in]);

          if (application.getSignal(in).getRight().isValueT_1Specified()) {
            // if the signal has default value specified
            // postphone the edge
            delayedEdges.add(new ImmutableTriple<Module, Module, Integer>(
                module1, module2, in));
          } else { // else insert it into the graph
            graph.addEdge(module1, module2);
          }

        } else {
          // TODO missing output for this signal
        }
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    java.io.File file = new java.io.File(args[0]);
    Loader loader = new Loader();
    Application application = loader.load(file);
    Preprocessor preprocessor = new Preprocessor();
    preprocessor.process(); // TODO:
    Sorter sorter = new Sorter();
    sorter.process(application);
    System.out.println(application.toString());
  }

}
