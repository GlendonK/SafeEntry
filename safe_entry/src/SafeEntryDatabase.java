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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * SafeEntryDatabase extends UnicastRemoteObject and implements Database.
 * UnicastRemoteObject allows for the remote object to be invoked by the client.
 * Database provides the methods to override.
 * SafeEntryDatabase contains the business logic of the application and database access.
 */
public class SafeEntryDatabase extends java.rmi.server.UnicastRemoteObject implements Database {
    
    /* used to save the client invoked object */
    private static HashMap<String, RemoteClientInterface> clientRemoteObjState = new HashMap<String, RemoteClientInterface>();
    private static Semaphore mutex = new Semaphore(1);          // semophore to prevent 2 threads writing the database csv.
    private final String CSV_PATH = "safe_entry_db.csv";        // the database

    

    public SafeEntryDatabase() throws java.rmi.RemoteException {
        // super();
    
    }

    /**
     * checkIn method is called by client and will write the client check in details into the database.
     * the remote invoked object by the client is also saved to a static class variable `clientRemoteObjState`.
     * @param NRIC String client NRIC.
     * @param name String client name.
     * @param location String client loction of checking in.
     * @param remote RemoteClientInterface invoked object by client.
     */
    @Override
    public void checkIn(String NRIC, String name, String location) {

        final String time = LocalDateTime.now().toString();

        /**
         * code to get the ip of client.
         * code to get the remote object reference
         */
        // String clientObj;
        // Remote remote;

        // try {
        //     clientObj = RemoteServer.getClientHost();
        //     remote = Naming.lookup("//localhost/database");
            
        // } catch (ServerNotActiveException e1) {
        //     e1.printStackTrace();
        //     clientObj="";
        // } catch (MalformedURLException e) {
        //     clientObj="";
        //     e.printStackTrace();
        // } catch (RemoteException e) {
        //     clientObj="";
        //     e.printStackTrace();
        // } catch (NotBoundException e) {
        //     clientObj="";
        //     e.printStackTrace();
        // }

        final String line1[] = { NRIC, name, time, "", location.toLowerCase(), "not infected", NRIC };      // default everyone is not infected.

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mutex.acquire();        // only threads holding the semaphore can write to database.
                    System.out.println("mutex aquired");
                    
                    System.out.println("Checking In " + NRIC + " " + name + " at " + location);
                    CSVWriter writer = new CSVWriter(
                            new FileWriter(CSV_PATH, true));
                    System.out.println(Thread.currentThread().getName());
                    

                    /* test semaphore */
                    // for (int i = 0; i < 10; i++) {
                    //     writer.writeNext(line1);
                    //     Thread.sleep(1000);
                    // }

                    writer.writeNext(line1);    // write the data to the next line
                    writer.close();
                    notifyCheckIn(NRIC, NRIC, name, location.toLowerCase(), time);
                    return;

                } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();        // release the semaphore for other threads to use.
                }

            }

        });
        thread.setName("\nCheck In Thread\n");
        thread.start();
        try {
            thread.join();      // wait for thread to end.
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return;

    }

    /**
     * method to check in with additional users/family.
     * @param NRICList the list of nric of all the users, the first nric in the list will check in for all. 
     * @param name the list of name od the users, first of the list is the user name who checks in for all.
     * @param location the location of check in.
     * @throws RemoteExceptin
     */
    @Override
    public void familyCheckIn(List<String> NRICList, List<String> name, String location) throws RemoteException {
        final String time = LocalDateTime.now().toString();

        List<String[]> familyList = new ArrayList<>();
        for (int i = 0; i<NRICList.size(); i++) {
            String line1[] = { NRICList.get(i), name.get(i), time, "", location.toLowerCase(), "not infected", NRICList.get(0) };      // default everyone is not infected.
            familyList.add(line1);
        }

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    mutex.acquire();        // only threads holding the semaphore can write to database.
                    System.out.println("mutex aquired");
                    
                    CSVWriter writer = new CSVWriter(
                            new FileWriter(CSV_PATH, true));
                    System.out.println(Thread.currentThread().getName());

                    for (int i = 0; i<familyList.size(); i++) {
                        writer.writeNext(familyList.get(i));    // write the data to the next line   
                    }
                    writer.close();

                    /** callback to confirm the check in */
                    for (int i = 0; i<familyList.size(); i++) {
                        System.out.println("NRIC!: "+i+" "+familyList.get(i)[0]);
                        System.out.println("NAME!: "+familyList.get(i)[1]);
                        
                        notifyCheckIn(familyList.get(0)[0], familyList.get(i)[0], familyList.get(i)[1], location.toLowerCase(), time);
                    }

                } catch (InterruptedException e) {
                    System.out.println("Family check in intterupted");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                }
                
            }
            
        });
        thread.setName("\nFamily Check In Thread\n");
        thread.start();
        try {
            thread.join();      // wait for thread to end.
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return;
        

        
    }

    /**
     * checkOut method is called by client to write the check out time to database.
     * works with family checkout.
     * @param NRIC String of client NRIC.
     * @param name String of client name.
     * @param location String of client checkout location.
     */
    @Override
    public void checkOut(String NRIC, String name, String location) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    mutex.acquire();

                    System.out.println("Checking Out");

                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    int i = 0;

                    /**
                     * check if NRIC, name and location then write the check out time.
                     */
                    for (String[] row : allData) {
                            System.out.println(NRIC);
                                System.out.println(name);
                                if (row[4].equals(location)) {
                                    System.out.println(location);
                                    if (row[6].equals(NRIC)) {      // checks the person NRIC who checks in for him/her.
                                        if (row[3].equals("") || row[3].equals(null)) {
                                            row[3] = LocalDateTime.now().toString();
    
                                            CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH));
                                            writer.writeAll(allData);
                                            writer.flush();
                                            writer.close();
                                            notifyCheckOut(NRIC, row[0], row[1], location, row[3]);
                                            System.out.println("Checked out" + row[0] + " " + row[1] + " at " + location
                                                    + " at " + row[3]);
                                            System.out.println(Thread.currentThread().getName());
    
                                        }
                                    } 
                                }
                        i++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                }

            }

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {

            System.out.println(e);
        }

        return;

    }

    /**
     * updateInfectedLocation is called by officer to notify all user if they had contact with 
     * an infected individual.
     * @param location String locations that the infected had been to.
     * @param checkInTime String the check in time by the infected. 'yyyy-MM-dd'T'HH:mm:ss' format.
     * @param checkOutTime String the check out time by the infected. 'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    @Override
    public void updateInfectedLocation(String location, String checkInTime, String checkOutTime)
            throws RemoteException {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mutex.acquire();
                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    int i = 0;

                    /**
                     * check if database is not empty.
                     * check all database entries that match the infected's loaction and timeframe, then 
                     * write the status to "infected" and do a callback to notify the affected users.
                     */
                    if (allData.size() > 0) {

                        for (String[] row : allData) {
                            // System.out.println(row[4]+" .....");
                            if (row[4].toString().contains(location.toLowerCase())) {
                                // System.out.println(row[4]);
                                long infectedCheckIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(checkInTime)
                                        .getTime();
                                long infectedCheckout = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                        .parse(checkOutTime).getTime();
                                long checkIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(row[2]).getTime();
                                long checkOut = 0;

                                if (!row[3].isEmpty()) {
                                    // System.out.println("THE CHECK OUT TIME: "+row[3].toString());
                                    checkOut = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(row[3]).getTime();
                                }

                                if (checkIn >= infectedCheckIn || checkOut >= infectedCheckIn
                                        || checkIn <= infectedCheckout) {

                                    row[5] = "infected";
                                    CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH));
                                    writer.writeAll(allData);
                                    writer.flush();
                                    writer.close();

                                    System.out.println("Affected User: " + row[0] + " " + row[1] + " at " + row[4]
                                            + " from " + row[2] + " to " + row[3]);

                                    // ** callback here */
                                    notifyClient(row[6], row[0], location.toLowerCase(), checkInTime, checkOutTime);

                                }
                            }
                            i++;
                        }

                    } else if (allData.size() < 1) {
                        System.out.println("NO DATA");
                    }

                    csvReader.close();

                } catch (IOException e) {
                    System.out.println(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                }
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        return;

    }

    /**
     * For client to see all of database.
     * This method will have a callback to client with all the databse entries.
     * @TODO: pretty print this
     */
    @Override
    public void readAll(RemoteClientInterface remote) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    List<String[]> databaseEntries = new ArrayList<>();

                    for (String[] col : allData) {

                        // System.out.println(
                        //         col[0] + ", " + col[1] + ", " + col[2] + ", " + col[3] + ", " + col[4] + ", " + col[5]);

                        String[] row = {col[0], col[1], col[2], col[3], col[4], col[5]};

                        databaseEntries.add(row);

                    }

                    csvReader.close();
                    remote.read(databaseEntries);

                } catch (IOException e) {
                    System.out.println(e);
                } catch (CsvException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();

        return;

    }

    @Override
    public void readUserOnly(String NRIC) throws RemoteException {
        
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    List<String[]> databaseEntries = new ArrayList<>();

                    for (String[] col : allData) {

                        // System.out.println(
                        //         col[0] + ", " + col[1] + ", " + col[2] + ", " + col[3] + ", " + col[4] + ", " + col[5]);

                        if (col[6].equals(NRIC)) {
                            String[] row = {col[0], col[1], col[2], col[3], col[4], col[5]};

                            databaseEntries.add(row);
                        }

                    }

                    csvReader.close();
                    SafeEntryDatabase.clientRemoteObjState.get(NRIC).readClient(databaseEntries);

                } catch (IOException e) {
                    System.out.println(e);
                } catch (CsvException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();

        return;
    }

    /**
     * isAlive remote method for client to check server is alive.
     * @return true to indicate server is alive.
     */
    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    /**
     * setRemoteClient method is used to save the invoked object by clients
     * @param remote RemoteClientInterface invoked object by client.
     * @param NRIC String NRIC of client.
     */
    @Override
    public boolean setRemoteClientState(RemoteClientInterface remote, String NRIC) {
        SafeEntryDatabase.clientRemoteObjState.put(NRIC, remote);
        return true;
        
    }

    /**
     * notifyCheckIn method callback to give feedback to client that check in is successful.
     * @param NRIC String NRIC of client.
     * @param name String name of client.
     * @param location String location of check in.
     * @param time String time of check in.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    private void notifyCheckIn(String NRICKey, String NRIC, String name, String location, String time) {
        try {
            SafeEntryDatabase.clientRemoteObjState.get(NRICKey).confirmCheckIn(NRIC, name, location, time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * notifyClient method nottify the client if he/she has been exposed to infect individuals.
     * @param NRIC String NRIC of client.
     * @param location String location of infected place.
     * @param from String time of infected check in.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @param to String time of infected check out.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    private void notifyClient(String NRICKey, String NRIC, String location, String from, String to) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRICKey).notifyCovid(NRIC, location, from, to);
        return;

    }

    /**
     * notifyCheckOut method callback to give feedback to client that check out is successful.
     * @param NRIC String NRIC of client.
     * @param name String name of client.
     * @param location String location of check out.
     * @param time String time of check out.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    private void notifyCheckOut(String NRICKey, String NRIC, String name, String location, String time) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRICKey).confirmCheckOut(NRIC, name, location, time);
        return;

    }

    

}
