package org.codehaus.mojo.smc.test;

/**
 * Just to check that the generated code works
 */
public class Test
{ 
  TestContext fsm = new TestContext(this);

  Test()
  {
    System.out.println(fsm.getState());
    fsm.Start();
    System.out.println(fsm.getState());
  }

  public static void main(String[] args) {
    new Test();
  }
}
