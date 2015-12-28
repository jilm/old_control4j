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

import static cz.lidinsky.tools.Validate.notEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  A request for new data. This object is immutable. It contains a list of
 *  requested signal identifiers.
 */
public class DataRequest extends Message {

  /**
   *  A set of ids of requested signals.
   */
  protected final Set<String> ids;

  /**
   *  @param ids
   *             a set of ids of requested signals
   *
   *  @throws cz.lidinsky.tools.CommonException
   *             if the parametr is null or empty set
   */
  public DataRequest(Collection<String> ids) {
    this.ids = Collections.unmodifiableSet(new HashSet<>(notEmpty(ids)));
  }

  public DataRequest() {
    this.ids = Collections.emptySet();
  }

  public boolean isRequest() {
    return true;
  }

  public Set<String> getIds() {
    return ids;
  }
}
