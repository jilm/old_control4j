package control4j;

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

import cz.lidinsky.tools.BaseException;

public class SyntaxErrorException extends BaseException {

  public SyntaxErrorException() {
    super();
  }

  private ExceptionCode code = ExceptionCode.NOT_SPECIFIED;

  public SyntaxErrorException setCode(ExceptionCode code) {
    this.code = code;
    return this;
  }

  public ExceptionCode getCode() {
    return code;
  }

  @Deprecated
  public SyntaxErrorException(String message) {
    set("message", message);
  }

  @Deprecated
  public SyntaxErrorException(String message, Throwable cause) {
    set("message", message);
    setCause(cause);
  }

  @Deprecated
  public SyntaxErrorException(Throwable cause) {
    setCause(cause);
  }
}
