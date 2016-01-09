/*
 *  Copyright 2013, 2014, 2016 Jiri Lidinsky
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

package control4j.tools;

/**
 *  Serves as a reference to the point, where referenced object was declared.
 *  To some line in the configuration file for example. It should return
 *  information in the human readable form. This information is used in a log
 *  message or in the error report.
 *
 *  <p>Reference information may and should be hierarchical.
 *  The example of output:<br>
 *  This is the module object, class: PMAnd,<br>
 *  on line: 25, column: 6,<br>
 *  in the file: heating.xml,<br>
 *  project: project1<br>
 *
 *  <p>To create object which particularize some existing reference, call method
 *  which starts with "specify" word.
 *
 *  <p>This object is immutable.
 */
public class DeclarationReference
{

  /**
   *  Point to the reference object which is more general.
   */
  private DeclarationReference parent;

  /**
   *  The text.
   */
  private String reference;

  /**
   *  Creates an empty declaration reference.
   */
  @Deprecated
  public DeclarationReference()
  {
    parent = null;
  }

  @Deprecated
  public DeclarationReference(DeclarationReference parent)
  {
    this.parent = parent;
  }

  public DeclarationReference(String text)
  {
    this.reference = text;
    this.parent = null;
  }

  private DeclarationReference(String text, DeclarationReference parent) {
      this.reference = text;
      this.parent = parent;
  }

  @Deprecated
  public void setProject(String projectName)
  {
    reference = "in the project: " + projectName;
  }

  /**
   *  Creates and returns a reference object to the project with given name.
   *
   *  @param projectName
   *             the name of the project
   *
   *  @return a reference object
   */
  public static DeclarationReference getProjectRef(String projectName) {
      return new DeclarationReference("In the project: " + projectName);
  }

  public DeclarationReference specifyProject(String projectName) {
      return new DeclarationReference("in the project: " + projectName, this);
  }

  @Deprecated
  public void setFile(String fileName)
  {
    reference = "in the file: " + fileName;
  }

  public static DeclarationReference getFileRef(String fileName) {
      return new DeclarationReference("In the file: " + fileName);
  }

  public DeclarationReference specifyFile(String fileName) {
      return new DeclarationReference("in the file: " + fileName, this);
  }

  @Deprecated
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

    public DeclarationReference specify(String text) {
        return new DeclarationReference(text, this);
    }

}
