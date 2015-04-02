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

import java.util.HashMap;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 *
 *  References and places a block into the processing.
 *
 */
public class Use extends Configurable
{

  private String href;
  private Scope scope;

  public Use(String href, Scope scope)
  {
    super();
    this.href = href;
    this.scope = scope;
  }

  public String getHref()
  {
    return href;
  }

  public Scope getScope()
  {
    return scope;
  }

  /*
   *
   *     Input
   *
   */

  private HashMap<String, Input> inputMap;

  public void putInput(String index, Input input)
  {
    if (inputMap == null)
      inputMap = new HashMap<String, Input>();
    inputMap.put(index, input);
  }

  public Input getInput(String index)
  {
    if (inputMap == null)
      throw new NoSuchElementException();
    Input input = inputMap.get(index);
    if (input == null)
      throw new NoSuchElementException();
    return input;
  }

  /*
   *
   *     Output
   *
   */

  private HashMap<String, Output> outputMap;

  public void putOutput(String index, Output output)
  {
    if (outputMap == null)
      outputMap = new HashMap<String, Output>();
    outputMap.put(index, output);
  }

  public Output getOutput(String index)
  {
    if (outputMap == null)
      throw new NoSuchElementException();
    Output output = outputMap.get(index);
    if (output == null)
      throw new NoSuchElementException();
    return output;
  }

  /*
   *
   *     Other
   *
   */

  @Override
  public boolean equals(Object object)
  {
    if (object == null) return false;
    if (object == this) return true;
    if (object.getClass() != getClass()) return false;
    Use compared = (Use)object;
    return new EqualsBuilder()
	.append(href, compared.href)
	.append(scope, compared.scope)
	.append(inputMap, compared.inputMap)
	.append(outputMap, compared.outputMap)
	.isEquals();
  }

  @Override
  public int hashCode()
  {
    int hash = href.hashCode();
    hash = hash ^ scope.hashCode();
    if (inputMap != null)
      hash = hash ^ inputMap.hashCode();
    if (outputMap != null)
      hash = hash ^ outputMap.hashCode();
    return hash;
  }

  @Override
  void toString(String indent, StringBuilder sb)
  {
    sb.append(indent).append("Use\n");
    String indent2 = indent + "  ";
    sb.append(indent2)
      .append("href=")
      .append(href)
      .append("\n");
    sb.append(indent2)
      .append("scope=")
      .append(scope.toString())
      .append("\n");
    super.toString(indent2, sb);
    if (inputMap != null && inputMap.size() > 0)
    {
      java.util.Set<String> keys = inputMap.keySet();
      for (String key : keys)
      {
	Input input = inputMap.get(key);
	sb.append(indent2).append("Input[").append(key)
	  .append("]=");
	input.toString(indent2, sb);
      }
    }
    if (outputMap != null && outputMap.size() > 0)
    {
      java.util.Set<String> keys = outputMap.keySet();
      for (String key : keys)
      {
	Output output = outputMap.get(key);
	sb.append(indent2).append("Output[").append(key)
	  .append("]=");
	output.toString(indent2, sb);
      }
    }
  }

}
