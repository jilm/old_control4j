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
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;

import java.util.ArrayList;
import java.util.Collection;
import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.IXmlHandler;
import control4j.tools.ParseException;
import control4j.tools.XmlReader;
import control4j.tools.XmlStartElement;
import control4j.tools.XmlEndElement;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents a use element.
 *
 */
public class Use extends Configurable implements IAdapter
{

  public Use() {}

  private String href;

  public String getHref() {
    return href;
  }

  Use setHref(String href) {
    this.href = href;
    return this;
  }

  private int scope;

  public int getScope() {
    return scope;
  }

  Use setScope(int scope) {
    this.scope = scope;
    return this;
  }

  private ArrayList<Input> input;

  void add(Input input) {
    notNull(input);
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
    notNull(output);
    if (this.output == null) {
      this.output = new ArrayList<Output>();
    }
    this.output.add(output);
  }

  public Collection<Output> getOutput() {
    return unmodifiableCollection(emptyIfNull(output));
  }

  public void translate(
      control4j.application.Use destination, Scope localScope)
  {
    // translate configuration
    super.translate(destination, localScope);

    // translate input
    if (input != null)
      for (Input inp : input)
      {
        control4j.application.Input destInput
            = new control4j.application.Input(
            resolveScope(inp.getScope(), localScope), inp.getHref());
        inp.translate(destInput, localScope);
        destination.putInput(inp.getIndex(), destInput);
      }

    // translate output
    if (output != null)
      for (Output out : output)
      {
        control4j.application.Output destOutput
            = new control4j.application.Output(
            resolveScope(out.getScope(), localScope), out.getHref());
        out.translate(destOutput, localScope);
        destination.putOutput(out.getIndex(), destOutput);
      }
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("href", href)
        .append("scope", scope)
        .append("input", input)
        .append("output", output);
  }

}
