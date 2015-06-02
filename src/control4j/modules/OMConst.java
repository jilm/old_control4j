package control4j.modules;

/*
 *  Copyright 2013, 2015 Jiri Lidinsky
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

//import org.apache.commons.lang3.builder.ToStringBuilder;
//import org.apache.commons.lang3.builder.ToStringStyle;

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;

import control4j.Signal;
import control4j.OutputModule;
import control4j.ConfigItem;

/**
 *  Output module, which returns one constant value,
 *  constant signal, which doesn't change in time.
 *  Value must be specified in configuration of the
 *  module. Returned signal is always valid and with
 *  timestamp set to the system time.
 */
public class OMConst extends OutputModule implements IToStringBuildable
{
  @ConfigItem
  public double value;

  public void get(Signal[] output, int outputLength)
  {
     output[0] = Signal.getSignal(value);
  }

  @Override
  public String toString()
  {
    return new ToStringBuilder()
        .append(this)
        .toString();
  }

  public void toString(ToStringBuilder builder)
  {
    builder.append("value", value);
  }

}
