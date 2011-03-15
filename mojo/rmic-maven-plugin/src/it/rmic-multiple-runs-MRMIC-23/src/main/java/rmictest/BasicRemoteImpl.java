package rmictest;

import java.rmi.server.UnicastRemoteObject;

public class BasicRemoteImpl
    extends UnicastRemoteObject
    implements BasicRemoteInterface
{
    public BasicRemoteImpl()
        throws java.rmi.RemoteException
    {
        super();
    }

    public String sayHello()
    {
        return "hello";
    }
}
