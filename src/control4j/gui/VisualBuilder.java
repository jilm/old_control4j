package control4j.gui;

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

import static cz.lidinsky.tools.Validate.notNull;

import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.tree.Node;

import javax.swing.JComponent;

public abstract class VisualBuilder {

  public static JComponent createVisualComponent(Node<GuiObject> node) {
    VisualObject vo = getVO(notNull(node));
    JComponent component = vo.getVisualComponent();
    if (component == null) {
      component = vo.createVisualComponent();
      return component;
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message", "The visual component already exists!")
        .set("object", vo);
    }
  }

  public static void configureVisualComponent(Node<GuiObject> node) {
    VisualObject vo = getVO(notNull(node));
    JComponent component = vo.getVisualComponent();
    if (component == null) {
      throw new CommonException()
        .setCode(ExceptionCode.ILLEGAL_STATE)
        .set("message",
            "Cannot configure visual component which has not been created yet!")
        .set("object", vo);
    } else {
      vo.configureVisualComponent();
      for (Node<GuiObject> child : node.getChildren()) {
        try {
          component.add(createVisualComponent(child));
          configureVisualComponent(child);
        } catch (Exception e) {
          // this is OK, component is just not visual
        }
      }
    }
  }

  public static VisualObject getVO(Node<GuiObject> node) {
    GuiObject go = node.getDecorated();
    if (go instanceof VisualObject) {
      return (VisualObject)go;
    } else {
      throw new CommonException()
        .setCode(ExceptionCode.CLASS_CAST)
        .set("message", "Given object cannot be cast to VisualObject!")
        .set("object", go);
    }
  }

}
