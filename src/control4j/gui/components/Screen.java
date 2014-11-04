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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import control4j.scanner.Getter; 
import control4j.scanner.Setter;

/**
 *
 */
public class Screen extends JPanel
implements Cloneable, control4j.gui.IComponentName
{

  private String name;

  public Screen()
  {
    super(null);
  }

  @Getter(key="Name")
  public String getName()
  {
    if (name != null && name.length() > 0)
      return name;
    else
      return getClass().getSimpleName() + String.valueOf(getIndex() + 1);
  }

  @Setter(key="Name")
  public void setName(String name)
  {
    this.name = name;
  }

  @Getter(key="title")
  public String getTitle()
  {
    int index = getIndex();
    if (index >= 0)
    {
      JTabbedPane parent = (JTabbedPane)getParent();
      return parent.getTitleAt(index);
    }
    return "";
  }

  @Setter(key="title")
  public void setTitle(String title)
  {
    int index = getIndex();
    if (index >= 0)
    {
      JTabbedPane parent = (JTabbedPane)getParent();
      parent.setTitleAt(index, title);
    }
  }

  @Getter(key="background-color")
  @Override
  public Color getBackground()
  {
    return super.getBackground();
  }

  @Override
  @Setter(key="background-color")
  public void setBackground(Color color)
  {
    super.setBackground(color);
  }

  private int getIndex()
  {
    JTabbedPane parent = (JTabbedPane)getParent();
    for (int i=0; i<parent.getTabCount(); i++)
      if (parent.getComponentAt(i) == this)
        return i;
    return -1;
  }

  @Override
  public void doLayout()
  {
    super.doLayout();
    for (int i=0; i<getComponentCount(); i++)
    {
      Component component = getComponent(i);
      Dimension size = component.getPreferredSize();
      if (size != null) component.setSize(size);
    }
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    Screen clone = (Screen)super.clone();
    clone.removeAll();
    for (int i=0; i<getComponentCount(); i++)
    {
      Component child = getComponent(i);
      Component childClone = null;
      if (child instanceof AbstractComponent)
        childClone = (Component)((AbstractComponent)child).clone();
      else if (child instanceof AbstractPanel)
        childClone = (Component)((AbstractPanel)child).clone();
      else
        assert false;
      clone.add(childClone);
    }
    return clone;
  }

}
