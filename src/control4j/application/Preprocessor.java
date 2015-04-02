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

import cz.lidinsky.tools.graph.Graph;
import cz.lidinsky.tools.graph.IGraph;

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

    // cycle test
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

    // expand all of the use objects
    if (!cycle)
    while (application.getUseObjectsSize() > 0)
    {
      Pair<Use, Scope> use = application.getUse(0);
      expand(use.getKey(), use.getValue());
      application.removeUse(0);
    }

    // resolve configuration on the application level
    resolveConfiguration(application);

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
      expandSignalDefinitions(block, scope);
      // Expand all of the modules
      expandModules(block, scope, inputSubstitution, outputSubstitution);
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
  protected void expandSignalDefinitions(Block block, Scope localScope)
  {
    for (control4j.application.nativelang.Signal signal : block.getSignals())
    {
      Signal translatedSignal = new Signal();
      signal.translate(translatedSignal, localScope);
      String name = signal.getName();
      Scope scope = Translator.resolveScope(signal.getScope(), localScope);
      try
      {
      application.putSignal(name, scope, translatedSignal);
      }
      catch (DuplicateElementException e) {}
    }
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
  protected void expandModules(
      Block block, Scope localScope, Map<String, Input> inputSubstitution,
      Map<String, Output> outputSubstitution)
  {
    for (control4j.application.nativelang.Module module : block.getModules())
    {
      Module translated = new Module(module.getClassName());
      module.translate(translated, localScope, inputSubstitution,
	  outputSubstitution);
      application.addModule(translated);
      // TODO
    }
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
    Set<String> keys = object.getReferenceConfigKeys();
    for (String key : keys)
    {
      Reference reference = object.getReferenceConfigItem(key);
      try
      {
        String value = application.getDefinition(
	    reference.getHref(), reference.getScope());
        object.resolveConfigItem(key, value);
      }
      catch (NoSuchElementException e)
      {
	reportMissingDefinition(key, reference, object);
      }
    }
  }

  /**
   *  For debug purposes
   */
  public static void main(String[] args) throws Exception
  {
    String filename = args[0];
    java.io.File file = new java.io.File(filename);
    Loader loader = new Loader();
    ITranslatable translatable = loader.load(file);
    Application app = new Application();
    translatable.translate(app);
    Preprocessor preprocessor = new Preprocessor();
    preprocessor.process(app);
    System.out.println(app.toString());
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
