package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class SorterTest {

  Module module1;
  Module module2;
  Sorter sorter1;

  @Before
  public void initialize() {
    module1 = new Module("class1");
    Input input = new Input();
    input.setPointer(0);
    module1.putInput(input);

    module2 = new Module("class2");
    Output output = new Output();
    output.setPointer(0);
    module2.putOutput(output);

    sorter1 = new Sorter()
      .add(module1)
      .add(module2);
  }

  @Test
  public void test1() {
    System.out.println(sorter1.toString());
    java.util.Iterator<Module> iter = sorter1.iterator();
    System.out.println(sorter1.toString());
    assertEquals(module2, iter.next());
    assertEquals(module1, iter.next());
  }

  @Ignore
  @Test(expected=SyntaxErrorException.class)
  public void test6() {
  }


}
