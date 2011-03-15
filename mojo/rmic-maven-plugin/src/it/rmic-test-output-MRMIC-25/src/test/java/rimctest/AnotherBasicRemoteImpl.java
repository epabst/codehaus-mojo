package rmictest;

import java.rmi.server.UnicastRemoteObject;

public class AnotherBasicRemoteImpl
    extends UnicastRemoteObject
    implements BasicRemoteInterface
{
    public AnotherBasicRemoteImpl()
        throws java.rmi.RemoteException
    {
        super();
    }

    public String sayHello()
    {
        return "hello";
    }
}
