
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client extends java.rmi.server.UnicastRemoteObject implements RemoteClientInterface{

    static int port = 1099;
    static String host = "localhost";

    public Client() throws RemoteException {
        // super();
        try {
            String rmi = "rmi://" + host + ":" + port + "/database";
            Database database = (Database) Naming.lookup(rmi);
            int choose = 0;

            
                System.out.println("choose. 1(check in) 2(check out) 3(update)");
                Scanner scan = new Scanner(System.in);
                choose = scan.nextInt();

                //** open threads for each action; while loop will listen for inputs */
                if (choose == 1 ) {
                    database.checkIn("S1234567B", "Alice", "nyp", this);
                    System.out.println("completed checkin");
                } else if (choose == 2) {
                    database.checkOut("S1234567B", "Alice", "nyp");
                    System.out.println("completed checkout");
                } else if (choose == 3) {
                    database.updateInfectedLocation("nyp", "2021-06-07T00:52:52.034223", "2021-06-08T01:52:52.034223");
                    System.out.println("completed update");
                }
                scan.close();
            
            //database.setRemoteClient(this, "S1234567A");

            // database.checkIn("S1234567B", "Alice", "nyp", this);
            // database.read();
            //database.updateInfectedLocation("nyp", "2021-06-07T00:52:52.034223", "2021-06-08T01:52:52.034223");
            //database.checkOut("S1234567B", "Alice", "nyp");

            
            //System.exit(0);
            //return;
        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }
    }

    @Override
    public void confirmCheckIn() throws RemoteException {
        System.out.println("Checked In.");
        
    }

    @Override
    public void notifyCovid() throws RemoteException {
        System.out.println("Infected");
        
    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    

}
