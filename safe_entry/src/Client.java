/*
 * Client
 * 
 * CSC 3004 lab assignment
 * 
 * author: Glendon Keh 1901884, Aloysius Goh 1902774
 * 
 * submission date: 18 june 2021 
 * 
*/


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Client class is to start the user and officer interaction with the system.
 * Users can check in, check out. Officers can update the location of possible infection.
*/
public class Client extends java.rmi.server.UnicastRemoteObject implements RemoteClientInterface {

    static int port = 1099;                 // port used for rmi
    static String host = "localhost";       // network address of server

    public Client() throws RemoteException {
        // super();

    }

    /**
     * method to start asking user for input.
     */
    public void run() {
        try {

            Client client = new Client();
            String rmi = "rmi://" + host + ":" + port + "/database";    // server binded to address ending with /database
            Database database = (Database) Naming.lookup(rmi);          // look for the address of the server
            int choose = 0;

            System.out.println("choose. 1(check in) 2(check out) 3(update)");
            Scanner scan = new Scanner(System.in);          // cant close this as it needs to run constantly in while loop.
            choose = scan.nextInt();

            // * !TODO: family checkin and checkout */

            if (choose == 1) {
                /** 
                 * check in 
                */
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            database.checkIn("S1234567B", "Bob", "nyp", client);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                        }
                        System.out.println("completed checkin");

                    }

                });
                thread.start();

            } else if (choose == 2) {
                /** 
                 * check out
                */
                database.checkOut("S1234567B", "Bob", "nyp");
                System.out.println("completed checkout");
            } else if (choose == 3) {
                /** 
                 * For officer to update covid location
                */
                database.updateInfectedLocation("nyp", "2021-06-07T00:52:52.034223", "2021-06-10T01:52:52.034223");
                System.out.println("completed update");
            }

        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }
    }

    /** 
    * Callback functions 
    */

    /**
     * The Client class will call this method when server does a callback using this method.
     * This method is to provide a feedback to client that check in is successful.
     * @param NRIC the String of the NRIC of the user and is also used as a unique id for the server and database.
     * @param name the String of the name of the user.
     * @param location the String of the location the user is checking in from.
     * @param time the String of the time the user checked in. yyyy-MM-dd'T'HH:mm:ss format.  
     * @throws RemoteException
    */ 
    @Override
    public void confirmCheckIn(String NRIC, String name, String location, String time) throws RemoteException {
        System.out.println("Checked In: " + NRIC + " " + name + " " + location + " at " + time);

    }

    /**
     * The Client class will call this method when server does a callback using this method.
     * This method is to provide a feedback to client that check out is successful.
     * @param NRIC the String of the NRIC of the user and is also used as a unique id for the server and database.
     * @param name the String of the name of the user.
     * @param location the String of the location the user is checking out from.
     * @param time the String of the time the user checked out.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException  
    */ 
    @Override
    public void confirmCheckOut(String NRIC, String name, String location, String time) throws RemoteException {
        System.out.println("Checked Out: " + NRIC + " " + name + " " + location + " at " + time);

    }

    /**
     * The Client class will call this method when server does a callback using this method.
     * This method is to notify the user that he/she had been to a place where a infected 
     * person had been to in the same timeframe.
     * @param location the String of the location that the infected person was.
     * @param from the String of the time the infected person checked in to the location. 'yyyy-MM-dd'T'HH:mm:ss' format.
     * @param to the String of the time the infected person checked out of the location. 'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    @Override
    public void notifyCovid(String location, String from, String to) throws RemoteException {
        System.out.println("Possible Exposure at " + location + " from " + from + " to " + to);

    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            while (true) {
                client.run();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
