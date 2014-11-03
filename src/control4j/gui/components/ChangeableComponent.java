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

import java.util.ArrayList;
import javax.swing.JComponent;
import control4j.gui.changers.IChangeable;
import control4j.gui.changers.Changer;

public class ChangeableComponent extends JComponent implements IChangeable
{

  private ArrayList<Changer> changers = new ArrayList<Changer>();

  public void addChanger(Changer changer)
  {
    changers.add(changer);
    changer.setParent(this);
  }

  public Changer getChanger(int index)
  {
    return changers.get(index);
  }

  public int getChangerCount()
  {
    return changers.size();
  }

  public void removeChanger(Changer changer)
  {
    changers.remove(changer);
  }

  @Override
  protected Object clone() throws CloneNotSupportedException
  {
    ChangeableComponent clone = (ChangeableComponent)super.clone();
    clone.changers = new ArrayList<Changer>();
    for (Changer changer : changers)
      clone.addChanger((Changer)changer.clone());
    return clone;
  }

}
