import java.rmi.Remote;

public interface RemoteClientInterface extends Remote {
    public void confirmCheckIn() throws java.rmi.RemoteException;

    public void notifyCovid() throws java.rmi.RemoteException;

}
