package org.codehaus.mojo.openjpa.testentity;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class TestEntity {

  @Id
  private int xint1;

  private String string1;

  public TestEntity() {
  }

  public TestEntity(int int1, String string1) {
    this.xint1 = int1;
    this.string1 = string1;
  }

  public int getInt1() {
    return xint1;
  }

  public void setInt1(int int1) {
    this.xint1 = int1;
  }

  public String getString1() {
    return string1;
  }

  public void setString1(String string1) {
    this.string1 = string1;
  }

  public String toString()  {
    return xint1 + ":" + string1;
  }

}
