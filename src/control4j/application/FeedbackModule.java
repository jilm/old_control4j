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

package control4j.application;

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import org.apache.commons.lang3.mutable.MutableObject;

/**
 *  This is a special module definition that is used for breaking the
 *  feedback.
 */
public class FeedbackModule extends Module {

  public static final String INPUT_CLASSNAME
    = "control4j.modules.system.IMBreakFeedback";

  public static final String OUTPUT_CLASSNAME
    = "control4j.modules.system.OMBreakFeedback";

  private MutableObject sharedSignal;

  public FeedbackModule(String className, MutableObject sharedSignal) {
    super(className);
    this.sharedSignal = sharedSignal;
  }

  public MutableObject getSharedSignal() {
    return sharedSignal;
  }

}
