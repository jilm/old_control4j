package control4j.gui.components;

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

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import control4j.scanner.Setter;
import control4j.scanner.Getter;

/**
 *  Panel that uses Flow layout.
 */
public class Box extends AbstractPanel
{

  private int alignmentPoint = 0;

  /**
   *
   */
  private static int counter;

  /**
   *
   */
  private final int number = ++counter;

  public Box()
  {
    super();
    setLayout(new FlowLayout());
  }

  /**
   *
   */
  @Override
  protected int getCounter()
  {
    return number;
  }

  @Setter(key="Alignment Point")
  public void setAlignmentPoint(int index)
  {
    alignmentPoint = index;
  }

  @Getter(key="Alignment Point")
  public int getAlignmentPoint()
  {
    return alignmentPoint;
  }

}
