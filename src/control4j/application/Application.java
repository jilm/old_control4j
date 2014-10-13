package control4j.application;

/*
 *  Copyright 2013 Jiri Lidinsky
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
import java.util.Collection;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import control4j.ErrorManager;
import control4j.application.gui.ScreenDeclaration;
import control4j.gui.Screens;

/**
 *  A crate object for the application. This crate object contains only
 *  declarations of the modules and resources.
 */
public class Application
{
  private LinkedList<ModuleDeclaration> modules;
  private LinkedList<ResourceDeclaration> resources;
  private Screens screens;
  private SignalManager signals;

  /**
   *  Create and initialize new instance.
   */
  public Application()
  {
    modules = new LinkedList<ModuleDeclaration>();
    resources = new LinkedList<ResourceDeclaration>();
    signals = SignalManager.getInstance();
  }

  /**
   *  Returns the collection of all module declarations.
   *
   *  @return the collection of all module declarations
   */
  public Collection<ModuleDeclaration> getModules()
  {
    return modules;
  }

  /**
   *  Returns the collection of all resource declarations.
   *
   *  @return the collection of all resource declarations
   */
  public Collection<ResourceDeclaration> getResources()
  {
    return resources;
  }

  /**
   *  Adds a module declaration given as the parameter.
   *
   *  @param module
   *             a module declaration to be added to the application
   */
  public void addModule(ModuleDeclaration module)
  {
    modules.add(module);
  }

  /**
   *  Adds a resource declaration given as the parameter.
   *
   *  @param resource
   *             a resource declaration to be added to the application
   */
  public void addResource(ResourceDeclaration resource)
  {
    resources.add(resource);
  }

  public void addSignal(SignalDeclaration signal)
  {
    try
    {
      signals.add(signal);
    }
    catch (SuchElementAlreadyExistsException e)
    {
      SignalDeclaration signal2 = signals.get(signal.getScope(), signal.getName());
      String reference2 = signal2.getDeclarationReferenceText();
      String reference1 = signal.getDeclarationReferenceText();
      ErrorManager.getInstance().reportDuplicateSignalDeclaration(reference1, reference2); 
    }
  }

  public SignalManager getSignals()
  {
    return signals;
  }

  public void setScreens(Screens screens)
  {
    this.screens = screens;
  }

  public Screens getScreens()
  {
    return screens;
  }

  public boolean hasGui()
  {
    return screens != null;
  }

}
