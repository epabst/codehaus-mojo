package com.acme;

public class HelloWorldService implements HelloWorldServiceMBean
{
   // Our message attribute
   private String message = "Sorry no message today";

   // Getters and Setters
   public String getMessage()
   {
      return message;
   }
   
   public void setMessage(String message)
   {
      this.message = message;
   }

   // The printMessage operation
   public void printMessage()
   {
      System.out.println(message);
   }

   // The lifecycle
   public void start() throws Exception
   {
      System.out.println("Starting with message=" + message);
   }
   
   public void stop()
   {
      System.out.println("Stopping with message=" + message);
   }
}
