package control4j.ld.text;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import control4j.ld.Contact;
import control4j.ld.SerialContactBlock;
import control4j.ld.ParallelContactBlock;

public class ParallelRendererTest
{

  private ParallelContactBlock contactBlock;
  private Contact contact1;
  private Contact contact2;
  private Contact contact3;
  private Contact contact4;

  @Before
  public void initialize()
  {
    contactBlock = new ParallelContactBlock();
    contact1 = new Contact("XIC", "a");
    contact2 = new Contact("XIC", "b");
    contact3 = new Contact("XIO", "a_very_long_name");
    contact4 = new Contact("XIC", "another_long_name");
  }

  @Test
  public void test1()
  {
    ParallelRenderer renderer = new ParallelRenderer();
    renderer.append(contact1);
    renderer.append(contact2);
    renderer.append(contact3);
    renderer.append(contact4);
    renderer.complete(0);
    renderer.print();
  }

  @Test
  public void test2()
  {
    SerialRenderer sr = new SerialRenderer();
    ParallelRenderer pr = new ParallelRenderer();
    sr.append(contact1, 0);
    sr.append(contact2, 0);
    sr.complete();
    pr.append(sr);
    pr.append(contact3);
    pr.complete(0);
    pr.print();
  }

  @Test
  public void test3()
  {
    SerialRenderer sr = new SerialRenderer();
    ParallelRenderer pr = new ParallelRenderer();
    sr.append(contact1, 0);
    sr.append(contact2, 0);
    sr.complete();
    pr.append(contact3);
    pr.append(sr);
    pr.complete(0);
    pr.print();
  }

  @Test
  public void test4()
  {
    SerialRenderer sr2 = new SerialRenderer();
    SerialRenderer sr1 = new SerialRenderer();
    ParallelRenderer pr = new ParallelRenderer();
    sr1.append(contact1, 0);
    sr1.append(contact2, 0);
    sr1.complete();
    sr2.append(contact3, 0);
    sr2.append(contact4, 0);
    sr2.complete();
    pr.append(sr1);
    pr.append(sr2);
    pr.complete(0);
    pr.print();
  }
}
