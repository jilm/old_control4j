package control4j.application.gui;

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

import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JComponent;
import control4j.IConfigBuffer;
import control4j.Signal;
import control4j.InputModule;
import control4j.gui.Changer;
import control4j.gui.Screens;
import control4j.Resource;

public class Gui extends Resource
{

  private JFrame mainFrame;

  private Screens gui;

  public void initialize(control4j.application.Resource definition)
  {
    gui = ((GuiResource)definition).getGui();
  }

  @Override
  public void initialize(IConfigBuffer configuration)
  {
  }

  public void setGui(Screens gui)
  {
    this.gui = gui;
  }

  /**
   *  Show gui
   */
  @Override
  public void prepare()
  {
    super.prepare();
    //buffer = new Signal[getNumberOfAssignedInputs()];
    // Create and set up the window
    mainFrame = new JFrame("Top level demo");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JComponent screensComponent = gui.createVisualComponent();
    mainFrame.add(screensComponent);
    gui.configureVisualComponent();
    // Show the main window
    javax.swing.SwingUtilities.invokeLater(
      new Runnable()
      {
        public void run()
	{
	  mainFrame.pack();
	  mainFrame.setVisible(true);
	}
      }
    );
  }

  private class Updater implements Runnable
  {
    private Signal[] buffer;

    Updater(Signal[] signals, int size)
    {
      buffer = new Signal[size];
      System.arraycopy(signals, 0, buffer, 0, size);
    }

    public void run()
    {
    }
  }

  @Override
  public boolean satisfies(IConfigBuffer configuration)
  {
    return true;
  }

}
