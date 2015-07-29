package control4j.gui.components;

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

import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JComponent;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;

/**
 *
 *  A component dedicated to show a line of text.
 *
 */
@control4j.annotations.AGuiObject(name="Label")
public class Label extends VisualObjectBase
{

  /** font size */
  private float fontSize = 12;

  /** The text that will be displayed */
  protected String text = "";

  @Setter("Text")
  public void setText(String text)
  {
    if (text == null)
      this.text = "";
    else
      this.text = text;
    if (component != null)
      ((JLabel)component).setText(this.text);
  }

  @Getter("Text")
  public String getText()
  {
    return text;
  }

  @Setter("Font Size")
  public void setFontSize(double size)
  {
    fontSize = (float)size;
    if (component != null)
    {
      Font font = component.getFont();
      component.setFont(font.deriveFont(fontSize));
    }
  }

  @Getter("Font Size")
  public double getFontSize()
  {
    return fontSize;
  }

  @Getter("Width")
  public int getWidth()
  {
    if (component != null)
      return component.getWidth();
    else
      return 0;
  }

  @Getter("Height")
  public int getHeight()
  {
    if (component != null)
      return component.getHeight();
    else
      return 0;
  }

  @Override
  protected JComponent createSwingComponent()
  {
    return new JLabel();
  }

  @Override
  public void configureVisualComponent()
  {
    super.configureVisualComponent();
    ((JLabel)component).setText(text);
    Font font = component.getFont();
    component.setFont(font.deriveFont(fontSize));
  }

}
