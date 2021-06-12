import java.rmi.RemoteException;

public class Officer {
    public static void main(String[] args) {
        try {
            while (true) {
                new Client().runOfficer();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
