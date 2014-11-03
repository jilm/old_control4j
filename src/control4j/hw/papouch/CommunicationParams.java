package control4j.hw.papouch;

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

import control4j.scanner.Getter;

public class CommunicationParams
{
  private int address;
  private int speed;

  public CommunicationParams(int address, int speed)
  {
    this.address = address;
    this.speed = speed;
  }

  @Getter(key="address")
  public int getAddress()
  {
    return address;
  }

  @Getter(key="speed")
  public int getSpeed()
  {
    return speed;
  }
}

