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
 *  An interface which must be implemented by an application crate,
 *  an object that is returned by the application loader, and which
 *  must support a translation into the native application language.
 *
 */
public interface ITranslatable
{

  /**
   *  Translates an application into the native application language
   *  and the result is stored in the given argument.
   *
   *  @param application
   *             an object where the result of tranlastion will be
   *             placed
   *
   *  @throws ...
   *             if it was impossible to perform the translation because
   *             of some error in this object data
   */
  void translate(control4j.application.nativelang.Application application);

}
