package control4j.application;

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
import java.util.NoSuchElementException;

/**
 *  Class dedicated to store, manage and return signal declarations.
 *  Each signal si identified by the name and scope. Each signal
 *  declaration which is added to this object gets a unique integer
 *  identifier which is called handler.
 *
 *  <p>This class is singleton. There is only one instance.
 */
public class SignalManager
{

  /** Internal structure to store all of the signal declarations */
  private ArrayList<SignalDeclaration> signalDeclarations 
                             = new ArrayList<SignalDeclaration>();

  /**
   *  The onaly instance of this class
   */
  private static SignalManager instance = new SignalManager();

  private SignalManager()
  { }

  /**
   *  Returns the only instance of this class.
   */
  public static SignalManager getInstance()
  {
    return instance;
  }

  /**
   *  Adds a new signal declaration into the internal buffer
   *  and returns a handler which is assigned to the declaration
   *  by this method. If there already is a signal with the same
   *  name and scope an exception is thrown.
   *
   *  @param signal
   *             signal declaration object to add
   *
   *  @return handler of the signal for furhter manipulations
   *
   *  @throws SuchElementAlreadyExistsException
   *             if there already is such signal present in the internal
   *             buffer
   */
  public int add(SignalDeclaration signal)
  {
    try
    {
      int handler = getHandler(signal.getScope(), signal.getName());
      throw new SuchElementAlreadyExistsException();
    }
    catch (NoSuchElementException e)
    {
      signalDeclarations.add(signal);
      return signalDeclarations.size() - 1;
    }
  }

  /**
   *  Returns signal declaration of the signal with given scope and name.
   *  The scope of the returned signal doesn't have to be exactly the
   *  same as given in the parameter, but must belong to the scope tree
   *  (must be equal to one in the parent chain) and if there is more
   *  then one signal with given name, it returns the one that has
   *  closest scope to the one that is given.
   *  
   *  @param scope
   *            scope of the requested signal
   *
   *  @param name
   *            name of the requested signal
   *
   *  @return signal declaration object with the given scope and name
   *
   *  @throws NoSuchElementException
   *            if there is not a signal with given scope and name
   */
  public SignalDeclaration get(Scope scope, String name)
  {
    int handler = getHandler(scope, name);
    return get(handler);
  }

  /**
   *  Returns handler of the signal with given scope and name.
   *  The scope of the returned signal doesn't have to be exactly the
   *  same as given in the parameter, but must belong to the scope tree
   *  (must be equal to one in the parent chain) and if there is more
   *  then one signal with given name, it returns the one that has
   *  closest scope to the one that is given.
   *  
   *  @param scope
   *            scope of the requested signal
   *
   *  @param name
   *            name of the requested signal
   *
   *  @return handler of the signal with given scope and name
   *
   *  @throws NoSuchElementException
   *            if there is not a signal with given scope and name
   */
  public int getHandler(Scope scope, String name)
  {
    int handler = -1;
    int minDistance = Integer.MAX_VALUE; 
    for (int i=0; i<signalDeclarations.size(); i++)
    {
      SignalDeclaration signal = get(i);
      if (signal.getName().equals(name))
      {
        int distance = scope.belongs(signal.getScope());
	if (distance == 0)
	{
	  return i;
	}
        else if (distance > 0 && distance < minDistance)
	{
	  minDistance = distance;
	  handler = i;
	} 
      }
    }
    if (handler >= 0)
      return handler;
    else
      throw new NoSuchElementException("There is not a signal with given name");
  }

  /**
   *  Returns signal declaration with given handler.
   *
   *  @param handler
   *             identifier of the requested signal
   *
   *  @return a signal declaration object with the given
   *             handler
   *
   *  @throws IndexOutOfBoundsException
   *             if there is not a signal with given handler
   */
  public SignalDeclaration get(int handler)
  {
    return signalDeclarations.get(handler);
  }

  /**
   *  Returns number of all the signal declarations in the internal
   *  buffer.
   *
   *  @return number of the signal declarations stored
   */
  public int size()
  {
    return signalDeclarations.size();
  }

  private static int computeHash(Scope scope, String name)
  {
    int scopeHash = scope.hashCode();
    int nameHash = name.hashCode();
    return scopeHash ^ nameHash;
  }

}
