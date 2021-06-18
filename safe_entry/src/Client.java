/*
 * SafeEntryDatabase
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
import java.util.List;

/**
 * Client class is to start the user and officer interaction with the system.
 * Users can check in, check out. Officers can update the location of possible infection.
*/
public class Client extends java.rmi.server.UnicastRemoteObject implements RemoteClientInterface {

    final private int PORT = 1099;                 // port used for rmi
    final private String HOST = "192.168.43.79";       // network address of server
    private boolean isAlive = false;
    private boolean isCheckServerThreadRunning = false;

    private Database database;

    private Client client = this;

    public Client() throws RemoteException {
        // super();
        final String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
        try {
            database = (Database) Naming.lookup(rmi);// look for the address of the server
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }          
            

    }

    /**
     * check in for individual user.
     * @param location
     * @param NRIC
     * @param name
     */
    public void userCheckIn(String location, String NRIC, String name) {
        /** 
         * check in 
        */
        try {
            database.setRemoteClientState(client, NRIC);        // add client remote object to server state
            database.checkIn(NRIC, name, location);
            if (isCheckServerThreadRunning == false) {
                checkServerThread(database, NRIC);              // starts checking every 5 secs if server alive
            }
        } catch (RemoteException re) {
            re.printStackTrace();
            System.out.println("\nPlease try check in again.\n");
        }
    }

    /**
     * check in for user family.
     * @param location location is same for all family members.
     * @param pax number of family member excluding user.
     * @param NRICList list of user and user family nric. user nric is the first in list.
     * @param nameList list of user and user family name. user name is the first in list.
     */
    public void userFamCheckIn(String location, int pax, List<String> NRICList, List<String> nameList ) {
        
        try {
            database.setRemoteClientState(this, NRICList.get(0));        // add client remote object to server state
            database.familyCheckIn(NRICList, nameList, location);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (isCheckServerThreadRunning == false) {
            checkServerThread(database, NRICList.get(0));              // starts checking every 5 secs if server alive
        }
    }

    /**
     * check out for inidvidual user.
     * family checkout also use this method.
     * @param NRIC for family checkout nric is the user's nric.
     * @param name for family checkout name is the user's name.
     * @param location 
     */
    public void userCheckOut(String NRIC, String name, String location){
        /** 
         * check out
        */
        try {
            database.checkOut(NRIC, name, location.toLowerCase());
        } catch (RemoteException e) {
            System.out.println(e);
            System.out.println("\nRetrying...\n");
            /**
             * If check out fail due to server failure, will try to invoke remote check out method again
             * until the server comes back alive. Then add the client remote object to server state.
             */
            retry(NRIC);
                
        }
    }

    /**
     * read from database, only entries matching user nric and user's family member.
     * will trigger a callback. 
     * @param NRIC
     */
    public void userRead(String NRIC) {
        /**
         * read client data
         */
        try {
            database.readUserOnly(NRIC);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * for officer to set the location and time of possible infection.
     * @param officerLoc
     * @param startTime time infected person enter lcoation.
     * @param endTime time infected person exit location.
     */
    public void officerUpdateInfectedLocation(String officerLoc, String startTime, String endTime) {
        /** 
         * For officer to update covid location.
         */
        try {
            database.updateInfectedLocation(officerLoc.toLowerCase(), startTime, endTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("completed update");
    }
    
    /**
     * for officer to get all entries of the database.
     */
    public void officerRead() {
        /**
         * for officer to read all database entries.
         */
        try {
            database.readAll(this);
        } catch (RemoteException e) {
            e.printStackTrace();
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
    public void notifyCovid(String NRIC, String location, String from, String to) throws RemoteException {
        System.out.println("\n"+ NRIC + " Possible Exposure at " + location + " from " + from + " to " + to);
        System.out.println("\nPlease pay attention to your heath for 14 days from " + from + ". Please see a doctor if feeling ill\n");

    }

    /**
     * method to run a thread checkServer method.
     * @param database the remote object to invoke.
     * @param NRIC String NRIC of user.
     */
    private void checkServerThread(Database database, String NRIC) {
        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                isCheckServerThreadRunning = true;
                //System.out.println("\nA new check server thread: " + Thread.currentThread().getName() +"\n");
                checkServer(database, NRIC);                
            }
            
        });
        thread.start();
        
    }

    /**
     * method to check every 5 secs for server status. If server not alive, keep trying to 
     * look up for server rmi url in the rmi registry. When server is alive again, send this class's
     * instance to server to restore state. 
     * @param database the remote object to invoke.
     * @param NRIC String NRIC of user.
     */
    private void checkServer(Database database, String NRIC) {

        while(true){
            try {
                //System.out.println("check server thread: " + Thread.currentThread().getName());
                if(database.isAlive() == true) {
                    isAlive = true;
                    //System.out.println("Server is Alive");
                    Thread.sleep(5000);
                }
            } catch (RemoteException e) {
                /**
                 * try lookup rmi url again and restore state.
                 * keep trying.
                 */
                isAlive = false;
                System.out.println("Server is Dead");
                retry(NRIC);
                
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            
        }
        
    }

    /**
     * retry connection to server.
     * @param NRIC
     */
    private void retry(String NRIC) {
        try {
            final String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
            database = (Database) Naming.lookup(rmi);
            database.setRemoteClientState(client, NRIC);        // set state on server side invoked remote object
        } catch (RemoteException re) {
            System.out.print("Trying to reconnect to server...");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (NotBoundException e1) {
            e1.printStackTrace();
        }
    }
    
    /**
     * call back function to print all the database entries.
     * @param List<String[]>
     */
    @Override
    public void read(List<String[]> data) throws RemoteException {
        for (String[] row : data) {
            System.out.println(" | " + row[0] + " | " + row[1] + " | " + row[2] + " | " + row[3] + " | " + row[4] + " | " + row[5] + " | ");

        }
        
    }
    
    /**
     * call back function to print only user's entries from the database entries.
     * @param List<String[]>
     */
    @Override
    public void readClient(List<String[]> data) throws RemoteException {
        for (String[] row : data) {
            System.out.println(" | " + row[0] + " | " + row[1] + " | " + row[2] + " | " + row[3] + " | " + row[4] + " | " + row[5] + " | ");
        }        
    }
    
}
