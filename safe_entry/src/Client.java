
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client extends java.rmi.server.UnicastRemoteObject {

    static int port = 1099;
    static String host ="localhost";

    public Client() throws RemoteException {
        //super();
        try {
            String rmi = "rmi://"+host+":"+port+"/database";
            Database database = (Database)Naming.lookup(rmi);
            //database.checkIn("NRIC", "name", "location", rmi);
            database.read();

            System.out.println("completed");
            System.exit(0);
            return;
        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (RemoteException e) {
       e.printStackTrace();
        }
    }
    

    
}
