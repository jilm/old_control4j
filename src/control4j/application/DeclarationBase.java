package control4j.application;

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

import control4j.tools.DeclarationReference;

/**
 * 
 *  A common base for classes that hold data needed for creation
 *  runtime objects like modules or resources.
 *
 *  <p>Declaration that is typicaly content of objects that are
 *  derived from this abstrac class is read from a file. This
 *  class thefore provides common means for keeping information
 *  about a place whre the object come from or where was declared.
 *  This is useful for solving potential problems.
 *
 */
public abstract class DeclarationBase
{

  /**
   *  Contains information about a place where the object was
   *  declared in human readable form. May be null!
   */
  private DeclarationReference declarationReference;

  /**
   *  Sets the declaration reference.
   *
   *  @param reference
   *             declaration reference; may be null
   */
  public void setDeclarationReference(DeclarationReference reference)
  {
    declarationReference = reference;
  }

  /**
   *  Sets the declaration reference.
   *
   *  @param reference
   *             declaration reference, may be null, in such a case
   *             the declaration reference will be empty.
   */
  public final void setDeclarationReference(String reference)
  {
    if (reference != null)
      setDeclarationReference(new DeclarationReference(reference));
    else
      setDeclarationReference((DeclarationReference)null);
  }

  /**
   *  Returns the assigned declaration reference. It may return null.
   *
   *  @return declaration reference or null
   */
  public DeclarationReference getDeclarationReference()
  {
    return declarationReference;
  }

  /**
   *  Returns the text of the declaration reference. If it was not
   *  assigned, it returns an empty string.
   *
   *  @return the text representation of the declaration reference
   */
  public String getDeclarationReferenceText()
  {
    if (declarationReference != null)
      return declarationReference.toString();
    else
      return "";
  }

}
