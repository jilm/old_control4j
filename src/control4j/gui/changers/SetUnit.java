package control4j.gui.changers;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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

import control4j.Signal;
import control4j.gui.Changer;

/**
 *
 *  Take a unit form given signal and set the appropriate string property.
 *
 */
@control4j.annotations.AGuiObject(name="Set unit", tags={"text", "unit"})
public class SetUnit extends Changer<String>
{

  @Override
  protected void update(Signal input)
  {
    setProperty(input.getUnit());
  }

  @Override
  public Class getPropertyClass()
  {
    return String.class;
  }

}
