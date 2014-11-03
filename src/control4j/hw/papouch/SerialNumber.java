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

public class SerialNumber
{
  public int productNumber;
  public int serialNumber;
  public long other;

  public SerialNumber(int productNumber, int serialNumber, long other)
  {
    this.productNumber = productNumber;
    this.serialNumber = serialNumber;
    this.other = other;
  }

  @Getter(key="product-number")
  public int getProductNumber()
  {
    return productNumber;
  }

  @Getter(key="serial-number")
  public int getSerialNumber()
  {
    return serialNumber;
  }

  @Getter(key="other")
  public long getOther()
  {
    return other;
  }
}

