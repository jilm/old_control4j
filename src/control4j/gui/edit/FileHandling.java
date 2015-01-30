package control4j.gui.edit;

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
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import control4j.gui.Reader;
import control4j.gui.Writer;
import control4j.gui.Screens;

/**
 *
 *  Provides file handling of the editor.
 *
 *  <p>This is the only object that can replace screens object.
 *
 */
class FileHandling 
implements ActionListener, FileEvent, DataListener, TreeModelListener
{

  /**
   *  File that was opened or saved last time.
   */
  private File file = null;

  /**
   *  Indicates that file has changed and need to be saved.
   */
  private boolean hasChanged = false;

  private Screens screens;

  /**
   *  Listeners to the file events.
   */
  private final LinkedList<FileListener> listeners =
    new LinkedList<FileListener>();

  private JFrame frame;

  /**
   *
   */
  FileHandling(JFrame frame)
  {
    super();
    this.frame = frame;
  }

  /**
   *  Adds File menu at the end of the given menu bar.
   */
  void addMenu(Menu menu)
  {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    menu.add(fileMenu);
    menu.addItem("New", "FILE_NEW", this, 'N');
    menu.addItem("Load", "FILE_LOAD", this, 'L');
    menu.addItem("Save", "FILE_SAVE", this, 'S');
    menu.addItem("Save As", "FILE_SAVE_AS", this, 'A');
    menu.addSeparator();
    menu.addItem("Exit", "FILE_EXIT", this, 'x');
  }

  /**
   *  Actualize menu to correspond to the current status.
   */
  protected void actualizeMenu()
  {
  }

  /**
   *  Ask user if he realy wants to close a file without saving changes.
   *
   *  @return true if the action should be performed, and false if
   *             it should be canceled
   */
  protected boolean askUser()
  {
    return true;
  }

  /**
   *  Response method for file menu items.
   */
  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("FILE_NEW"))
      fileNew();
    else if (e.getActionCommand().equals("FILE_LOAD"))
      load();
    else if (e.getActionCommand().equals("FILE_SAVE"))
      save();
    else if (e.getActionCommand().equals("FILE_SAVE_AS"))
      saveAs();
    else if (e.getActionCommand().equals("FILE_EXIT"))
      exit();
  }

  /**
   *  Response method for menu item file/new.
   */
  public void fileNew()
  {
    if (askUser())
    {
      screens = new Screens();
      file = null;
      hasChanged = false;  // nothing interesting to save
      actualizeMenu();
      fireFileChangedEvent();
    }
  }

  /**
   *  Response method for menu item file/load.
   */
  protected void load()
  {
    if (askUser())
    {
      JFileChooser fileChooser = new JFileChooser();
      int result = fileChooser.showOpenDialog(frame);
      if (result == JFileChooser.APPROVE_OPTION)
        try
        {
          file = fileChooser.getSelectedFile();
          java.io.InputStream is = new java.io.FileInputStream(file);
          load(is);
        }
        catch (java.io.FileNotFoundException ex) { }
	catch (IOException e) { }
    }
  }

  /**
   *  Loads the gui from the given input stream
   */
  protected void load(java.io.InputStream inputStream) throws IOException
  {
    try
    {
      control4j.gui.Reader reader = new control4j.gui.Reader();
      reader.load(inputStream);
      screens = reader.get();
      inputStream.close();
      hasChanged = false;
      actualizeMenu();
      fireFileChangedEvent();
    }
    catch (java.io.FileNotFoundException e) 
    {
      throw e;
    }
    catch (java.io.IOException e) 
    {
      throw e;
    }
  }

  /**
   *
   */
  public void load(String filename) throws IOException
  {
    try
    {
      file = new File(filename);
      java.io.InputStream is = new java.io.FileInputStream(file);
      load(is);
    }
    catch (java.io.IOException e) 
    {
      file = null;
      throw e;
    }
  }

  /**
   *  Response method for menu item file/save.
   */
  protected void save()
  {
    if (file == null)
      saveAs();
    else
      try
      {
	java.io.OutputStream os = new java.io.FileOutputStream(file);
	Writer writer = new Writer();
	writer.write(screens, os);
        hasChanged = false;
      }
      catch (java.io.FileNotFoundException ex) 
      {
        file = null;
      }
      catch (javax.xml.stream.XMLStreamException e)
      {
      }
    actualizeMenu();
  }

  /**
   *  Response method for menu item file/save as.
   */
  protected void saveAs()
  {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION)
    {
      file = fileChooser.getSelectedFile();
      save();
    }
    actualizeMenu();
    fireFileChangedEvent();
  }

  /**
   *  Response method for menu item file/exit
   */
  protected void exit()
  {
    if (askUser())
      System.exit(0);
  }

  /**
   *  Should be called whenever something has changed and file need to 
   *  be saved.
   */
  public void dataChanged(Screens screens)
  {
    hasChanged = true;
    this.screens = screens;
  }

  public File getFile()
  {
    return file;
  }

  public Screens getScreens()
  {
    return screens;
  }

  protected void fireFileChangedEvent()
  {
    for (FileListener listener : listeners)
      listener.fileChanged(this);
  }

  public FileListener addFileEventListener(FileListener listener)
  {
    if (listener != null) listeners.add(listener);
    return listener;
  }

  public FileListener removeFileEventListener(FileListener listener)
  {
    if (listener != null) listeners.remove(listener);
    return listener;
  }

  public void treeNodesChanged(TreeModelEvent e)
  {
  }

  public void treeNodesInserted(TreeModelEvent e)
  {
  }

  public void treeNodesRemoved(TreeModelEvent e)
  {
  }

  public void treeStructureChanged(TreeModelEvent e)
  {
  }

}
