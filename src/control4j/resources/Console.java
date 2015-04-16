package control4j.resources;

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

import control4j.IConfigBuffer;
import control4j.Resource;

public class Console extends Resource 
implements ITextWriter
{

  public Console() { }

  public void print(String text)
  {
    System.out.print(text);
  }

  public void println(String text)
  {
    System.out.println(text);
  }

  @Override
  public boolean satisfies(IConfigBuffer configuration)
  {
    return true;
  }

  @Override
  public void initialize(IConfigBuffer configuration)
  {
  }

}
