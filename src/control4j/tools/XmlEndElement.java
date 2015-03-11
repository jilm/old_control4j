package control4j.tools;

/*
 *  Copyright 2013, 2014 Jiri Lidinsky
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
public @interface XmlEndElement
{

  /**
   *  Local name of the parent of the element for which annotated method
   *  should be called.
   */
  String parent() default "";

  /**
   *  Local name of the end element for which annotated method should be
   *  called.
   */
  String localName();

  String namespace() default "";

  String parentNamespace() default "";

}
