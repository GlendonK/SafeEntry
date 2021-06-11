
/*
 * Database interface
 * 
 * CSC 3004 lab assignment
 * 
 * author: Glendon Keh 1901884, Aloysius Goh 1902774
 * 
 * submission date: 18 june 2021 
 * 
*/
import java.rmi.Remote;

/**
 * Database interface extends the Remote interface. Database interface is used
 * by both client and server. Client need to know the methods to call and server
 * needs to override the methods.
 */
public interface Database extends Remote {
    public void checkIn(String NRIC, String name, String location) throws java.rmi.RemoteException;

    public void checkOut(String NRIC, String name, String location) throws java.rmi.RemoteException;

    public void updateInfectedLocation(String location, String checkInTime, String checkOutTime)
            throws java.rmi.RemoteException;

    public boolean setRemoteClientState(RemoteClientInterface remote, String NRIC) throws java.rmi.RemoteException;

    public boolean isAlive() throws java.rmi.RemoteException;

}
