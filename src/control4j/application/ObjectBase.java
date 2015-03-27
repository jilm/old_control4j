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

/**
 *
 *  It is a common ancestor of all of the application objects.
 *
 */
public abstract class ObjectBase
{

  /**
   *  Appends the content of this object into the sb in the
   *  human readable form. Do not write any identifiers, write
   *  just a content. If the content occupy just one line of
   *  text, just place it to the sb. If it occupy more than
   *  just a line, place the indent parameter in front of 
   *  the second and each subsequent line. Place a new line
   *  symbol behind the last line.
   *
   *  @param sb
   *             to place the result
   */
  abstract void toString(String indent, StringBuilder sb);
  
}
