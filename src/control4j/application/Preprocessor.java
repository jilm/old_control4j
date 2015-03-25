package control4j.application;

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

import java.util.Set;
import java.util.NoSuchElementException;
import java.text.MessageFormat;

public class Preprocessor
{

  public Preprocessor()
  { }

  private Application application;

  public void process(Application application)
  {
    this.application = application;

    // resolve configuration on the application level
    resolveConfiguration(application);
  }

  /**
   *  Goes through the all of the configuration items of the
   *  given object that were specified in the form of reference,
   *  finds appropriate referenced object and substitude the
   *  value for the reference inside the object.
   */
  protected void resolveConfiguration(Configurable object)
  {
    Set<String> keys = object.getReferenceConfigKeys();
    for (String key : keys)
    {
      Reference reference = object.getReferenceConfigItem(key);
      try
      {
        String value = application.getDefinition(
	    reference.getHref(), reference.getScope());
        object.resolveConfigItem(key, value);
      }
      catch (NoSuchElementException e)
      {
	reportMissingDefinition(key, reference, object);
      }
    }
  }

  public static void main(String[] args) throws Exception
  {
    String filename = args[0];
    java.io.File file = new java.io.File(filename);
    Loader loader = new Loader();
    ITranslatable translatable = loader.load(file);
    Application app = new Application();
    translatable.translate(app);
    Preprocessor preprocessor = new Preprocessor();
    preprocessor.process(app);
    System.out.println(app.toString());
  }

  /*
   *
   *     Report Errors
   *
   */

  /**
   *  Reports a message about missing definition.
   */
  private void reportMissingDefinition(
      String key, Reference reference, DeclarationBase object)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Cannot find appropriate define element for a property.\n")
      .append("Key: {0}, href: {1}.")
      .append("The property element is a part of:\n{2}\n");
    String message = MessageFormat.format(
	sb.toString(), key, reference.getHref(), 
	object.getDeclarationReferenceText());
    ErrorManager.getInstance().addError(message);
  }

}
