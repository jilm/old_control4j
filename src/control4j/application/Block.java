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

import java.util.Collection;

public abstract class Block extends DeclarationBase {

  /** Does nothing. */
  public Block() {
    super();
  }

  /**
   *  Expands inner elements of the block (which is modules, signals, inner
   *  block references) into the given handler.
   *
   *  @param use
   *             reference to this block. It is used to resolve block input
   *             and output
   *
   *  @param handler
   *             an object to send epanded modules, signals, etc.
   */
  public abstract void expand(Use use, Preprocessor handler);

}
