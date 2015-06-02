package control4j;

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

import static control4j.tools.Logger.catched;

import cz.lidinsky.tools.reflect.ObjectMapDecorator;
import cz.lidinsky.tools.reflect.Setter;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;

import java.io.Closeable;
import java.util.Set;

/**
 *  Common predecesor of all the resources.
 */
public abstract class Resource implements Closeable {

  public Resource() {}

  public abstract boolean isEquivalent(
      control4j.application.Resource definition);

  /**
   *  Initialize the resource.
   */
  public void initialize(control4j.application.Resource definition) {
    ObjectMapDecorator objectMap = new ObjectMapDecorator(String.class);
    objectMap.setSetterFilter(
	ObjectMapDecorator.getAnnotationPredicate(Setter.class));
    objectMap.setDecorated(this,
        ObjectMapDecorator.getStringSetterClosureFactory(this, true));
    Set<String> keySet = objectMap.keySet();
    for (String key : keySet) {
      try {
        objectMap.put(key, definition.getConfiguration().getString(key));
      } catch (ConfigItemNotFoundException e) {
        catched(this.getClass().getName(), "initialize", e);
      }
    }
  }

  public void prepare() {}

  public void close() {}

}
