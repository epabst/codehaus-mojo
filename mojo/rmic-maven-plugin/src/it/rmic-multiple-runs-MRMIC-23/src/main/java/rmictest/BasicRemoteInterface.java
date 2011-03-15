package rmictest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BasicRemoteInterface extends Remote
{
    public String sayHello() throws RemoteException;
}
