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

import static org.apache.commons.lang3.StringUtils.isBlank;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import java.text.MessageFormat;

/**
 *
 *  Represents one error.
 *
 */
public class ErrorRecord {

  /**
   *  An empty constructor.
   */
  public ErrorRecord() {}

  //---------------------------------------------------------------- Modifiers.

  private Phase phase;

  public ErrorRecord setPhase(Phase phase) {
    this.phase = phase;
    return this;
  }

  private Throwable cause;

  public ErrorRecord setCause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  //------------------------------------------------------------ Print Message.

  private StringBuilder sb;

  @Override
  public String toString() {

    sb = new StringBuilder();

    switch (phase) {

      // problem during block expansion
      case BLOCK_EXPANSION:
        switch (getCauseCode()) {
          // block definition missing
          case NO_SUCH_ELEMENT:
            sb.append("Block definition is missing!");
            break;
          // cycle definition detected
          case CYCLIC_DEFINITION:
            sb.append("There is cyclic dependency between blocks!");
            break;
          // other
          default:
            return defaultMessage();
        }
        break;

      // problem during module input resolving
      case INPUT_RESOLVING:
        switch (getCauseCode()) {
          case NO_SUCH_ELEMENT:
            sb.append("Signal declaration for some module input is missing!\n");
            append("Signal name: {0}\n", "name");
            append("Signal scope: {0}\n", "scope");
            break;
          default:
            return defaultMessage();
        }

      // problem during module instantiation
      case MODULE_INSTANTIATION:
        System.out.println(cause.toString());
        switch (getCauseCode()) {
          // There is no implementation of some module
          case CLASS_NOT_FOUND:
            System.out.println("class not found");
            break;
          // other
          default:
            System.out.println("other");
            break;
        }
        break;

      default:
        return defaultMessage();
    }
    return sb.toString();
  }

  //------------------------------------------------------------------ Private.

  private ExceptionCode getCauseCode() {
    return CommonException.getCode(cause);
  }

  private String defaultMessage() {
    if (cause == null) {
      return "Unspecified error was detected!";
    } else {
      return cause.getMessage();
    }
  }

  private void append(String text) {
    if (text != null) {
      sb.append(text);
    }
  }

  private void append(String text, String key) {
    if (text != null && key != null) {
      sb.append(MessageFormat.format(text, getValue(key)));
    }
  }

  private String getValue(String key) {
    if (cause != null && key != null) {
      if (cause instanceof CommonException) {
        return ((CommonException)cause).get(key);
      } else if ("message".equals(key)) {
        return cause.getMessage();
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

}
