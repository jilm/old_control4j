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

/**
 *
 *  Represents a define object which is simply named
 *  value which may be later referenced by some property
 *  object. This object is immutable.
 *
 */
public class Define extends DeclarationBase
{

  protected String name;
  protected String value;
  protected int scope;

  /**
   *  Sets the properties of the object.
   */
  public Define(String name, String value, String scope)
  {
    if (name == null || value == null)
      throw new NullPointerException();
    this.scope = Parser.parseScope2(scope);
    this.name = name;
    this.value = value;
  }

  public String getName()
  {
    return name;
  }

  /**
   *  Returns the value of the property.
   */
  public String getValue()
  {
    return value;
  }

  public String getScope()
  {
    return Parser.formatScope(scope);
  }

  public int getScopeCode()
  {
    return scope;
  }

  @Override
  protected String getDefaultObjectIdentification()
  {
    return java.text.MessageFormat.format(
	"Define object; name: {0}; value: {1}; scope: {2}",
	getName(), getValue(), getScope());
  }

}
