import java.rmi.RemoteException;

public class User {
    
    public static void main(String[] args) {
        try {
            while (true) {
                new Client().runUser();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
