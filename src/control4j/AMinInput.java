package control4j;

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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Used to indicate that the annotated module class requires
 *  at least as may input signals. This annotation is used by
 *  some implicite Module class methods to check module IO
 *  interconnection.
 *
 *  <p>Such obligatory input terminals must have input array
 *  indexes start from zero to some n. Than it is guaranteed
 *  that on the first n-1 input array indexes will not be
 *  <code>null</code> value.
 *
 *  <p>For example, if there is a module, say the comparator
 *  which requires exactly two input signals, a referece and
 *  a compared signal, than this annotation, together with the
 *  <code>AMaxInput</code> annotation  may be used to say that
 *  exactly two signals must be connected to the first two
 *  terminals of the comparator (indexes 0 and 1).
 *
 *  @see Module
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AMinInput
{

  /**
   *  Specify how many input signals must be, at least, connected
   *  to some module. These obligatory input terminals have indexes
   *  from zero to <code>value</code>. This parameter is optional,
   *  default value is zero.
   */
  int value() default 0;

}
