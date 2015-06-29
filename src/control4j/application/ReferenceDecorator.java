package control4j.application;

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

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ReferenceDecorator<S, T> {

  String href;
  Scope scope;
  T value;
  protected S decorated;

  public ReferenceDecorator(String href, Scope scope, T value, S decorated) {
    this.href = trim(notBlank(href));
    this.scope = notNull(scope);
    this.value = value;
    this.decorated = notNull(decorated);
  }

  public S getDecorated() {
    return decorated;
  }

  public String getHref() {
    return href;
  }

  public Scope getScope() {
    return scope;
  }

  public T getValue() {
    return value;
  }

}
