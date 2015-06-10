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

public class ErrorRecord {

  public static final int DATATYPE_ERROR = 1;

  public static final int MESSAGE_CODE = 1;
  public static final int SHOULD_BE_CODE = 2;
  public static final int IS_CODE = 3;
  public static final int WHERE_CODE = 4;
  public static final int WHAT_CODE = 5;


  public ErrorRecord set(int code, String value) {
    return this;
  }

  public ErrorRecord set(int errorCode) {
    return this;
  }

}
