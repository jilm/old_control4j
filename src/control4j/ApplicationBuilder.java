package control4j;

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

import control4j.application.Application;
import control4j.application.ModuleDeclaration;
import control4j.application.ResourceDeclaration;
import control4j.resources.Resource;
import control4j.tools.DeclarationReference;
import static control4j.tools.LogMessages.*;
import static control4j.tools.Logger.*;
import java.util.Collection;

/**
 *
 *  Takes declarations of all modules, resources and signals and
 *  creates instances of objects that implement that functionality.
 *  To build the application, call the
 *  {@link #build} method. Created instances will be stored in
 *  {@link ResourceManager} and {@link ModuleManager} objects.
 *
 *  @see ResourceManager
 *  @see ModuleManager
 *
 */
public class ApplicationBuilder
{

  /**
   *  How many fatal errors was detected during the process of application
   *  building.
   */
  private int fatalErrors = 0;

  /**
   *  Build the application into the executable form from based on the
   *  given declaration. The result will be stored in the ResourceManager
   *  and ModuleManager objects.
   *
   *  @param application
   *             the declaration of the application which will be used
   *             to create executable form
   *
   *  @throws SyntaxErrorException
   *             if it is not possible to create executable form from
   *             the given declaration. There could be many reasons.
   *
   *  @see ResourceManager
   *  @see ModuleManager
   */
  public void build(Application application) throws SyntaxErrorException
  {
    // create resources
    fine("Going to build resources ...");
    buildResources(application.getResources());
    // create modules
    fine("Going to create modules ... ");
    buildModules(application.getModules());
    // if there is a gui, build it
    if (application.hasGui())
    {
      fine("Going to build gui ... ");
      control4j.application.gui.GuiBuilder guiBuilder
        = new control4j.application.gui.GuiBuilder();
      guiBuilder.build(application);
    }
    ModuleManager.getInstance().complete();
  }

  private void buildResources(Collection<ResourceDeclaration> resourceDeclarations) throws SyntaxErrorException
  {
    ResourceManager manager = ResourceManager.getInstance();
    // create instances for all of the resource declarations
    for (ResourceDeclaration declaration : resourceDeclarations)
      try
      {
        Resource resource = Resource.createInstance(declaration);
        manager.add(declaration.getName(), resource);
      }
      catch (SyntaxErrorException e)
      {
        fatalErrors ++;
        reportSyntaxError1(declaration, e);
      }
      catch (SystemException e)
      {
        fatalErrors ++;
	reportSystemError1(declaration, e);
      }
    // create potential connections between resources
    for (ResourceDeclaration declaration : resourceDeclarations)
      try
      {
        Resource resource = manager.get(declaration.getName());
	manager.assignResources(resource, declaration.getConfiguration());
      }
      catch (SyntaxErrorException e)
      {
        fatalErrors ++;
      }
      catch (SystemException e)
      {
        fatalErrors ++;
      }
  }

  private void buildModules(Collection<ModuleDeclaration> moduleDeclarations)
  {
    ModuleManager moduleManager = ModuleManager.getInstance();
    for (ModuleDeclaration moduleDeclaration : moduleDeclarations)
    {
      try
      {
        Module module = Module.getInstance(moduleDeclaration);
        moduleManager.add(module);
      }
      catch (SyntaxErrorException e)
      {
        ErrorManager.getInstance().reportSyntaxError(e.getMessage(), moduleDeclaration.getDeclarationReferenceText());
      }
      catch (ModuleImplementationException e)
      {
      }
    }
  }

  /**
   *
   */
  private void reportSyntaxError1(ResourceDeclaration declaration, Exception exception)
  {
    StringBuilder message = new StringBuilder();
    message.append(getMessage("coreap01"));
    message.append('\n');
    message.append(exception.getMessage());
    message.append('\n');
    DeclarationReference reference = declaration.getDeclarationReference();
    if (reference != null)
    {
      message.append(reference.toString());
      message.append('\n');
    }
    System.err.print(message.toString());
  }

  /**
   *
   */
  private void reportSystemError1(ResourceDeclaration declaration, Exception exception)
  {
    StringBuilder message = new StringBuilder();
    message.append(getMessage("coreap02"));
    message.append('\n');
    message.append(exception.getMessage());
    message.append('\n');
    String className = declaration.getClassName();
    message.append(getMessage("coreap03", className));
    message.append('\n');
    System.err.print(message.toString());
  }

}
