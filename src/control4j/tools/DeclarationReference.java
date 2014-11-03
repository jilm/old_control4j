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
 *  Serves as a reference to some point, where somethig was declared. 
 *  To some line in the configuration file for example. It should 
 *  return information in the human readable form. This information 
 *  is used for example in logs or if it is necessary to report some 
 *  error or fault.
 *
 *  <p>Reference information may and should be hierarchical, the 
 *  example of output:<br>
 *  This is the module object, class: PMAnd,<br>
 *  on line: 25, column: 6,<br>
 *  in the file: heating.xml,<br>
 *  project: project1<br>
 *
 *  <p>There are two kinds of methods to manipulate the content of the
 *  object. Methods which names begining word "set", set the content
 *  of this object. But if you have an object which points to the project
 *  and you want to create reference to the file within this project,
 *  use method which name begins to "add" on the project ref object. 
 *  This method creates and returnes new object and sets the project
 *  ref object as a parent.
 *
 */
public class DeclarationReference
{

  /**
   *
   */
  private DeclarationReference parent;

  /**
   *
   */
  private String reference;

  /**
   *  Creates an empty declaration reference.
   */
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
    reference = "in the project: " + projectName;
  }

  public void setFile(String fileName)
  {
    reference = "in the file: " + fileName;
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

  public void setParent(DeclarationReference parent)
  {
    this.parent = parent;
  }

  /**
   *  Returns the declaration reference as a string. The returned string
   *  contains not only content of this object but also the whole chain
   *  of objects. Declaration ref objects are separated by the colon and
   *  line end. Content of this object is on the firs line, parent on the
   *  second, and so on.
   *
   *  @return a string representation
   */
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
