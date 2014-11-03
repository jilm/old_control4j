package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.ld.Contact;
import control4j.ld.SerialContactBlock;

public class SerialRendererTest
{

  private SerialContactBlock contactBlock;
  private Contact contact1;
  private Contact contact2;
  private Contact contact3;
  private Contact contact4;

  @Before
  public void initialize()
  {
    contactBlock = new SerialContactBlock();
    contact1 = new Contact("XIC", "a");
    contact2 = new Contact("XIC", "b");
    contact3 = new Contact("XIO", "a_very_long_name");
    contact4 = new Contact("XIC", "another_long_name");
  }

  @Test
  public void test1()
  {
    SerialRenderer renderer = new SerialRenderer();
    renderer.append(contact1, 0);
    renderer.append(contact2, 0);
    renderer.append(contact3, 0);
    renderer.append(contact4, 0);
    renderer.complete();
    renderer.print();
  }

  @Test
  public void test2()
  {
    ParallelRenderer pr = new ParallelRenderer();
    SerialRenderer sr = new SerialRenderer();
    pr.append(contact1);
    pr.append(contact2);
    pr.complete(0);
    sr.append(contact3, 0);
    sr.append(pr);
    sr.complete();
    sr.print();
  }

  @Test
  public void test3()
  {
    ParallelRenderer pr = new ParallelRenderer();
    SerialRenderer sr = new SerialRenderer();
    pr.append(contact1);
    pr.append(contact2);
    pr.complete(0);
    sr.append(pr);
    sr.append(contact3, 0);
    sr.complete();
    sr.print();
  } 

  @Test
  public void test4()
  {
    ParallelRenderer pr1 = new ParallelRenderer();
    ParallelRenderer pr2 = new ParallelRenderer();
    SerialRenderer sr = new SerialRenderer();
    pr1.append(contact1);
    pr1.append(contact2);
    pr1.complete(0);
    pr2.append(contact3);
    pr2.append(contact4);
    pr2.append(contact1);
    pr2.complete(0);
    sr.append(pr1);
    sr.append(pr2);
    sr.append(contact2, 0);
    sr.complete();
    sr.print();
  }
 
}
