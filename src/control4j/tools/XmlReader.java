package control4j.tools;

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

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static control4j.tools.Logger.*;

/**
 *
 *  Uses a SAX parser to read given XML document. This class
 *  serves like a dispatcher which receives events from parser
 *  and hand them over to the custom handler object. It uses
 *  annotations to choose appropriate reciver method for a
 *  given event.
 *
 *  <p>The input XML document may contain parts that belongs
 *  to defferent grammers. To handle such a document, this
 *  class contains a stack for a handler classes. If the handler
 *  reach an element which is the root element of the foreign
 *  subtree, it must simply create an appropriate handler and
 *  the events will be dispatched into that handler as long as
 *  the end element of the subtree is reached. Than the control
 *  is return back to the parent handler.
 *
 */
public class XmlReader extends DefaultHandler
{

  /** Contain handlers */
  private HandlerCrate handlerStack;

  /** Locator of the document */
  private Locator locator;

  /** Indicate that new handler object was added and that the
      last event must be sent again. */
  private boolean repeat = false;

  private int line;
  private int column;

  private String localName;
  private String namespace;

  /**
   *  Does nothing.
   */
  public XmlReader()
  {
  }

  /**
   *  Adds a handler object. Methods of this object are used to
   *  respond to the xml start element and xml end element events.
   *  Such methods must be annotated by XmlStartElement and 
   *  XmlEndElement annotations. A handler must be added before
   *  the load method is called.
   */
  public void addHandler(IXmlHandler handler)
  {
    if (handler == null) throw new NullPointerException();
    handlerStack = new HandlerCrate(handlerStack);
    handlerStack.handler = handler;
    repeat = true;
  }

  /**
   *  Removes a handler that is on top of the handler stack.
   *  This method is called as soon as the XML subtree is
   *  processed.
   */
  protected void removeHandler()
  {
    handlerStack = handlerStack.parent;
  }

  /**
   *  Creates new SAX parser and reads the XML document from 
   *  the given input stream.
   *
   *  @param inputStream
   *             a stream from which the XML document is read
   *
   *  @throws IOException
   *             if something goes wrong
   */
  public void load(InputStream inputStream) throws IOException
  {
    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      factory.setXIncludeAware(true);
      SAXParser parser = factory.newSAXParser();
      parser.parse(inputStream, this);
    }
    catch (SAXException e)
    {
      throw new IOException(e);
    }
    catch (ParserConfigurationException e)
    {
      throw new IOException(e);
    }
  }

  public String getLocalName()
  {
    return localName; // TODO
  }

  public String getNamespace()
  {
    return namespace; // TODO
  }

  /**
   *  Goes through methods of the handler object and returns
   *  the method that match the most precisly to the given
   *  criteria.
   */
  protected Method findHandler(
      String namespace, String localName, boolean isStartElement)
      throws NoSuchElementException
  {
    Method result = null;
    int resultRank = -1;

    Method[] methods = handlerStack.handler.getClass().getDeclaredMethods();
    for (Method method : methods)
    {

      // get annotation informations
      String annoLocalName;
      String annoNamespace;
      String annoParent;
      String annoParentNamespace;
      if (isStartElement)
      {
        XmlStartElement annotation 
	    = method.getAnnotation(XmlStartElement.class);
	if (annotation == null) continue;
	annoLocalName = annotation.localName();
	annoNamespace = annotation.namespace();
	annoParent = annotation.parent();
	annoParentNamespace = annotation.parentNamespace();
      }
      else
      {
        XmlEndElement annotation 
	    = method.getAnnotation(XmlEndElement.class);
	if (annotation == null) continue;
	annoLocalName = annotation.localName();
	annoNamespace = annotation.namespace();
	annoParent = annotation.parent();
	annoParentNamespace = annotation.parentNamespace();
      }

      int rank = 0;

      // decide wheather it match local name
      if (annoLocalName.equals(localName))
	rank += 8;
      else if (!annoLocalName.equals("*"))
	continue;

      // if the namespace match
      if (annoNamespace.equals(namespace))
	rank += 4;
      else if (annoNamespace.equals("^") 
	  && handlerStack.elementStack.namespace.equals(namespace))
	rank += 4;
      else if (!annoNamespace.equals("*"))
	continue;

      // if it match parent local name
      if (annoParent.equals(handlerStack.elementStack.localName))
	rank += 2;
      else if (!annoParent.equals("*"))
	continue;

      // parent namespace
      if (annoParentNamespace.equals(handlerStack.elementStack.namespace))
	rank += 1;
      else if (!annoParentNamespace.equals("*"))
	continue;

      if (rank > resultRank)
      {
	resultRank = rank;
	result = method;
      }
    }
    
    if (result == null)
      throw new NoSuchElementException();
    else
      return result;
  }

  /*
   *
   *   Overriden methods of the SAX Default Handler class
   *   Respond methods to the SAX parser events.
   *
   */

  /**
   *  Gets a start element event from the SAX parser, finds 
   *  the appropriate method to serve this event and runs it.
   *  Do not override this method.
   */
  @Override
  public final void startElement(
      String uri, String localName, String qName, Attributes attributes) 
      throws SAXException
  {
    this.localName = localName;
    this.namespace = uri;
    try
    {
      while (true)
      {
        // find a method that can handle the event
        Method method = findHandler(uri, localName, true);

        // call the method
	repeat = false;
        method.setAccessible(true);
        method.invoke(handlerStack.handler, attributes);
        method.setAccessible(false);

        // if the event has not been handled, try again
	if (!repeat) break;

	handlerStack.handler.startProcessing(this);
      }
    }
    // there is no appropriate handler method
    catch (NoSuchElementException e)
    {
      reportMissingHandler("start element");
    }
    catch (IllegalAccessException e)
    {
      throw new SAXException(e);
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      throw new SAXException(e);
    }
    finally
    {
      // add an element into the element stack
      handlerStack.elementStack = new ElementCrate(handlerStack.elementStack);
      handlerStack.elementStack.localName = localName;
      handlerStack.elementStack.namespace = uri;
      // remember the location
      line = locator.getLineNumber();
      column = locator.getColumnNumber();
    }
  }

  /**
   *  Gets end element from SAX parser, finds appropriate method and
   *  runs it. Do not override this method.
   */
  @Override
  public final void endElement(String uri, String localName, String qName)
  throws SAXException
  {
    this.localName = localName;
    this.namespace = uri;
    // remove the last element from the element stack
    handlerStack.pop();

    try
    {
      // find a method that implemnts the work
      Method method = findHandler(uri, localName, false);

      // call the method
      method.setAccessible(true);
      method.invoke(handlerStack.handler);
      method.setAccessible(false);
    }
    // there is no appropriate handler method
    catch (NoSuchElementException e)
    {
      reportMissingHandler("end element");
    }
    catch (IllegalAccessException e)
    {
      throw new SAXException(e);
    }
    catch (java.lang.reflect.InvocationTargetException e)
    {
      throw new SAXException(e);
    }
    finally
    {
      // if the element stack is empty, remove the handler from the
      // top of the handler stack
      if (handlerStack.isEmpty()) 
      {
	handlerStack.handler.endProcessing();
        removeHandler();
      }
      // remember the location
      line = locator.getLineNumber();
      column = locator.getColumnNumber();
    }
  }

  /**
   *  Does nothing.
   */
  @Override
  public final void startDocument()
  {
    handlerStack.handler.startProcessing(this);
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Does nothing.
   */
  @Override
  public final void endDocument()
  {
    //handlerStack.handler.endProcessing();
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Stores a locator for future use.
   */
  @Override
  public final void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
  }

  /** A buffer to collect the whole text content of some element */
  private StringBuilder characterBuffer = new StringBuilder();

  /**
   *  Stores characters into the character buffer.
   */
  @Override
  public final void characters(char[] ch, int start, int length)
  {
    characterBuffer.append(ch, start, length);
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /**
   *  Does nothing, except, it stores a location.
   */
  @Override
  public final void ignorableWhitespace(char[] ch, int start, int length)
  {
    line = locator.getLineNumber();
    column = locator.getColumnNumber();
  }

  /*
   *
   *  Implementation of internal stack of handlers and
   *  an implemnetation of stack of elements.
   *
   */

  /**
   *  A crate class which contains a handler object together with
   *  the element stack for this handler. Handlers are linked.
   */
  private static class HandlerCrate
  {

    /** the handler. */
    IXmlHandler handler;

    /** parent handler or null. */
    HandlerCrate parent;

    /** element stack for the handler. */
    ElementCrate elementStack;

    /**
     *  Initialize new handler crate. It adds an empty element
     *  crate that stands for a root element into the element
     *  stack.
     */
    HandlerCrate(HandlerCrate parent)
    {
      this.parent = parent;
      elementStack = new ElementCrate(null);
      elementStack.localName = "";
      elementStack.namespace = "";
    }

    /**
     *  Removes an element from the top of the stack.
     */
    void pop()
    {
      elementStack = elementStack.parent;
    }

    /**
     *  Returns true if the element stack is empty.
     */
    boolean isEmpty()
    {
      return elementStack.parent == null;
    }
  }

  /**
   *  One entry of the element stack.
   */
  private static class ElementCrate
  {
    /** Local name of the element. */
    String localName;

    /** Namespace of the element. */
    String namespace;

    /** Parent element. */
    ElementCrate parent;

    ElementCrate(ElementCrate parent)
    {
      this.parent = parent;
    }
  }

  /**
   *  Log the fact, that the handler method for some XML event
   *  has not been found. It could mean, that eather the XML
   *  document contains something it should not, or it means,
   *  that the developer doesn't write the handler object
   *  properly.
   *
   *  @param event
   *             a text description of the event. For example:
   *             "start element", "end element", ...
   */
  protected void reportMissingHandler(String event)
  {
    fine(java.text.MessageFormat.format(
	"Didn''t find any handler for the event: {0}.\n" +
	"Last start element: '{'{1}'}':{2},\n" +
	"on line: {3,number,integer}; column: {4,number,integer}; " +
	"public id: {5}; system id: {6}", 
	event, namespace, localName, line, column,
	locator.getPublicId(), locator.getSystemId()));
  }

}
