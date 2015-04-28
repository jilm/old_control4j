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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Map;
import java.text.MessageFormat;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import cz.lidinsky.tools.graph.Graph;
import cz.lidinsky.tools.graph.IGraph;

import static control4j.tools.Logger.*;

import control4j.tools.DuplicateElementException;

/**
 *
 *  Preprocessing of the application.
 *
 */
public class Preprocessor implements IGraph<Use>
{

  /**
   *  Does nothing.
   */
  public Preprocessor()
  { }

  private Application application;

  /**
   *  Entry point of the preprocessor. Given application
   *  serves as the input and output of this method.
   */
  public void process(Application application)
  {

    this.application = application;

    // blocks expansion
    // cycle test
    /*
    Graph<Use> graph = new Graph<Use>();
    boolean cycle = false;
    for (int i=0; i<application.getUseObjectsSize(); i++)
      if (!graph.isAcyclicDFS(this, application.getUse(i).getKey()))
      {
        System.out.println("Cyclic: "
            + application.getUse(i).getKey().getHref());
        cycle = true;
      }
      else
        System.out.println("Acyclic: "
            + application.getUse(i).getKey().getHref());
    */

    // expand all of the use objects
    //if (!cycle)
    while (application.getUseObjectsSize() > 0)
    {
      Pair<Use, Scope> use = application.getUse(0);
      expand(use.getKey(), use.getValue());
      application.removeUse(0);
    }

    // resource substitution
    // for all of the modules, if there is a resource reference
    // find it and substitude in place of the reference
    for (int i=0; i<application.getModulesSize(); i++)
    {
      Module module = application.getModule(i);
      System.out.println(module.getClassName());
      while(module.getResourceRefsSize() > 0)
      {
        Triple<String, String, Scope> resourceRef = module.getResourceRef(0);
        System.out.println(resourceRef.toString());
        try
        {
          Resource resource = application.getResource(
              resourceRef.getMiddle(), resourceRef.getRight());
          module.putResource(resourceRef.getLeft(), resource);
          module.removeResourceRef(0);
        }
        catch (NoSuchElementException e)
        {
          catched(getClass().getName(), "process", e); // TODO
        }
      }
    }

    // property substitution
    // global properties
    resolveConfiguration(application);
    // module properties
    for (int i=0; i<application.getModulesSize(); i++)
    {
      Module module = application.getModule(i);
      resolveConfiguration(module);
      // substitute properties of the resources of the module
      Set<String> keys = module.getResourceKeys();
      for (String key : keys)
        resolveConfiguration(module.getResource(key));
      // substitute properties of the input elements
      for (int j=0; j<module.getInputSize(); j++)
        if (module.getInput(j) != null)
          resolveConfiguration(module.getInput(j));
      for (int j=0; j<module.getVariableInputSize(); j++)
        resolveConfiguration(module.getVariableInput(j));
      // substitute properties of the output elements
      for (int j=0; j<module.getOutputSize(); j++)
        if (module.getOutput(j) != null)
          resolveConfiguration(module.getOutput(j));
      for (int j=0; j<module.getVariableOutputSize(); j++)
        resolveConfiguration(module.getVariableOutput(j));
    }
    // signal definitions properties
    for (int i=0; i<application.getSignalsSize(); i++)
    {
      Signal signal = application.getSignal(i).getRight();
      resolveConfiguration(signal);
      // tag properties
      Set<String> tagNames = signal.getTagNames();
      for (String name : tagNames)
        resolveConfiguration(signal.getTag(name));
    }

    for (int i=0; i<application.getModulesSize(); i++)
    {
      Module module = application.getModule(i);

      // create module input map
      // input with fixed index
      for (int j=0; j<module.getInputSize(); j++)
        if (module.getInput(j) != null)
          try
          {
            Input input = module.getInput(j);
            Signal signal
                = application.getSignal(input.getHref(), input.getScope());
            int signalIndex = application.getSignalIndex(signal);
            module.putInputSignalIndex(j, signalIndex);
          }
          catch (NoSuchElementException e) { } // TODO

      // input with variable index
      for (int j=0; j<module.getVariableInputSize(); j++)
        try
        {
          Input input = module.getVariableInput(j);
          Signal signal
              = application.getSignal(input.getHref(), input.getScope());
          int signalIndex = application.getSignalIndex(signal);
          module.addInputSignalIndex(signalIndex);
        }
        catch (NoSuchElementException e) { } // TODO

      // create module output map
      // output with fixed index
      for (int j=0; j<module.getOutputSize(); j++)
        if (module.getOutput(j) != null)
          try
          {
            Output output = module.getOutput(j);
            Signal signal
                = application.getSignal(output.getHref(), output.getScope());
            int signalIndex = application.getSignalIndex(signal);
            module.putOutputSignalIndex(j, signalIndex);
          }
          catch (NoSuchElementException e) { } // TODO

      // output with variable index
      for (int j=0; j<module.getVariableOutputSize(); j++)
        try
        {
          Output output = module.getVariableOutput(j);
          Signal signal
              = application.getSignal(output.getHref(), output.getScope());
          int signalIndex = application.getSignalIndex(signal);
          module.addOutputSignalIndex(signalIndex);
        }
        catch (NoSuchElementException e) { } // TODO

      // tagged signals
      if (module.getInputTagsSize() > 0 || module.getOutputTagsSize() > 0)
        for (int j=0; j<application.getSignalsSize(); j++)
        {
          Signal signal = application.getSignal(j).getRight();
          Set<String> tags = signal.getTagNames();
          for (String tag : tags)
          {
            if (module.containsInputTag(tag))
              module.addInputSignalIndex(j);
            if (module.containsOutputTag(tag))
              module.addOutputSignalIndex(j);
          }
        }
    }

    // cleen-up
    this.application = null;

  }

  /**
   *  Finds appropriate block definition and expand it into the
   *  application crate in place of the use object.
   *
   *  @param use
   *             a use element to expand
   *
   *  @param scope
   *             a scope under which the use element was defined
   */
  protected void expand(Use use, Scope scope)
  {
    try
    {
      // create the inner scope of the use element
      Scope innerScope = new Scope(scope);
      // get appropriate block definition
      Block block = application.getBlock(use.getHref(), use.getScope());
      // Pair a block input and output with the use's input and output
      Map<String, Input> inputSubstitution 
          = pairInput(use, block.getInputSet());
      Map<String, Output> outputSubstitution 
          = pairOutput(use, block.getOutputSet());
      // expand 
      expand(block, innerScope, inputSubstitution, outputSubstitution);
    }
    catch (NoSuchElementException e)
    {
      // TODO
    }
  }

  /**
   *  Expand the given block. Returns the use elements that were
   *  defined inside the block. These objects are not placed into
   *  the application object.
   *
   *  @param block
   *             a block definition to expand
   *
   *  @param scope
   *             a scope into which the content of the block will
   *             be placed
   *
   *  @param inputSubstitution
   *             a map of the block input to the signal references
   *             Accept the null value in case there in no block
   *             input.
   *
   *  @param outputSubstitution
   *             a map of the block output to the signal references.
   *             Accept the null value in case there is no block 
   *             output.
   */
  protected void expand(
      Block block, Scope scope, Map<String, Input> inputSubstitution,
      Map<String, Output> outputSubstitution)
  {
    // Expand all of the signal definitions
    for (control4j.application.nativelang.Signal signal : block.getSignals())
      translate(signal, scope);
    // Expand all of the modules
    for (control4j.application.nativelang.Module rawModule 
        : block.getModules())
    {
      Module module = translate(
          rawModule, scope, inputSubstitution, outputSubstitution);
      application.addModule(module);
    }
    // Expand all of the nested use objects
    for (control4j.application.nativelang.Use rawUse : block.getUseObjects())
    {
      Use use = new Use(
          rawUse.getHref(), 
          Translator.resolveScope(rawUse.getScope(), scope));
      rawUse.translate(use, scope);
      application.add(use, scope);
    }
  }

  /**
   *  Procedure that translate all of the signal definitions inside
   *  the block and place them into the application object.
   *
   *  @param block
   *             a block to expand
   *
   *  @param localScope
   *             an inner scope of the use element into which the
   *             block is expanded
   */
  protected void translate(
      control4j.application.nativelang.Signal rawSignal, Scope localScope)
  {
    Signal signal = new Signal();
    rawSignal.translate(signal, localScope);
    String name = rawSignal.getName();
    Scope scope = Translator.resolveScope(rawSignal.getScope(), localScope);
    try
    {
      application.putSignal(name, scope, signal);
    }
    catch (DuplicateElementException e) {}
  }

  /**
   *  Procedure that translate all of the modules inside
   *  the block and place them into the application object.
   *
   *  @param block
   *             a block to expand
   *
   *  @param localScope
   *             an inner scope of the use element into which the
   *             block is expanded
   */
  protected Module translate(
      control4j.application.nativelang.Module rawModule, 
      Scope localScope, 
      Map<String, Input> inputSubstitution, 
      Map<String, Output> outputSubstitution)
  {
      Module module = new Module(rawModule.getClassName());
      rawModule.translate(module, localScope, inputSubstitution,
          outputSubstitution);
      return module;
      // TODO
  }

  protected Map<String, Input> pairInput(Use use, Set<String> inputSet)
  {
    HashMap<String, Input> result = new HashMap<String, Input>();
    try
    {
      for (String alias : inputSet)
      {
        Input input = use.getInput(alias);
        result.put(alias, input);
      }
    }
    catch (NoSuchElementException e)
    {
      // TODO
    }
    return result;
  }

  protected Map<String, Output> pairOutput(Use use, Set<String> outputSet)
  {
    HashMap<String, Output> result = new HashMap<String, Output>();
    try
    {
      for (String alias : outputSet)
      {
        Output output = use.getOutput(alias);
        result.put(alias, output);
      }
    }
    catch (NoSuchElementException e)
    {
      // TODO
    }
    return result;
  }

  /**
   *  Goes through the all of the configuration items of the
   *  given object that were specified in the form of reference,
   *  finds appropriate referenced object and substitude the
   *  value for the reference inside the object.
   */
  protected void resolveConfiguration(Configurable object)
  {
    while(object.getConfigItemRefsSize() > 0)
    {
      Triple<String, String, Scope> reference 
          = object.getConfigItemReference(0);
      try
      {
        String value = application.getDefinition(
            reference.getMiddle(), reference.getRight());
        object.removeConfigItemReference(0);
        object.putProperty(reference.getLeft(), value);
      }
      catch (NoSuchElementException e)
      {
        //reportMissingDefinition(reference.getLeft(), reference, object); // TODO
        warning("Missing definition");
      }
      catch (DuplicateElementException e)
      {  
        warning("Duplicate element");
      } // TODO
    }
  }

  /**
   *  For debug purposes
   */
  public static void main(String[] args) throws Exception
  {
  }

  /**
   *  Returns the direct successors of the use element.
   *  This method is used by the algorithm for test of
   *  acyclic property.
   */
  public Collection<Use> getDirectSuccessors(Use use)
  {
    try
    {
      // find given use in the application
      Scope scope = use.getScope();
      // find appropriate block
      Block block = application.getBlock(use.getHref(), use.getScope());
      // Expand all of the nested use objects
      int size = block.getUseObjects().size();
      ArrayList<Use> nestedUseObjects = new ArrayList<Use>(size);
      for (control4j.application.nativelang.Use rawUse : block.getUseObjects())
      {
        Use nestedUse = new Use(
            rawUse.getHref(),
            Translator.resolveScope(rawUse.getScope(), scope));
        rawUse.translate(nestedUse, scope);
        nestedUseObjects.add(nestedUse);
      }
      return nestedUseObjects;
    }
    catch (NoSuchElementException e) {} // TODO
    return null; // TODO
  }

  /*
   *
   *     Report Errors
   *
   */

  /**
   *  Reports a message about missing definition.
   */
  private void reportMissingDefinition(
      String key, Reference reference, DeclarationBase object)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Cannot find appropriate define element for a property.\n")
      .append("Key: {0}, href: {1}.")
      .append("The property element is a part of:\n{2}\n");
    String message = MessageFormat.format(
        sb.toString(), key, reference.getHref(),
        object.getDeclarationReferenceText());
    ErrorManager.getInstance().addError(message);
  }

}
