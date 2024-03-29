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

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static control4j.tools.LogMessages.getMessage;

import java.util.ArrayList;
import java.util.Collection;
//import org.xml.sax.Attributes;

import control4j.application.Scope;
import control4j.tools.ParseException;

import cz.lidinsky.tools.ToStringBuilder;

/**
 *
 *  Represents a use element.
 *
 */
public class Use extends Configurable implements IReference {

  public Use() {}

  private String href;

  public String getHref() {
    //check();
    return href;
  }

  public void setHref(String href) {
      this.href = href;
  }

  private int scope;

  public int getScope() {
    //check();
    return scope;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  private ArrayList<Input> input;

  void add(Input input) {
    notNull(input, getMessage("msg006", "input",
        getDeclarationReferenceText()));
    if (this.input == null) {
      this.input = new ArrayList<Input>();
    }
    this.input.add(input);
  }

  public Collection<Input> getInput() {
    return unmodifiableCollection(emptyIfNull(input));
  }

  private ArrayList<Output> output;

  void add(Output output) {
    notNull(output, getMessage("msg006", "output",
        getDeclarationReferenceText()));
    if (this.output == null) {
      this.output = new ArrayList<Output>();
    }
    this.output.add(output);
  }

  public Collection<Output> getOutput() {
    return unmodifiableCollection(emptyIfNull(output));
  }

  protected void check() {
    if (href == null) {
      throw new IllegalStateException(getMessage("msg002", "href",
          getDeclarationReferenceText()));
    }
  }

  @Override
  public void toString(ToStringBuilder builder) {
    super.toString(builder);
    builder.append("href", href)
        .append("scope", scope)
        .append("input", input)
        .append("output", output);
  }

}
