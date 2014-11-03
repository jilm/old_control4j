package control4j.ld.text;

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

import java.util.List;
import java.util.ArrayList;

/**
 *  
 */
abstract class RendererBase
{

  static final Library library = Library.getInstance();

  /** Ascii representation of the contact block */
  ArrayList<StringBuilder> ascii;

  /** True means that there is a connector on the line */
  ArrayList<Boolean> leftConnectors;

  /** True means that there is a connector on the line */
  ArrayList<Boolean> rightConnectors;

  int getMaxLength()
  {
    int length = 0;
    for (int i=0; i<ascii.size(); i++)
      length = Math.max(length, ascii.get(i).length());
    return length;
  }

  /**
   *  Creates an empty renderer base.
   */
  RendererBase()
  {
    ascii = new ArrayList<StringBuilder>();
    leftConnectors = new ArrayList<Boolean>();
    rightConnectors = new ArrayList<Boolean>();
  }

  /**
   *  Uses argument as a base for further processing. It doesn't
   *  provide copy of the argument!
   */
  RendererBase(RendererBase base)
  {
    ascii = base.ascii;
    leftConnectors = base.leftConnectors;
    rightConnectors = base.rightConnectors;
  }

  /**
   *  Copy argument into internal properties of this object.
   */
  void copy(RendererBase block)
  {
    for (int i=0; i<block.ascii.size(); i++)
      ascii.add(new StringBuilder(block.ascii.get(i)));
    leftConnectors.addAll(block.leftConnectors);
    rightConnectors.addAll(block.rightConnectors);
  }

  /**
   *  Fill line with index index on the left and right side, such
   *  that the original content is center aligned and the whole
   *  line has width width.
   */
  void alignCenter(int index, int width)
  {
    StringBuilder line = ascii.get(index);
    char leftFillChar = leftConnectors.get(index).booleanValue() ? '-' : ' ';
    char rightFillChar = rightConnectors.get(index).booleanValue() ? '-' : ' ';
    while (line.length() < width-1)
    {
      line.insert(0, leftFillChar);
      line.append(rightFillChar);
    }
    if (line.length() < width)
      line.append(rightFillChar);
  }

  void alignCenter(int width)
  {
    for (int i=0; i<ascii.size(); i++)
      width = Math.max(width, ascii.get(i).length());
    for (int i=0; i<ascii.size(); i++)
      alignCenter(i, width);
  }

  /**
   *  Fill each line with blanc characters such that all of the lines
   *  has the same length.
   */
  void alignRight()
  {
    int length = 0;
    for (int i=0; i<ascii.size(); i++)
      length = Math.max(length, ascii.get(i).length());
    for (int i=0; i<ascii.size(); i++)
    {
      char fillChar = rightConnectors.get(i).booleanValue() ? '-' : ' ';
      while (ascii.get(i).length() < length)
        ascii.get(i).append(fillChar);
    }
  }

  /**
   *  It adds characters on the right side of the ascii with index index,
   *  such that the whole line has length width.
   */
  void fillRight(int index, int width)
  {
    StringBuilder line = ascii.get(index);
    char fillChar = rightConnectors.get(index).booleanValue() ? '-' : ' ';
    while (line.length() < width)
      line.append(fillChar);
  }

  /**
   *  It insert number characters to the left side of ascii with index index.
   */
  void fillLeft(int index, int number)
  {
    StringBuilder line = ascii.get(index);
    char fillChar = leftConnectors.get(index).booleanValue() ? '-' : ' ';
    for (int i=0; i<number; i++)
      line.insert(0, fillChar);
  }

  void addBar()
  {
    int begin = 0;
    while (!rightConnectors.get(begin).booleanValue()) begin++;
    int end = ascii.size() -1;
    while (!rightConnectors.get(end).booleanValue()) end--;
    char barChar = ' ';
    if (begin != end)
    {
      alignRight();
      for (int i=0; i<ascii.size(); i++)
      {
        if (i<begin) barChar = ' ';
	else if (i>end) barChar =' ';
	else if (rightConnectors.get(i).booleanValue()) barChar = '+';
        else barChar = '|';
        ascii.get(i).append(barChar);
      }
    }
    else
    {
      ascii.get(0).append(' ');
      ascii.get(1).append('-');
    }
  }

  void addBar(List<Boolean> connectors)
  {
    int size = Math.max(rightConnectors.size(), connectors.size());
    boolean[] unitConnectors = new boolean[size];
    for (int i=0; i<rightConnectors.size(); i++) 
      unitConnectors[i] = rightConnectors.get(i).booleanValue();
    for (int i=0; i<connectors.size(); i++)
      unitConnectors[i] = unitConnectors[i] || connectors.get(i).booleanValue();
    int begin = 0;
    while (!unitConnectors[begin]) begin++;
    int end = size-1;
    while (!unitConnectors[end]) end--;
    char barChar;
    if (end != begin)
    {
      alignRight();
      for (int i=0; i<size; i++)
      {
        if (i<begin) barChar = ' ';
	else if (i>end) barChar = ' ';
	else if (unitConnectors[i]) barChar = '+';
	else barChar = '|';
	ascii.get(i).append(barChar);
      }
    }
  }

  void ensureCapacity(int capacity)
  {
    while (ascii.size() < capacity)
    {
      ascii.add(new StringBuilder());
      leftConnectors.add(Boolean.valueOf(false));
      rightConnectors.add(Boolean.valueOf(false));
    }
  }

  /**
   *  For debug purpouses. Prints ascii buffer into the standard output.
   */
  void print()
  {
    for (int i=0; i<ascii.size(); i++)
      System.out.println(ascii.get(i).toString());
  }

}
