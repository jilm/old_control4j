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

public class ErrorRecord {

  public static final int DATATYPE_ERROR = 1;

  /** If the value of some attribute is out of bounds or is not allowed. */
  public static final int WRONG_ATTRIBUTE_VALUE_ERROR = 2;

  public static final int WRONG_SCOPE2_VALUE_ERROR = 3;
  public static final int WRONG_SCOPE3_VALUE_ERROR = 4;

  public static final int DUPLICATE_DEFINITION_ERROR = 5;
  public static final int NAME_CODE = 1;
  public static final int SCOPE_CODE = 2;
  public static final int REFERENCE1_CODE = 3;
  public static final int REFERENCE2_CODE = 4;


  public static final int MESSAGE_CODE = 1;
  public static final int SHOULD_BE_CODE = 2;
  public static final int IS_CODE = 3;
  public static final int WHERE_CODE = 4;
  public static final int WHAT_CODE = 5;
  public static final int POSSIBLE_VALUES_CODE = 6;
  public static final int ATTRIBUTE_CODE = 7;
  public static final int VALUE_CODE = 8;

  private int errorCode;

  private String[] params = new String[9];

  public ErrorRecord set(int code, String value) {
    if (value == null) {
      params[code] = "<null>";
    } else if (isBlank(value)) {
      params[code] = "<blank>";
    } else {
      params[code] = value;
    }
    return this;
  }

  public ErrorRecord set(int errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    switch (errorCode) {
      case WRONG_ATTRIBUTE_VALUE_ERROR:
        if (!isBlank(params[ATTRIBUTE_CODE])) {
          sb.append("Wrong value of the ")
            .append(params[ATTRIBUTE_CODE])
            .append("attribute!");
        } else {
          sb.append("Wrong attribute value!");
        }
        if (!isBlank(params[VALUE_CODE])) {
          sb.append("Founded value: ")
            .append(params[VALUE_CODE]);
        }
        if (!isBlank(params[POSSIBLE_VALUES_CODE])) {
          sb.append("Possible values are: ")
            .append(params[POSSIBLE_VALUES_CODE]);
        }
        if (!isBlank(params[WHERE_CODE])) {
          sb.append("The error was detected here: ")
            .append(params[WHERE_CODE]);
        }
        break;

      case WRONG_SCOPE2_VALUE_ERROR:
        sb.append("Posible values for the scope attribute are: ")
          .append("local, global!");
        if (!isBlank(params[VALUE_CODE])) {
          sb.append("Found value: ")
            .append(params[VALUE_CODE]);
        }
        if (!isBlank(params[WHERE_CODE])) {
          sb.append("The error was detected here: ")
            .append(params[WHERE_CODE]);
        }
        break;

      case WRONG_SCOPE3_VALUE_ERROR:
        sb.append("Posible values for the scope attribute are: ")
          .append("local, parent, global!");
        if (!isBlank(params[VALUE_CODE])) {
          sb.append("Found value: ")
            .append(params[VALUE_CODE]);
        }
        if (!isBlank(params[WHERE_CODE])) {
          sb.append("The error was detected here: ")
            .append(params[WHERE_CODE]);
        }
        break;

      case DUPLICATE_DEFINITION_ERROR:
        sb.append("There are two definitions with the same name and under")
          .append(" the same scope!")
          .append("\nName: ")
          .append(params[NAME_CODE])
          .append("\nScope: ")
          .append(params[SCOPE_CODE])
          .append("\nReference 1: ")
          .append(params[REFERENCE1_CODE])
          .append("\nReference 2: ")
          .append(params[REFERENCE2_CODE]);
        break;

    }
    return sb.toString();
  }

}
