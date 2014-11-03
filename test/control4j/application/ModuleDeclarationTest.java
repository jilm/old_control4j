package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.List;

public class ModuleDeclarationTest
{

  public ModuleDeclaration module1;
  public ModuleDeclaration module2;
  public ModuleDeclaration module3;

  public Input input1;
  public Input input2;
  public Input input3;
  public Input input4;
  public Input input5;

  public Scope scope = new Scope();

  @Before
  public void initialize()
  {
    input1 = new Input(scope, "input1");
    input1.setIndex(0);
    input2 = new Input(scope, "input2");
    input2.setIndex(1);
    input3 = new Input(scope, "input3");
    input3.setIndex(5);
    input4 = new Input(scope, "input4");
    input4.setIndex(-1);
    input5 = new Input(scope, "input5");
    input5.setIndex(5);

    module1 = new ModuleDeclaration("className");    
    module1.complete();

    module2 = new ModuleDeclaration("className");
    module2.addInput(input2);
    module2.complete();

    module3 = new ModuleDeclaration("className");
    module3.addInput(input2);
    module3.addInput(input3);
    module3.addInput(input4);
    module3.addInput(input1);
    module3.complete();
  }

  @Test
  public void getInputsSize1()
  {
    assertEquals(0, module1.getInputsSize());
  }

  @Test
  public void getInputsSize2()
  {
    assertEquals(2, module2.getInputsSize());
  }

  @Test
  public void getInputsSize3()
  {
    assertEquals(7, module3.getInputsSize());
  }

  @Test
  public void addInput1()
  {
    module1.addInput(input1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void getInput1()
  {
    module1.getInput(0);
  }

  @Test
  public void getInput2()
  {
    assertTrue(module2.getInput(1) == input2);
  }

  @Test
  public void getInput3()
  {
    assertNull(module2.getInput(0));
  }

  @Test
  public void getInput4()
  {
    Input input = module3.getInput(6);
    assertTrue(input == input4);
  }

  @Test
  public void getInput5()
  {
    assertTrue(module3.getInput(0) == input1);
  }

}

