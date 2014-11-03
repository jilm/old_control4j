package control4j.application;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import java.util.List;

public class SignalManagerTest
{

  public Scope scope1;
  public Scope scope2;
  public Scope scope3;
  public SignalDeclaration signal1;
  public SignalDeclaration signal2;
  public SignalDeclaration signal3;

  @Before
  public void initialize()
  {
    scope1 = new Scope();
    scope2 = new Scope(scope1);
    scope3 = new Scope();
    signal1 = new SignalDeclaration(scope1, "signal");
    signal3 = new SignalDeclaration(scope3, "signal");
    SignalManager.getInstance().add(signal1);
    SignalManager.getInstance().add(signal3);
  }

  @Test
  public void get1()
  {
    SignalDeclaration signal = SignalManager.getInstance().get(scope1, "signal");
    assertEquals(signal1, signal);
  }

  @Test
  public void get2()
  {
    SignalDeclaration signal = SignalManager.getInstance().get(scope2, "signal");
    assertEquals(signal1, signal);
  }

  @Test
  public void get3()
  {
    SignalDeclaration signal = SignalManager.getInstance().get(scope3, "signal");
    assertEquals(signal3, signal);
  }

}
