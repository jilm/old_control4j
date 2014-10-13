package control4j.tools;

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


public class Preferences
{

  private static final Preferences instance = new Preferences();

  private String project;

  public static Preferences getInstance()
  {
    return instance;
  }

  public void parseCommandLineArgs(String[] args)
  {
    if (args.length > 0)
    {
      project = args[args.length-1];
    }
  }

  public static java.util.prefs.Preferences get()
  {
    return java.util.prefs.Preferences.userRoot().node("control4j");
  }

  /**
   *  Returns the filename with project description.
   */
  public String getProject()
  {
    if (project != null)
      return project;
    else
      return get().get("project", "project.xml");     
  }

  /**
   *  Some kind of a min unit for components that are drawn into the GUI.
   *  Return value is in pixels.
   */
  public int getGuiMinSize()
  {
    return get().getInt("gui-min-size", 20);
  }


  /**
   *  <ul>
   *    <li> help
   *    <li> list [key]
   *    <li> set key value
   *    <li> export
   *    <li> import
   *  </ul>
   */
  public static void main(String[] args) throws Exception
  {
    java.util.prefs.Preferences prefs = get();
    String command = "help";
    if (args.length > 0) command = args[0];
    
    if (command.equals("list"))
    {
      String[] keys = prefs.keys();
      for (String key : keys)
        System.out.println(key + ": " + prefs.get(key, null));
    }
    else if (command.equals("set"))
    {
      String key = args[1];
      String value = args[2];
      prefs.put(key, value);
    }
    else if (command.equals("export"))
    {
      prefs.exportSubtree(System.out);
    }
  }

  /**
   *  Return colors for GUI.
   */
  public String getColor(String key)
  {
    // color from sense
    if (key.equals("warning"))
      return get().get(key, "orange"); 
    if (key.equals("error"))
      return get().get(key, "red");
    if (key.equals("invalid"))
      return get().get(key, "gray");
    if (key.equals("valid"))
      return get().get(key, "green");
    if (key.equals("on"))
      return get().get(key, "green");
    if (key.equals("off"))
      return get().get(key, "red");

    return "black";
  }

}
