import java.rmi.RemoteException;

public class App 
{
    public static void main(String[] args) {
        try {
            new Client();
        } catch (RemoteException e) {
       e.printStackTrace();
        }
    }
}
