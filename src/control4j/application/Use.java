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

  private HashMap<String, Input> inputMap;

  public void putInput(String index, Input input)
  {
    if (inputMap == null)
      inputMap = new HashMap<String, Input>();
    inputMap.put(index, input);
  }

  private HashMap<String, Output> outputMap;

  public void putOutput(String index, Output output)
  {
    if (outputMap == null)
      outputMap = new HashMap<String, Output>();
    outputMap.put(index, output);
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
