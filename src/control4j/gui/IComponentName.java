package control4j.gui;

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

/**
 *
 *  This interface should be implemented by all of the GUI components
 *  which will be used by the Editor. It is used for navigation purposes.
 *
 *  @see control4j.gui.edit.Editor
 *
 */
public interface IComponentName
{

  /**
   *  Returns a name of the component. Returned string is used by the
   *  editor for navigation purposes. The name should be unique and 
   *  should not be an empty string. It may not return null.
   */
  String getName();

}
