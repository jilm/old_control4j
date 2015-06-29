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

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  References and places a block into the processing.
 *
 */
public class Use extends Configurable {

  private String href;
  private Scope scope;

  public Use(String href, Scope scope) {
    super();
    this.href = href;
    this.scope = scope;
  }

  public String getHref() {
    return href;
  }

  public Scope getScope() {
    return scope;
  }

  /*
   *
   *     Input
   *
   */

  private HashMap<String, ReferenceDecorator<Input, Object>> inputMap;

  public void putInput(String index, ReferenceDecorator<Input, Object> input) {
    if (inputMap == null)
      inputMap = new HashMap<String, ReferenceDecorator<Input, Object>>();
    inputMap.put(index, input);
  }

  public ReferenceDecorator<Input, Object> getInput(String index) {
    if (inputMap == null)
      throw new NoSuchElementException();
    ReferenceDecorator<Input, Object> input = inputMap.get(index);
    if (input == null)
      throw new NoSuchElementException();
    return input;
  }

  /*
   *
   *     Output
   *
   */

  private HashMap<String, ReferenceDecorator<Output, Object>> outputMap;

  public void putOutput(
      String index, ReferenceDecorator<Output, Object> output) {
    if (outputMap == null)
      outputMap = new HashMap<String, ReferenceDecorator<Output, Object>>();
    outputMap.put(index, output);
  }

  public ReferenceDecorator<Output, Object> getOutput(String index) {
    if (outputMap == null)
      throw new NoSuchElementException();
    ReferenceDecorator<Output, Object> output = outputMap.get(index);
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
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("href", href)
        .append("scope", scope)
        .append("inputMap", inputMap)
        .append("outputMap", outputMap);
  }

}
