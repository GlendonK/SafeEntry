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


import java.net.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Client class is to start the user and officer interaction with the system.
 * Users can check in, check out. Officers can update the location of possible infection.
*/
public class Client extends java.rmi.server.UnicastRemoteObject implements RemoteClientInterface {

    final int PORT = 1099;                 // port used for rmi
    final String HOST = "localhost";       // network address of server
    private boolean serverAlive = false;

    public Client() throws RemoteException {
        // super();

    }

    /**
     * method to start asking user for input.
     */
    public void run() {
        int choose = 0;
        String NRIC = "S1234567A";
        String name = "Alice";
        String location = "NYP";
        try {

            Client client = new Client();
            final String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
            Database database = (Database) Naming.lookup(rmi);          // look for the address of the server
            
            System.out.println("choose. 1(check in) 2(check out) 3(update)");
            Scanner scan = new Scanner(System.in);          // cant close this as it needs to run constantly in while loop.
            choose = scan.nextInt();

            

            //TODO: family checkin and checkout

            if (choose == 1) {
                /** 
                 * check in 
                */
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            database.setRemoteClientState(client, NRIC);        // add client remote object to server state
                            database.checkIn(NRIC, name, location, client);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                        }
                        //System.out.println("completed checkin");

                    }

                });
                thread.start();

            } else if (choose == 2) {
                /** 
                 * check out
                */
                database.checkOut(NRIC, name, location.toLowerCase());
                //System.out.println("completed checkout");
                
                
            } else if (choose == 3) {
                
                /** 
                 * For officer to update covid location
                 * TODO: set location to lowercase.
                 */
                database.updateInfectedLocation("NYP", "2021-06-07T00:52:52.034223", "2021-06-15T01:52:52.034223");
                System.out.println("completed update");
            }

        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            System.out.println(re);
            System.out.println("\nRetrying...\n");
            /**
             * If check out fail due to server failure, will try to invoke remote check out method again
             * until the server comes back alive. Then add the client remote object to server state.
             */
            if (choose == 2) {
                try {
                    Client client = new Client();
                    String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
                    Database database = (Database) Naming.lookup(rmi);
                    database.setRemoteClientState(client, NRIC);
                } catch (RemoteException e) {
                    System.out.println("\nretry failed, please check out again\n");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
                
            }

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
