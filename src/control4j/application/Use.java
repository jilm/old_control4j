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

  private HashMap<String, Input> outputMap;

  public void putOutput(String index, Output output)
  {
    if (outputMap == null)
      outputMap = new HashMap<String, Output>();
    outputMap.put(index, output);
  }

}
