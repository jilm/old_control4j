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
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JPanel;
import cz.lidinsky.tools.reflect.Getter;
import cz.lidinsky.tools.reflect.Setter;
import control4j.gui.VisualContainer;

public class PanelPainter extends JPanel
{

  public PanelPainter()
  {
    super(null);
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

}

