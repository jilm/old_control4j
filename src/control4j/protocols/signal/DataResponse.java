package control4j.protocols.signal;

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
import java.util.Set;

import control4j.Signal;
import control4j.protocols.IResponse;

public class DataResponse extends Response
{

  protected HashMap<String, Signal> data = new HashMap<String, Signal>();

  public DataResponse()
  {
  }

  public boolean isRequest()
  {
    return false;
  }

  public void put(String id, Signal signal)
  {
    data.put(id, signal);
  }

  public Signal get(String id)
  {
    return data.get(id);
  }

  public Set<String> getIdSet()
  {
    return data.keySet();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getName());
    sb.append(data.toString());
    return sb.toString();
  }

  public boolean isFinished()
  {
    return false;
  }

}
