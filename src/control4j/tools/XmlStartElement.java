package control4j.tools;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

/**
 *
 *  @see SaxReader
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlStartElement
{

  /**
   *  Local name of the parent XML element for which
   *  annotated method should be called. If contains
   *  an empty string, the root element is expected.
   *  May contain *, in such a case the method is called
   *  for elements with given local name and arbitrary
   *  parent. If there is a method where the local name
   *  and parent are specified together with a method
   *  where only local name is specified whereas the
   *  parent may be arbitrary, the more specific method
   *  is chosen to handle the event.
   */
  String parent() default "*";

  /**
   *  Local name of the XML element for which this method
   *  will be called. May contain *; in such a case this
   *  method will be called for arbitrary strart element.
   */
  String localName();

  /**
   *  Required namespace of the element. The possible values
   *  are: a full namespace, '*' which means the namespace
   *  may be arbitrary, '^' that namespace must be the same
   *  as the namespace of the parent, and '' which means empty
   *  namespace.
   */
  String namespace() default "^";

  String parentNamespace() default "*";

}
