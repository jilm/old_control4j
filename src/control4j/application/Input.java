package control4j.application;

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

import control4j.tools.DeclarationReference;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  This is a crate object for the module input definition.
 *
 */
public class Input extends Configurable {

  public Input() { }

  private int pointer = -1;

  public void setPointer(int pointer) {
    this.pointer = pointer;
  }

  public int getPointer() {
    return pointer;
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("pointer", pointer);
  }

  private Signal signal;

  void setSignal(Signal signal) {
    this.signal = signal;
  }

  public Signal getSignal() {
    return signal;
  }

}
