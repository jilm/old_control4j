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

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.trim;

import java.util.ArrayList;
import org.xml.sax.Attributes;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents a tag of the signal.
 *
 */
public class Tag extends Configurable
{

  public Tag() {}

  private String name;

  /**
   *  Returns the value of the property.
   */
  public String getName() {
    check();
    return name;
  }

  Tag setName(String name) {
    this.name = trim(notBlank(name, "The name property may not be blank!\n"
        + getDeclarationReferenceText()));
    return this;
  }

  @Override
  public void toString(ToStringBuilder builder)
  {
    super.toString(builder);
    builder.append("name", name);
  }

  protected void check() {
    if (name == null) {
      throw new IllegalStateException("The name property may not be null!\n"
          + getDeclarationReferenceText());
    }
  }

}
