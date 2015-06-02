package control4j.application.nativelang;

/*
 *  Copyright 2013, 2014, 2015 Jiri Lidinsky
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

import cz.lidinsky.tools.IToStringBuildable;
import cz.lidinsky.tools.ToStringBuilder;
import cz.lidinsky.tools.ToStringStyle;

/**
 * 
 *  A common base for classes that hold data about the place where
 *  they were declared or from where they come from.
 *  This class provides common means for keeping such information.
 *  This is useful mainly for reporting potential problems.
 *
 *  <p>In other words, the returned information should be in the
 *  human readable form and the user should be able to unambiquously
 *  identify the referenced object if neccessary.
 *
 *  @see control4j.tools.DeclarationReference
 *
 */
public abstract class DeclarationBase implements IToStringBuildable
{

  /**
   *  Contains information about a place where the object was
   *  declared in the human readable form. May contain null value!
   */
  private DeclarationReference declarationReference;

  /**
   *  Creates an empty object with no reference information.
   */
  public DeclarationBase()
  {
    super();
  }

  /**
   *  Returnes just class name. This method should be overriden
   *  to return more useful information. It should return some
   *  default identification that will be returned in cases where
   *  no other reference was inserted.
   *
   *  @return name of this class
   */
  protected String getDefaultObjectIdentification()
  {
    return this.getClass().getName();
  }

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
   *             declaration reference, may be null
   */
  public final void setDeclarationReference(String reference)
  {
    if (reference != null)
      setDeclarationReference(new DeclarationReference(reference));
    else
      setDeclarationReference((DeclarationReference)null);
  }

  /**
   *  Returns the assigned declaration reference. If the reference
   *  has not been assigned yet, or if the null value was assigned,
   *  it returnes default identification.
   *
   *  @return declaration reference or default identification
   *
   *  @see #getDefaultObjectIdentification
   */
  public DeclarationReference getDeclarationReference()
  {
    if (declarationReference == null)
      declarationReference 
          = new DeclarationReference(getDefaultObjectIdentification());
    return declarationReference;
  }

  /**
   *  Returns the text of the declaration reference which was 
   *  assigned. If it has not been assigned yet, or a null value 
   *  has been assigned, it returns the default identification.
   *
   *  @return the text representation of the declaration reference
   *
   *  @see #getDefaultObjectIdentification
   */
  public String getDeclarationReferenceText()
  {
    if (declarationReference == null)
      return getDefaultObjectIdentification();
    else
      return declarationReference.toString();
  }

  @Override
  public String toString()
  {
    return new ToStringBuilder()
        .append(this)
        .toString();
  }

  public void toString(ToStringBuilder builder)
  {
    builder.append("declarationReference", declarationReference);
  }

}
