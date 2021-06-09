
public interface Database extends java.rmi.Remote {
    public void checkIn(String NRIC, String name, String location, RemoteClientInterface remote)throws java.rmi.RemoteException;

    public void checkOut(String NRIC, String name, String location)throws java.rmi.RemoteException;

    public void read()throws java.rmi.RemoteException;

    public void updateInfectedLocation(String location, String checkInTime, String checkOutTime)throws java.rmi.RemoteException;

    public void delete()throws java.rmi.RemoteException;

    public void setRemoteClient(RemoteClientInterface remote, String NRIC, String name, String location, String time)throws java.rmi.RemoteException;

    public void notifyClient(String NRIC, String location, String from, String to)throws java.rmi.RemoteException;
}
