
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client extends java.rmi.server.UnicastRemoteObject {

    static int port = 1099;
    static String host = "localhost";

    public Client() throws RemoteException {
        // super();
        try {
            String rmi = "rmi://" + host + ":" + port + "/database";
            Database database = (Database) Naming.lookup(rmi);
            
            // database.checkIn("S1234567A", "Lim", "nyp", rmi);
            // database.read();
            //database.updateInfectedLocation("nyp", "2021-06-07T00:52:52.034223", "2021-06-07T01:52:52.034223");
            database.checkOut("S1234567B", "Tan", "nyp");

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
