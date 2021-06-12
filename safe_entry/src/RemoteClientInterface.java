/*
 * RemoteClientInterface
 * 
 * CSC 3004 lab assignment
 * 
 * author: Glendon Keh 1901884, Aloysius Goh 1902774
 * 
 * submission date: 18 june 2021 
 * 
*/

/**
 * RemoteClientInterface is used for server to callback to client.
 */
import java.rmi.Remote;
import java.util.List;

public interface RemoteClientInterface extends Remote {
    
    public void confirmCheckIn(String NRIC, String name, String location, String time) throws java.rmi.RemoteException;

    public void confirmCheckOut(String NRIC, String name, String location, String time) throws java.rmi.RemoteException;

    public void notifyCovid(String location, String from, String to) throws java.rmi.RemoteException;

    public void read(List<String[]> data) throws java.rmi.RemoteException;

    public void readClient(List<String[]> data) throws java.rmi.RemoteException;

}
