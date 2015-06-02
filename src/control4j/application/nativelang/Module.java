package control4j.application.nativelang;

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
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static control4j.tools.LogMessages.getMessage;
import static control4j.tools.Logger.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.xml.sax.Attributes;

import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringStyle;
import cz.lidinsky.tools.IToStringBuildable;

import control4j.application.Scope;

/**
 *
 *  Stands for a module element.
 *
 */
public class Module extends DescriptionBase {

  public Module() {}

  private String className;

  public String getClassName() {
    check();
    return className;
  }

  Module setClassName(String className) {
    this.className = trim(notBlank(className, getMessage("msg004",
        "class name", getDeclarationReferenceText())));
    return this;
  }

  private ArrayList<Resource> resources;

  void add(Resource resource) {
    notNull(resource, getMessage("msg006", "resource",
        getDeclarationReferenceText()));
    if (resources == null) {
      resources = new ArrayList<Resource>();
    }
    resources.add(resource);
  }

  public Collection<Resource> getResources() {
    return unmodifiableCollection(emptyIfNull(resources));
  }

  private ArrayList<Input> input;

  void add(Input input) {
    notNull(input, getMessage("msg006", "input",
        getDeclarationReferenceText()));
    if (this.input == null) {
      this.input = new ArrayList<Input>();
    }
    this.input.add(input);
  }

  public Collection<Input> getInput() {
    return unmodifiableCollection(emptyIfNull(input));
  }

  private ArrayList<Output> output;

  void add(Output output) {
    notNull(output, getMessage("msg006", "output",
        getDeclarationReferenceText()));
    if (this.output == null) {
      this.output = new ArrayList<Output>();
    }
    this.output.add(output);
  }

  public Collection<Output> getOutput() {
    return unmodifiableCollection(emptyIfNull(output));
  }

  private ArrayList<String> inputTags;

  void addInputTag(String tag) {
    tag = trim(notBlank(tag, getMessage("msg004", "input tag",
        getDeclarationReferenceText())));
    if (inputTags == null) {
      inputTags = new ArrayList<String>();
    }
    inputTags.add(tag);
  }

  public Collection<String> getInputTags() {
    return unmodifiableCollection(emptyIfNull(inputTags));
  }

  private ArrayList<String> outputTags;

  void addOutputTag(String tag) {
    tag = trim(notBlank(tag, getMessage("msg004", "output tag",
        getDeclarationReferenceText())));
    if (outputTags == null) {
      outputTags = new ArrayList<String>();
    }
    outputTags.add(tag);
  }

  public Collection<String> getOutputTags() {
    return unmodifiableCollection(emptyIfNull(outputTags));
  }

  /**
   *  Creates an empty module object.
   *
   *  @param className
   *             a name of the class that implements functionality
   *             of a module
   */
  public Module(String className) {
    setClassName(className);
  }

  protected void check() { }

  /**
   *  Translate this module object to the given one.
   *
   *  @param destination
   *             an object where translated information will be stored
   *
   *  @param localScope
   *             a scope under which the module object was defined.
   *
   *  @param inputSubstitution
   *             a map alias - Input which is used for block expansion.
   *             Null value is accepted.
   *
   *  @param outputSubstitution
   *             a map alias - Ouput that is used for block expansion.
   *             Null value is accepted.
   */
  public void translate(
      control4j.application.Module destination, Scope localScope,
      Map<String, control4j.application.Input> inputSubstitution,
      Map<String, control4j.application.Output> outputSubstitution) {

    //check();

    // translate configuration
    super.translate(destination, localScope);

    // translate resource definitions
    if (resources != null) {
      for (Resource resource : resources) {
        if (resource.isReference()) {
	  // resource references are translated in the adapter
        } else {
          control4j.application.Resource translated
              = new control4j.application.Resource(resource.getClassName());
          resource.translate(translated, localScope);
          destination.putResource(resource.getKey(), translated);
        }
      }
    }

    // translate input
    if (input != null) {
      for (Input inp : input) {
        control4j.application.Input translated;
        // if the scope is local and the name is present in the
        // input substitution map, use the input from that map
        if (inp.getScope() == Parser.LOCAL_SCOPE_CODE
            && inputSubstitution != null
            && inputSubstitution.containsKey(inp.getHref())) {
          translated = inputSubstitution.get(inp.getHref());
        } else {
          // if this input is not a block input
          translated = new control4j.application.Input(
              resolveScope(inp.getScope(), localScope), inp.getHref());
          inp.translate(translated, localScope);
        }
        // store translated input into the module object
        String strIndex = inp.getIndex();
        if (strIndex == null) {
          // an input with variable index
          destination.putInput(translated);
        } else {
          // an input with fixed index
          try {
            int index = Integer.parseInt(strIndex);
            destination.putInput(index, translated);
          } catch (NumberFormatException e) {
            // TODO:
          }
        }
      }
    }

    // translate output
    if (output != null) {
      for (Output out : output) {
        control4j.application.Output translated;
        // if the scope is local and the name is present in the
        // output substitution map, use the output from that map
        if (out.getScope() == Parser.LOCAL_SCOPE_CODE
            && outputSubstitution != null
            && outputSubstitution.containsKey(out.getHref())) {
          translated = outputSubstitution.get(out.getHref());
        } else {
          // if this output is not a block output
          translated = new control4j.application.Output(
              resolveScope(out.getScope(), localScope), out.getHref());
          out.translate(translated, localScope);
        }
        // store translated output into the module object
        String strIndex = out.getIndex();
        if (strIndex == null) {
          // an output with variable index
          destination.putOutput(translated);
        } else {
          // an output with fixed index
          try {
            int index = Integer.parseInt(strIndex);
            destination.putOutput(index, translated);
          } catch (NumberFormatException e) {
            // TODO:
          }
        }
      }
    }

    // translate tagged input
    if (inputTags != null) {
      for (String tag : inputTags) {
        destination.addInputTag(tag);
      }
    }

    // translate tagged output
    if (outputTags != null) {
      for (String tag : outputTags) {
        destination.addOutputTag(tag);
      }
    }

  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("className", className)
        .append("resources", resources)
        .append("input", input)
        .append("output", output)
        .append("inputTags", inputTags)
        .append("outputTags", outputTags);
  }

}
