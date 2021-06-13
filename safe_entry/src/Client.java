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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Client class is to start the user and officer interaction with the system.
 * Users can check in, check out. Officers can update the location of possible infection.
*/
public class Client extends java.rmi.server.UnicastRemoteObject implements RemoteClientInterface {

    final private int PORT = 1099;                 // port used for rmi
    final private String HOST = "localhost";       // network address of server
    private boolean isAlive = false;
    private boolean isCheckServerThreadRunning = false;

    private static String NRIC;
    private static String name;
    private static String location;

    public Client() throws RemoteException {
        // super();

    }

    /**
     * method to start asking user for input.
     */
    public void runUser() {
        int choose = 0;
        
        try {

            Client client = this;
            final String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
            Database database = (Database) Naming.lookup(rmi);          // look for the address of the server
            
            System.out.println("choose. 1(check in) 2(check in with family) 3(check out) 4(read user history)");
            Scanner scan = new Scanner(System.in);      // cannot close as its constantly being used.
            choose = scan.nextInt();

            
            //TODO: family checkin and checkout

            if (choose == 1) {
                /** 
                 * check in 
                */
                System.out.println("Enter Location: ");
                Scanner scanner = new Scanner(System.in);
                location = scanner.nextLine();

                System.out.println("Enter Your NRIC: ");
                NRIC = scanner.nextLine();

                System.out.println("Enter Your Name");
                name = scanner.nextLine();

                try {
                    database.setRemoteClientState(client, NRIC);        // add client remote object to server state
                    database.checkIn(NRIC, name, location);
                    if (isCheckServerThreadRunning == false) {
                        checkServerThread(database, NRIC);              // starts checking every 5 secs if server alive
                        isCheckServerThreadRunning = true;
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                    System.out.println("\nPlease try check in again.\n");
                }
                //System.out.println("completed checkin");
                //scanner.close();
        
            } else if (choose == 2) {
                List<String> NRICList = new ArrayList<>();
                List<String> nameList = new ArrayList<>();

                System.out.println("location: ");
                Scanner scanner = new Scanner(System.in);
                location = scanner.nextLine();
                

                System.out.println("Number of people: ");
                int pax = scanner.nextInt();

                for (int i = 0; i<=pax; i++) {
                    if (i == 0) {

                        /**
                         * check in the user.
                         * the user who initiate the check in will check out for the familiy.
                         */
                        
                        System.out.println("Your NRIC: ");
                        Scanner scannerUser = new Scanner(System.in);
                        NRIC = scannerUser.nextLine();
                        NRICList.add(NRIC);                

                        System.out.println("Your Name: ");
                        name = scannerUser.nextLine();
                        nameList.add(name);

                        database.setRemoteClientState(client, NRIC);        // add client remote object to server state
                        
                        
                    } else if (i >0 ) {
                        /**
                         * check in family members.
                         */
                        System.out.println("Family member " + i +" NRIC: ");
                        Scanner famScanner = new Scanner(System.in);
                        String famNRIC = famScanner.nextLine();
                        NRICList.add(famNRIC);
                        

                        System.out.println("Family member " + i +" Name: ");
                        String famName = famScanner.nextLine();
                        nameList.add(famName);

                    }

                }
                database.familyCheckIn(NRICList, nameList, location);

                if (isCheckServerThreadRunning == false) {
                    checkServerThread(database, NRIC);              // starts checking every 5 secs if server alive
                    isCheckServerThreadRunning = true;
                }

                // paxInput.close();
                // locaInput.close();
                //scanner.close();
                
            } else if (choose == 3) {
                /** 
                 * check out
                */
                database.checkOut(NRIC, name, location.toLowerCase());
            } else if (choose == 4) {
                /**
                 * read client data
                 */
                database.readUserOnly(NRIC);
            }
            //scan.close();

        } catch (MalformedURLException urle) {
            urle.printStackTrace();
        } catch (RemoteException re) {
            System.out.println(re);
            System.out.println("\nRetrying...\n");
            /**
             * If check out fail due to server failure, will try to invoke remote check out method again
             * until the server comes back alive. Then add the client remote object to server state.
             */
            if (choose == 3) {
                try {
                    String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
                    Database database = (Database) Naming.lookup(rmi);
                    database.setRemoteClientState(this, NRIC);
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

    public void runOfficer() {
        int choose = 0;
        try {
            
            final String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
            Database database = (Database) Naming.lookup(rmi);          // look for the address of the server

            System.out.println("choose. 1(set location and time of infected), 2(read all entries)");
            Scanner scan = new Scanner(System.in);          // cant close this as it needs to run constantly in while loop.
            choose = scan.nextInt();

            if (choose == 1) {
                /** 
                 * For officer to update covid location.
                 */

                System.out.println("Input location: ");
                Scanner officerInput = new Scanner(System.in);
                String officerLoc = officerInput.nextLine();

                final String timeExample = LocalDateTime.now().toString();
                System.out.println("eg: " + timeExample);
                System.out.println("Input start time (yyy-mm-ddThh:mm:ss): ");
                String startTme = officerInput.nextLine();

                System.out.println("Input end time (yyy-mm-ddThh:mm:ss): ");
                String endTime = officerInput.nextLine();

                
                database.updateInfectedLocation(officerLoc.toLowerCase(), startTme, endTime);
                System.out.println("completed update");
            } else if (choose == 2) {
                /**
                 * for officer to read all database entries.
                 */
                database.readAll(this);
            }

        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
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
    public void checkServer(Database database, String NRIC) {

        while(true){
            try {
                //System.out.println("check server thread: " + Thread.currentThread().getName());
                if (database.isAlive() != true) {
                    isAlive = false;
                    //Thread.sleep(5000);
                    
                } else if(database.isAlive() == true) {
                    isAlive = true;
                    //System.out.println("Server is Alive");
                    Thread.sleep(5000);
                }
            } catch (RemoteException e) {
                /**
                 * try lookup rmi url again and restore state.
                 * recursively call checkServer method to keep trying.
                 */
                System.out.println("Server is Dead");
                try {
                    String rmi = "rmi://" + HOST + ":" + PORT + "/database";    // server binded to address ending with /database
                    database = (Database) Naming.lookup(rmi);
                    database.setRemoteClientState(this, NRIC);
                    checkServer(database, NRIC);    
                } catch (RemoteException re) {
                    re.printStackTrace();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (NotBoundException e1) {
                    e1.printStackTrace();
                }
                
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            
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
