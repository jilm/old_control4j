package control4j.modules;

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

import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JComponent;
import control4j.Signal;
import control4j.InputModule;
import control4j.gui.Changer;
import control4j.gui.Screens;

public class IMGui extends InputModule
{

  private JFrame mainFrame;

  private Screens gui;

  private LinkedList<Changer> updateListeners = new LinkedList<Changer>();

  //private Signal[] buffer;

  public void setGui(Screens gui)
  {
    this.gui = gui;
  }

  public void registerUpdateListener(Changer listener)
  {
    updateListeners.add(listener);
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

  /**
   *  Actualize gui
   */
  @Override
  protected void put(Signal[] input)
  {
    int size = getNumberOfAssignedInputs();
    javax.swing.SwingUtilities.invokeLater(new Updater(input, size));
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
      System.out.println("Updater run " + updateListeners.size());
      for (Changer listener : updateListeners)
        listener.update(buffer);
    }
  }

}
