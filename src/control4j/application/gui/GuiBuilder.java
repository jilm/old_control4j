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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import control4j.ConfigurationHelper;
import control4j.application.Application;
import control4j.application.ModuleDeclaration;
import control4j.application.Input;
import control4j.application.Scope;
import control4j.Module;
import control4j.ModuleManager;
import control4j.modules.IMGui;
import control4j.modules.IGuiUpdateListener;
import control4j.gui.SignalAssignment;
import control4j.gui.GuiStructureTreeModel;
import control4j.gui.ChangerIterator;
import control4j.gui.changers.Changer;
import control4j.gui.Screens;
import control4j.tools.DeclarationReference;

/**
 *  Gets the gui and fill-in missing information mainly about a connection
 *  to the signals.
 */
public class GuiBuilder
{

  /**
   *
   */
  private JFrame frame;

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
    ChangerIterator changers = new ChangerIterator(screens);
    while (changers.hasNext())
    {
      Changer changer = changers.next();
      String signalName = changer.getSignalName();
      if (signalName != null && signalName.length() > 0) 
        signalNames.add(signalName);
    }

    // Create new processing module
    DeclarationReference reference = new DeclarationReference();
    reference.setText("Added by GuiBuilder class");
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
    changers = new ChangerIterator(screens);
    while (changers.hasNext())
    {
      Changer changer = changers.next();
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
    changers = new ChangerIterator(screens);
    while (changers.hasNext())
    {
      Changer changer = changers.next();
      if (changer.getSignalName() != null)
        guiModule.registerUpdateListener(changer);
    }

    // Create and set up the window
    frame = new JFrame("Top level demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.add(screens);

    guiModule.setMainFrame(frame);
    ModuleManager.getInstance().add(guiModule);
  }

  /**
   *
   */
  public JFrame getFrame()
  {
    return frame;
  }


  /**
   *
   */
  private void assignSignalIndexes(Object object, ComponentDeclaration declaration)
  {
    try
    {
      Field[] fields = object.getClass().getDeclaredFields();
      for (Field field : fields)
      {
        SignalAssignment annotation = (SignalAssignment)field.getAnnotation(SignalAssignment.class);
	if (annotation != null)
	{
          String signalKey = annotation.name();
	  String signalName = declaration.getSignal(signalKey);
	  for (int i=0; i<signalMap.length; i++)
	    if (signalMap[i].equals(signalName))
	    {
	      field.setAccessible(true);
	      field.setInt(object, i);
	    }
	}
      }
    }
    catch (Exception e)
    {
    }

  }

  /**
   *
   */
  private JComponent createInstance(String className)
  {
    try
    {
      Class _class = Class.forName(className);
      JComponent instance = (JComponent)_class.newInstance();
      return instance;
    }
    catch (ClassNotFoundException e)
    {
    }
    catch (InstantiationException e)
    {
    }
    catch (IllegalAccessException e)
    {
    }
    return null;
  }

  /**
   *
   */
  private class SaveGui implements ActionListener
  {
    JTabbedPane gui;

    public SaveGui(JTabbedPane gui)
    {
      this.gui = gui;
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
      System.out.println("Goingt to save gui ..." + e.getActionCommand());
      control4j.gui.Writer writer = new control4j.gui.Writer();
      java.io.OutputStream os = new java.io.FileOutputStream("/home/jilm/gui.xml");
      writer.write(gui, os);
      }
      catch (java.io.IOException ex)
      {
        System.exit(1);
      }
    }
  }

}
