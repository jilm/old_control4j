package control4j;

/*
 *  Copyright 2013 Jiri Lidinsky
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

/**
 *  It is thrown when some important configuration
 *  was not specified.
 */
public class ConfigItemNotFoundException extends Exception
{
  /**
   *  Use when global application configuration item with key
   *  was not specified.
   */
  public ConfigItemNotFoundException(String key)
  {
    super("Global configuration item with key: " + key + " is missing!");
  }
}
