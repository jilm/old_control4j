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

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.ToStringBuilder;

/**
 *  Contains a module output definition. The output may be connected (bind with
 *  some signal) or it may be disconnected.  Each module output, which is
 *  connected, is bind to exactly one signal.  The signal is unambiguously
 *  identified by the <em>pointer</em> number.
 */
public class Output extends Configurable {

  /**
   *  An empty constructor.
   */
  public Output() {}

  //------------------------------------------------------------------ Pointer.

  private int pointer = -1;

  /**
   *  Sets the pointer, which is identification of the signal. The pointer is
   *  set by the <code>Preprocessor</code>.
   */
  void setPointer(int pointer) {
    this.pointer = pointer;
  }

  /**
   *  Returns the pointer, which is identification of the signal to which this
   *  output is connected to.
   *
   *  @throws CommonException
   *             with code <code>ILLEGAL_STATE</code>;
   *             if this output is not connected to any signal
   */
  public int getPointer() {
    return pointer;
    // TODO:  throw exception if disconnected
  }

  /**
   *  Returns true if and only if this output is connected to some signal.
   */
  public boolean isConnected() {
    return pointer >= 0;
  }

  //------------------------------------------------------------------- Signal.

  private Signal signal;

  /**
   *  Sets the signal to which this output is bind to. This method is called by
   *  the Preprocessor.
   */
  void setSignal(Signal signal) {
    this.signal = signal;
  }

  /**
   *  Returns the signal with which this output is bind to.
   *
   *  @throws CommonException
   *             with code <code>ILLEGAL_STATE</code>; if this output is not
   *             connected
   */
  public Signal getSignal() {
    if (isConnected()) {
      return signal;
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "The output is not connected!")
        .set("reference", getDeclarationReferenceText());
    }
  }

  //-------------------------------------------------------------------- Other.

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("pointer", pointer);
  }

}
