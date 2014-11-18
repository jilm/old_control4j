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

import java.awt.event.InputEvent;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import control4j.gui.Screens;
import control4j.gui.components.Screen;
import control4j.gui.changers.IChangeable;
import control4j.gui.changers.Changer;

/**
 *
 *  Data transfer support for the object tree panel.
 *
 */
class TreeTransferHandler
extends javax.swing.TransferHandler
{

  /**
   *
   */
  protected DataFlavor[] supportedFlavors;

  /**
   *
   */
  public TreeTransferHandler()
  {
    try
    {
      supportedFlavors = new DataFlavor[1];
      supportedFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType
	+ ";class=javax.swing.tree.TreePath");
    }
    catch (ClassNotFoundException e)
    {
      supportedFlavors = null;
    }
  }

  /**
   *
   */
  @Override
  public boolean canImport(javax.swing.TransferHandler.TransferSupport support)
  {
    System.out.println("canImport");
    if (support.isDataFlavorSupported(supportedFlavors[0]))
      return true;
    else
      return false;
  }

  /**
   *
   */
  @Override
  protected Transferable createTransferable(JComponent c)
  {
    JTree tree = (JTree)c;
    System.out.println(tree.getClass().getName());
    if (tree.getSelectionCount() == 0)
      return null;
    else
      return new TreeTransferable(tree.getLeadSelectionPath());
  }

  @Override
  public void exportAsDrag(JComponent comp, InputEvent e, int action)
  {
    System.out.println("exportAsDrag");
    super.exportAsDrag(comp, e, action);
  }

  @Override
  protected void exportDone(JComponent source, Transferable data, int action)
  {
    System.out.println("exportDone");
    if (action == MOVE)
    {
    }
  }

  @Override
  public int getSourceActions(JComponent c)
  {
    return COPY | MOVE;
  }

  /**
   *
   */
  @Override
  public boolean importData(TransferHandler.TransferSupport support)
  {
    if (support == null) throw new NullPointerException();
    if (canImport(support))
    {
      // get a place where the data should be imported
      JTree target = (JTree)support.getComponent();
      TreePath targetPath;
      if (support.isDrop())
        targetPath = target.getDropLocation().getPath();
      else
	targetPath = target.getLeadSelectionPath();
      // get a data to import
      try
      {
        TreePath sourcePath = (TreePath)support.getTransferable()
	  .getTransferData(supportedFlavors[0]);
	Object sourceObject = sourcePath.getLastPathComponent();
	Object targetObject = targetPath.getLastPathComponent();
	if (sourceObject instanceof Screens)
	{
	  // root element can not be moved or copied
	  return false;
	}
	else if (sourceObject instanceof Screen)
	{
	  // only root element can hold screen object
	  // so, if the target path is another screen, place source here
	  if (targetObject instanceof Screen)
	  {
	    Screens root = (Screens)((Screen)targetObject).getParent();
	    int index = root.getIndexOfChild((Screen)targetObject);
	    System.out.println("Copy screen at index: " + index);
	    //Copy.copy(sourceObject, root, index);
	  }
          // if the target is root, place screen at the end
	  else if(targetObject instanceof Screens)
	  {
	    System.out.println("Copy screen at the end of screens");
	    //Copy.copy(sourceObject, targetObject, -1);
	  }
	  // else do nothing
	  else
	  {
	    return false;
	  }
	}
	// component -> panel
	else if (sourceObject instanceof JComponent 
	  && targetObject instanceof JPanel)
	{
	  System.out.println("Component -> Panel");
	  //Copy.copy(sourceObject, targetObject, -1);
	}
	// Component at the position of another component
	else if (sourceObject instanceof JComponent
	  && targetObject instanceof JComponent)
	{
	  System.out.println("Component -> Component");
	  JComponent source = (JComponent)sourceObject;
	  JComponent targetComponent = (JComponent)targetObject;
	  JPanel parent = (JPanel)targetComponent.getParent();
          int index = parent.getComponentZOrder(targetComponent);
	  //parent.setComponentZOrder((JComponent)Copy.copy(source, parent, -1), index);
	  return true;
	}
	// Changer -> changeble
	else if (sourceObject instanceof Changer 
	  && targetObject instanceof IChangeable)
        {
	  System.out.println("Changer -> Changeable");
	  //Copy.copy(sourceObject, targetObject, -1);
	}
	else
	{
	  System.out.println("???");
	}

      }
      catch (java.awt.datatransfer.UnsupportedFlavorException e)
      {
      }
      catch (java.io.IOException e)
      {
      }
      return false;
    }
    else
      return false;
  }


  /**
   *
   */
  private class TreeTransferable implements Transferable
  {

    /**
     *
     */
    private TreePath data;

    /**
     *
     */
    TreeTransferable(TreePath data)
    {
      this.data = data;
    }

    /**
     *
     */
    public DataFlavor[] getTransferDataFlavors()
    {
      return supportedFlavors;
    }

    /**
     *
     */
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
      if (supportedFlavors[0].equals(flavor))
	return true;
      else
	return false;
    }

    /**
     *
     */
    public Object getTransferData(DataFlavor flavor)
    {
      return data;
    }

  }

}
