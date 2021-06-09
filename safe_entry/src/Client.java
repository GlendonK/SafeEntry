
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

                //* !TODO: open threads for each action; while loop will listen for inputs 
                //* calling new functions should not register a new client obj ?
                //* since its still the same obj no new instance instanciated  
                
                if (choose == 1 ) {
                    database.checkIn("S1234567B", "Bob", "nyp", this);
                    System.out.println("completed checkin");
                } else if (choose == 2) {
                    database.checkOut("S1234567B", "Bob", "nyp");
                    System.out.println("completed checkout");
                } else if (choose == 3) {
                    database.updateInfectedLocation("nyp", "2021-06-07T00:52:52.034223", "2021-06-10T01:52:52.034223");
                    System.out.println("completed update");
                }
                scan.close();
            
        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }
    }


    //** Callback functions */
    @Override
    public void confirmCheckIn(String NRIC, String name, String location, String time) throws RemoteException {
        System.out.println("Checked In: " + NRIC + " " + name +" " + location + " at " + time);
        
    }

    @Override
    public void confirmCheckOut(String NRIC, String name, String location, String time) throws RemoteException {
        System.out.println("Checked Out: " + NRIC + " " + name +" " + location + " at " + time);
        
    }

    @Override
    public void notifyCovid(String location, String from, String to) throws RemoteException {
        System.out.println("Possible Exposure at " + location + " from " + from + " to " + to );
        
    }

    public static void main(String[] args) {
        try {
            new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
