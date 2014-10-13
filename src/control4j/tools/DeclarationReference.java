package control4j.tools;

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

/**
 *
 *  Serves as an reference to some point in the file for examle where
 *  somethig was declared. It should return this information in the
 *  human readable form. This is used for example in logs or if it
 *  is necessary to report some error or fault.
 *
 *  Reference information may be hierarchical, the example of output:
 *  project: project1
 *  file: heating.xml
 *  line: 25, column: 6
 *
 */
public class DeclarationReference
{

  private DeclarationReference parent;

  private String reference;

  public DeclarationReference()
  {
    parent = null;
  }

  public DeclarationReference(DeclarationReference parent)
  {
    this.parent = parent;
  }

  public DeclarationReference(String text)
  {
    setText(text);
  }

  public void setProject(String projectName)
  {
    reference = "in project: " + projectName;
  }

  public void setFile(String fileName)
  {
    reference = "in file: " + fileName;
  }

  public DeclarationReference addFile(String filename)
  {
    DeclarationReference ref = new DeclarationReference(this);
    ref.setFile(filename);
    return ref;
  }

  public void setLineColumn(int line, int column)
  {
    reference = String.format("on line: %d; column: %d", line, column);
  }

  public DeclarationReference addLineColumn(int line, int column)
  {
    DeclarationReference ref = new DeclarationReference(this);
    ref.setLineColumn(line, column);
    return ref;
  }

  public void setLine(int line)
  {
    reference = String.format("on line: %d", line);
  }

  public DeclarationReference addLine(int line)
  {
    DeclarationReference ref = new DeclarationReference(this);
    ref.setLine(line);
    return ref;
  }

  public void setText(String text)
  {
    reference = text;
  }

  public DeclarationReference addText(String text)
  {
    DeclarationReference ref = new DeclarationReference(this);
    ref.setText(text);
    return ref;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(reference);
    for (DeclarationReference dr=parent; dr != null; dr = dr.parent)
    {
      sb.append(',');
      sb.append('\n');
      sb.append(dr.reference);
    }
    return sb.toString();
  }

}
