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

package control4j.modules.system;

import static cz.lidinsky.tools.Validate.notNull;

import control4j.AMinInput;
import control4j.InputModule;
import control4j.Signal;
import control4j.application.FeedbackModule;
import control4j.application.Module;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.lang3.mutable.MutableObject;

/**
 *  A module which is used to handle feedback. This module is used by the
 *  application sorter to handle feedback inside the application. Do not use it
 *  directly!
 */
@AMinInput(1)
public class IMBreakFeedback extends InputModule {

  /**
   *  A signal which is shared between this object and IMBreakFeedback.
   */
  private MutableObject<Signal> sharedSignal;

  /**
   *  Takes shared signal reference from the definition.
   */
  @Override
  public void initialize(Module definition) {
    super.initialize(definition);
    if (definition instanceof FeedbackModule) {
      sharedSignal = ((FeedbackModule)definition).getSharedSignal();
    } else {
      throw new AssertionError();
    }
  }

  /**
   *  Put shared signal on its output.
   */
  @Override
  public void put(Signal[] input, int inputLength) {
    sharedSignal.setValue(input[0]);
  }

}
