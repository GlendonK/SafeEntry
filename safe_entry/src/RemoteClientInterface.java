import java.rmi.Remote;

public interface RemoteClientInterface extends Remote {
    public void confirmCheckIn(String NRIC, String name, String location, String time) throws java.rmi.RemoteException;

    public void notifyCovid(String location, String from, String to) throws java.rmi.RemoteException;

}
