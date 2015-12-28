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

package control4j.protocols.signal;

import static cz.lidinsky.tools.Validate.notNull;

import control4j.Signal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataResponse extends Message {

  private final Map<String, Signal> data;

  public DataResponse(Map<String, Signal> data) {
    this.data = Collections.unmodifiableMap(new HashMap<>(notNull(data)));
  }

  public DataResponse() {
    this.data = Collections.emptyMap();
  }

  public boolean isRequest() {
    return false;
  }

  public Map<String, Signal> getData() {
    return data;
  }

}
