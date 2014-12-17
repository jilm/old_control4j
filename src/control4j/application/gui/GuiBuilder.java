package control4j.application.gui;

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
import java.util.Set;
import java.util.HashSet;
import control4j.application.Application;
import control4j.application.ModuleDeclaration;
import control4j.application.Input;
import control4j.application.Scope;
import control4j.Module;
import control4j.Control;
import control4j.ModuleManager;
import control4j.ICycleEventListener;
import control4j.modules.IMGui;
import control4j.modules.IGuiUpdateListener;
import control4j.gui.GuiObject;
import control4j.gui.Changer;
import control4j.gui.Screens;
import control4j.gui.ComponentIterator;
import control4j.tools.DeclarationReference;

/**
 *
 *  Takes the gui tree and fill-in missing information mainly about a 
 *  signal connections. Moreover, it creates and configures a IMGui
 *  module which hand signals to the gui.
 *
 */
public class GuiBuilder
{

  /**
   *  A module which is responsible for collecting signals for gui and for
   *  passing these signals into gui. This module is created by this class
   *  and it is not neccessary to define it by hand.
   */
  private IMGui guiModule;

  /**
   *
   */
  private String[] signalMap;

  /**
   *
   */
  public void build(Application application)
  {
    // Get screen definitions
    Screens screens = application.getScreens();

    // Get the set of signals displayed by the gui
    Set<String> signalNames = new HashSet<String>();
    ComponentIterator changers 
      = new ComponentIterator(screens, Changer.class);
    while (changers.hasNext())
    {
      Changer changer = (Changer)changers.next();
      String signalName = changer.getSignalName();
      if (signalName != null && signalName.length() > 0) 
        signalNames.add(signalName);
    }

    // Create new processing module
    DeclarationReference reference = new DeclarationReference();
    reference.setText("Added by GuiBuilder object");
    signalMap = new String[signalNames.size()];
    ModuleDeclaration guiModuleDecl 
        = new ModuleDeclaration("control4j.modules.IMGui");
    int index = 0;
    for (String signalName : signalNames)
    {
      signalMap[index] = signalName;
      Input input = new Input(Scope.getGlobal(), signalName);
      input.setIndex(index);
      input.setDeclarationReference(reference);
      guiModuleDecl.addInput(input);
      index++;
    }
    guiModule = (IMGui)Module.getInstance(guiModuleDecl);

    // Set signal indexes for changers
    changers = new ComponentIterator(screens, Changer.class);
    while (changers.hasNext())
    {
      Changer changer = (Changer)changers.next();
      String signalName = changer.getSignalName();
      if (signalName == null)
        changer.setSignalIndex(-1);
      else
        // find the signal name in the array
        for (int i=0; i<signalMap.length; i++)
          if (signalMap[i].equals(signalName))
	  {
	    changer.setSignalIndex(i);
	    break;
	  }
    }

    // Register all of the changers to get updates
    changers = new ComponentIterator(screens, Changer.class);
    while (changers.hasNext())
    {
      Changer changer = (Changer)changers.next();
      if (changer.getSignalName() != null)
        guiModule.registerUpdateListener(changer);
    }

    // If some gui object implements ICycleEventListener interface,
    // subscribe it.
    ComponentIterator guiObjects
      = new ComponentIterator(screens, GuiObject.class);
    while (guiObjects.hasNext())
    {
      GuiObject guiObject = guiObjects.next();
      if (guiObject instanceof ICycleEventListener)
	Control.registerCycleEventListener((ICycleEventListener)guiObject);
    }

    // finally add the gui module into the module manager
    guiModule.setGui(screens);
    ModuleManager.getInstance().add(guiModule);
  }

}
