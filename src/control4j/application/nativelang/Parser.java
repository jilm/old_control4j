package control4j.application.nativelang;

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

class Parser
{

  public static final String GLOBAL_SCOPE_STRING = "global";
  public static final String LOCAL_SCOPE_STRING = "local";
  public static final String PARENT_SCOPE_STRING = "parent";

  public static final int GLOBAL_SCOPE_CODE = 0;
  public static final int LOCAL_SCOPE_CODE = 1;
  public static final int PARENT_SCOPE_CODE = 2;

  private Parser()
  {
  }

  public static int parseScope2(String scope)
  {
    if (scope == null) return LOCAL_SCOPE_CODE;
    String trimed = scope.trim();
    if (trimed.length() == 0) return LOCAL_SCOPE_CODE;
    if (GLOBAL_SCOPE_STRING.equals(trimed)) return GLOBAL_SCOPE_CODE;
    if (LOCAL_SCOPE_STRING.equals(trimed)) return LOCAL_SCOPE_CODE;
    throw new IllegalArgumentException();
  }
						  
  public static String formatScope(int scope)
  {
    switch (scope)
    {
      case GLOBAL_SCOPE_CODE: return GLOBAL_SCOPE_STRING;
      case LOCAL_SCOPE_CODE: return LOCAL_SCOPE_STRING;
      case PARENT_SCOPE_CODE: return PARENT_SCOPE_STRING;
      default:
        throw new IllegalArgumentException();
    }
  }

}
