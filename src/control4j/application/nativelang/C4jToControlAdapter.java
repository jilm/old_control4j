package control4j.application.nativelang;

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

import control4j.application.Scope;
import control4j.tools.DuplicateElementException;

public class C4jToControlAdapter extends AbstractAdapter
{

  protected control4j.application.Application destination;

  public C4jToControlAdapter(control4j.application.Application destination) {
    System.out.println(destination.getClass().getName());
    if (destination instanceof control4j.application.Application) {
      this.destination = (control4j.application.Application)destination;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  public void startLevel()
  {
    destination.startScope();
  }

  public void endLevel()
  {
    destination.endScope();
  }

  public void put(Module module)
  {
    Scope localScope = destination.getScopePointer();
    control4j.application.Module translated =
        new control4j.application.Module(module.getClassName());
    module.translate(translated, localScope, null, null);
    destination.addModule(translated);
  }

  public void put(Block block)
  {
    try
    {
      Scope localScope = destination.getScopePointer();
      control4j.application.Block translated =
          new control4j.application.Block();
      block.translate(translated, localScope);
      destination.putBlock(block.getName(),
          resolveScope(block.getScope(), localScope), translated);
    }
    catch (DuplicateElementException e)
    {
          // TODO:
    }
  }

  public void put(Signal signal)
  {
    try
    {
      Scope localScope = destination.getScopePointer();
      control4j.application.Signal translated
          = new control4j.application.Signal();
      signal.translate(translated, localScope);
      destination.putSignal(signal.getName(),
          resolveScope(signal.getScope(), localScope), translated);
    }
    catch (DuplicateElementException e)
    {
          // TODO:
    }
  }

  public void put(ResourceDef resource)
  {
    try
    {
      Scope localScope = destination.getScopePointer();
      control4j.application.Resource translated =
          new control4j.application.Resource(resource.getClassName());
      resource.translate(translated, localScope);
      destination.putResource(resource.getName(),
          resolveScope(resource.getScope(), localScope), translated);
    }
    catch (DuplicateElementException e)
    {
          // TODO:
    }
  }

  public void put(Define define)
  {
    try
    {
      Scope localScope = destination.getScopePointer();
      Scope scope =
          define.getScope() == 0 ? Scope.getGlobal() : localScope;
      destination.putDefinition(
          define.getName(), scope, define.getValue());
    }
    catch (DuplicateElementException e)
    {
          // TODO:
    }
  }

  public void put(Property property)
  {
    try
    {
      Scope localScope = destination.getScopePointer();
      if (property.isReference())
      {
        destination.putProperty(property.getKey(), property.getHref(),
            resolveScope(property.getScope(), localScope));
      }
      else
      {
        destination.putProperty(property.getKey(), property.getValue());
      }
    }
    catch (DuplicateElementException e)
    {
      // TODO:
    }
  }

  public void put(Use use)
  {
    Scope localScope = destination.getScopePointer();
    control4j.application.Use translated
        = new control4j.application.Use(use.getHref(),
        resolveScope(use.getScope(), localScope));
    use.translate(translated, localScope);
    destination.add(translated, localScope);
  }

  protected Scope resolveScope(int scopeCode, Scope localScope)
  {
    if (scopeCode == 0)
      return Scope.getGlobal();
    else if (scopeCode == 1)
      return localScope;
    else if (scopeCode == 2)
      return localScope.getParent();
    else
      throw new IllegalArgumentException();
  }

}
