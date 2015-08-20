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

package control4j.gui.swing;

/**
 *
 *  A component which purpose is to display a grid of signals.
 *
 */
public class GridUtils {

  /**
   *  Returns number of rows of the grid with given index and total number of
   *  grid cells.
   *
   *  @param grid
   *             a zero based index into the set of all possible rows / columns
   *             variants
   *
   *  @param count
   *             total number of required cells inside the grid
   */
  public static int rows(int grid, int count) {
    if (count == 0) return 0;
    int grids = gridAlternatives(count);
    if (grid <= grids / 2) {
      return grid + 1;
    } else {
      return divide(count, grids - grid);
    }
  }

  public static int cols(int grid, int count) {
    if (count == 0) return 0;
    int grids = gridAlternatives(count);
    if (grid <= grids / 2) {
      return divide(count, grid + 1);
    } else {
      return grids - grid;
    }
  }

  /**
   *  Returns number of all of the posible combinations of numbers of rows and
   *  columns for a given number of grid cells.
   */
  public static int gridAlternatives(int count) {
    if (count == 0) return 0;
    int result = (int)Math.floor(Math.sqrt(count) * 2.0f);
    if (result * result / 4 - count == 0) {
      return result - 1;
    } else {
      return result;
    }
  }

  /**
   *  Divide given integer numbers and return result runded up to the integer
   *  number. It is used to calculate number of colums from number of rows and
   *  the total number of the cells in the grid.
   */
  protected static int divide(int n, int d) {
    return n / d + ((n % d) > 0 ? 1 : 0);
  }

}
