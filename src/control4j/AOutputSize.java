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
 *  Used to indicate that the annotated module class has so
 *  many input terminals and no more. This annotation is used
 *  by some implicite Module class methods to check module IO
 *  interconnection.
 *
 *  <p>In contrast to the AMinInput, this annotation defines
 *  terminals that may be used, but need not to be. This annotation
 *  may be used together with AMinInput. So, if both AMinInput(m)
 *  and AMaxInput(n) is used than: m must be less or eaqual to n,
 *  terminals from zero to m (inclusive) must be connected and
 *  terminals with indexes from m to n (inclusive) may be connected.
 *
 *  @see Module
 *  @see AMinInput
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AOutputSize
{

  /**
   *  Specify how many input signals may be connected
   *  to some module. These optional input terminals have indexes
   *  that start after the obligatory input block and ends on the
   *  one specified by this <code>value</code>.
   *  This parameter is optional and default value is
   *  <code>Integer.MAX_VALUE</code>.
   */
  int value() default Integer.MAX_VALUE;

}
