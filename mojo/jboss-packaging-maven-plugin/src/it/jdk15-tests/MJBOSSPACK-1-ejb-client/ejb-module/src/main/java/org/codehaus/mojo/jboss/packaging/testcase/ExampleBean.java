package org.codehaus.mojo.jboss.packaging.testcase;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(Example.class)
public class ExampleBean implements Example {
  public void foo () {

  }
}