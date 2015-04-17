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

import java.util.LinkedList;

class Application implements IToStringBuildable
{

  /** Duration of the whole cycle in ms */
  private int cyclePeriod = 1000;
  private int startCycleDelay = 200;

  int dataBufferSize;

  LinkedList<ICycleEventListener> eventListeners
      = new LinkedList<ICycleEventListener>();

  /**
   *  Specify duration of the one cycle loop in ms.
   *
   *  @param period
   *             period of one cycle loop in ms. Must be
   *             greater than zero
   */
  @ConfigItem(key="cycle-period", optional=true)
  public void setCyclePeriod(int period)
  {
    if (period > 0)
      cyclePeriod = period;
    // else !!! TODO:
  }

  @ConfigItem(key="start-delay", optional=true)
  public void setStartCycleDeley(int delay)
  {
    if (delay > 0)
      startCycleDelay = delay;
    // TODO: else
  }

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
    return new ToStringBuilder(new ToStringStyle())
	.append(this)
	.toString();
  }

  public String toString(ToStringBuilder builder)
  {
    return builder.append("cyclePeriod", cyclePeriod)
        .append("startCycleDelay", startCycleDelay)
	.append("dataBufferSize", dataBufferSize)
        .append("inputModules", inputModules)
	.append("processModules", processModules)
        .append("outputModules", outputModules)
        .toString();
  }

}
