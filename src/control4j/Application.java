package control4j;

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

import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringStyle;
import cz.lidinsky.tools.IToStringBuildable;

//import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

class Application implements IToStringBuildable
{

  Application() { }

  Pair<InputModule, int[]>[] inputModules;

  Triple<ProcessModule, int[], int[]>[] processModules;

  Pair<OutputModule, int[]>[] outputModules;

  void add(Module module, int[] inputMap, int[] outputMap)
  {
  }

  @Override
  public String toString()
  {
    return new ToStringBuilder(new ToStringStyle()).append(this).toString();
  }

  public String toString(ToStringBuilder builder)
  {
    return builder.append("inputModules", inputModules)
        .append("processModules", processModules)
        .append("outputModules", outputModules)
        .toString();
  }

}
