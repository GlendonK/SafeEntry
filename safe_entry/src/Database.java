
public interface Database extends java.rmi.Remote {
    public void checkIn(String NRIC, String name, String location, String rmi)throws java.rmi.RemoteException;

    public void checkOut()throws java.rmi.RemoteException;
}
