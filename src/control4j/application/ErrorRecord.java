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

import static org.apache.commons.lang3.StringUtils.isBlank;
import control4j.ExceptionCode;
import control4j.SyntaxErrorException;

public class ErrorRecord {

  private ErrorCode code;

  public ErrorRecord setCode(ErrorCode code) {
    this.code = code;
    return this;
  }

  private Throwable cause;

  public ErrorRecord setCause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    switch (code) {

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

          default:
            return defaultMessage();
        }
        break;

      // problem during module instantiation
      case MODULE_INSTANTIATION:
        switch (getCauseCode()) {
          // There is no implementation of some module
          case CLASS_NOT_FOUND:
            break;
        }
        break;

      default:
        return defaultMessage();
    }
    return sb.toString();
  }

  private ExceptionCode getCauseCode() {
    if (cause == null) {
      return ExceptionCode.NOT_SPECIFIED;
    } else if (cause instanceof SyntaxErrorException) {
      return ((SyntaxErrorException)cause).getCode();
    } else {
      // TODO:
      return ExceptionCode.NOT_SPECIFIED;
    }
  }

  private String defaultMessage() {
    if (cause == null) {
      return "Unspecified error was detected!";
    } else {
      return cause.getMessage();
    }
  }

}
