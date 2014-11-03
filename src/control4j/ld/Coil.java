package control4j.ld;

/*
 *  Copyright 2013 Jiri Lidinsky
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

/**
 *
 */
public class Coil
{
  private String type = "OTE";
  private String name;

  public Coil(String name)
  {
    this.name = name;
  }

  public Coil(String type, String name)
  {
    this.name = name;
    if (type != null && type.length() > 0)
      this.type = type;
  }

  public String getName()
  {
    return this.name;
  }

  public String getType()
  {
    return this.type;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setType(String type)
  {
    this.type = type;
  }
}
