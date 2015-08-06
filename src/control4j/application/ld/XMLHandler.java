package control4j.application.ld;

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

import static cz.lidinsky.tools.chain.Factory.getInstantiator;
import static org.apache.commons.collections4.PredicateUtils.notNullPredicate;

import control4j.ld.Coil;
import control4j.ld.Contact;
import control4j.ld.ContactBlock;
import control4j.ld.LadderDiagram;
import control4j.ld.ParallelContactBlock;
import control4j.ld.Rung;
import control4j.ld.SerialContactBlock;

import java.util.ArrayList;
import java.util.ArrayDeque;
import control4j.tools.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import control4j.tools.DuplicateElementException;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;
import cz.lidinsky.tools.xml.IXMLHandler;
import cz.lidinsky.tools.xml.XMLReader;
import cz.lidinsky.tools.xml.AXMLStartElement;
import cz.lidinsky.tools.xml.AXMLEndElement;
import cz.lidinsky.tools.xml.AXMLDefaultUri;
import cz.lidinsky.tools.xml.AXMLText;
import cz.lidinsky.tools.chain.Factory;

import java.lang.reflect.Method;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Predicate;

/**
 *
 *
 */
@AXMLDefaultUri("http://control4j.lidinsky.cz/ld")
public class XMLHandler implements IXMLHandler
{

  protected static Factory<Object, AbstractAdapter> adapterFactory;

  {
    adapterFactory = new Factory<Object, AbstractAdapter>();
    adapterFactory.add(getInstantiator(Ld2ControlAdapter.class));
  }

  private AbstractAdapter adapter;

  /**
   *
   */
  public XMLHandler()
  {
  }

  public void setHandler(AbstractAdapter handler) {
    adapter = handler;
  }

  /*
   *
   *    Implementation of IXMLHandler
   *
   */


  public void startProcessing() {
    if (adapter == null) {
      throw new CommonException()
        .setCode(ExceptionCode.NULL_POINTER)
        .set("message", "Adapter missing!");
    }
  }

  /**
   *  Cleen up.
   */
  public void endProcessing()
  {
  }

  protected LadderDiagram ld;

  @AXMLStartElement("{http://control4j.lidinsky.cz/application}application/ld")
  public boolean startApplicationLd(Attributes attributes) {
    ld = new LadderDiagram();
    adapter.startLd();
    return true;
  }

  @AXMLStartElement("/ld")
  public boolean startLd(Attributes attributes) {
    ld = new LadderDiagram();
    adapter.startLd();
    return true;
  }

  @AXMLEndElement("ld")
  public boolean endLd() {
    // TODO:
    adapter.put(ld);
    adapter.endLd();
    ld = null;
    return true;
  }

  @AXMLText("ld/description")
  public boolean textLdDescription(String text) {
    // TODO:
    return true;
  }

  protected Rung rung;

  @AXMLStartElement("ld/rung")
  public boolean startLdRung(Attributes attributes) {
    rung = new Rung();
    return true;
  }

  @AXMLEndElement("ld/rung")
  public boolean endLdRung() {
    // TODO:
    adapter.put(rung);
    rung = null;
    return true;
  }

  protected ArrayDeque<ContactBlock> contactStack
                                           = new ArrayDeque<ContactBlock>();

  @AXMLStartElement("rung/serial")
  public boolean startRungSerial(Attributes attributes) {
    // TODO: contact stack should be empty
    SerialContactBlock contacts = new SerialContactBlock();
    rung.setContactBlock(contacts);
    contactStack.push(contacts);
    return true;
  }

  @AXMLStartElement("rung/parallel")
  public boolean startRungParallel(Attributes attributes) {
    // TODO: contact stack should be empty
    ParallelContactBlock contacts = new ParallelContactBlock();
    rung.setContactBlock(contacts);
    contactStack.push(contacts);
    return true;
  }

  @AXMLStartElement("rung/contact")
  public boolean startRungContact(Attributes attributes) {
    // TODO: contact stack should be empty
    Contact contact = new Contact();
    contact.setName(attributes.getValue("name"));
    contact.setType(attributes.getValue("type"));
    rung.setContactBlock(contact);
    //contactStack.push(contact);
    return true;
  }

  @AXMLText("rung/description")
  public boolean textRungDescription(String text) {
    return true;
  }

  @AXMLStartElement("serial/parallel")
  public boolean startSerialParallel(Attributes attributes)
  {
    ParallelContactBlock contacts = new ParallelContactBlock();
    ((SerialContactBlock)contactStack.getFirst()).add(contacts);
    contactStack.push(contacts);
    return true;
  }

  @AXMLEndElement("parallel")
  public boolean endParallel() {
    contactStack.pop();
    return true;
  }

  @AXMLStartElement("parallel/serial")
  public boolean startParallelSerial(Attributes attributes)
  {
    SerialContactBlock contacts = new SerialContactBlock();
    ((ParallelContactBlock)contactStack.getFirst()).add(contacts);
    contactStack.push(contacts);
    return true;
  }

  @AXMLEndElement("serial")
  public boolean endSerial() {
    contactStack.pop();
    return true;
  }

  @AXMLStartElement("serial/contact")
  public boolean startSerialContact(Attributes attributes)
  {
    Contact contact = new Contact();
    contact.setName(attributes.getValue("name"));
    contact.setType(attributes.getValue("type"));
    ((SerialContactBlock)contactStack.getFirst()).add(contact);
    return true;
  }

  @AXMLStartElement("parallel/contact")
  public boolean startParallelContact(Attributes attributes)
  {
    Contact contact = new Contact();
    contact.setName(attributes.getValue("name"));
    contact.setType(attributes.getValue("type"));
    ((ParallelContactBlock)contactStack.getFirst()).add(contact);
    return true;
  }

  @AXMLEndElement("contact")
  public boolean endContact() {
    return true;
  }

  @AXMLStartElement("rung/coil")
  public boolean startRungCoil(Attributes attributes) {
    Coil coil = new Coil(attributes.getValue("name"));
    coil.setType(attributes.getValue("type"));
    rung.addCoil(coil);
    return true;
  }

  @AXMLEndElement("coil")
  public boolean endCoil() {
    return true;
  }

  @AXMLStartElement("description")
  public boolean startDescription(Attributes attributes) {
    // TODO:
    return true;
  }

  @AXMLEndElement("description")
  public boolean endDescription() {
    // TODO:
    return true;
  }

}
