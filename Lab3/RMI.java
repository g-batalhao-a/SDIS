import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote {
    String respond(String received) throws RemoteException;
}
