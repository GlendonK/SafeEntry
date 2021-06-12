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

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
    public void checkIn(String NRIC, String name, String location, RemoteClientInterface remote) {

        final String time = LocalDateTime.now().toString();

        final String line1[] = { NRIC, name, time, "", location, "not infected" };      // default everyone is not infected.

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mutex.acquire();        // only threads holding the semaphore can write to database.
                    System.out.println("mutex aquired");
                    setRemoteClient(remote, NRIC, name, location, time);
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
                    return;

                } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println(e);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    mutex.release();        // release the semaphore for other threads to use.
                }

            }

        });
        thread.start();
        try {
            thread.join();      // wait for thread to end.
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return;

    }

    @Override
    public void familyCheckIn(ArrayList<List<String>> info,RemoteClientInterface remote ) {


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mutex.acquire();  

                    final String time = LocalDateTime.now().toString();
                    CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH, true)); 


                    for(int i=0;i<info.size();i++)
                    {                        
                        String nric = info.get(i).get(0);
                        String name = info.get(i).get(1);
                        String location = info.get(i).get(2);
                    
                        if(i==0)
                        {

                            setRemoteClient(remote, nric, name, location, time);
                            System.out.println("Checking In...");
                            String line1[] = {nric, name, time, "", location, "not infected",nric };
                            writer.writeNext(line1);
                        
                        }
                        else
                        {

                            String line1[] = {nric, name, time, "", location, "not infected"};                  
                            writer.writeNext(line1);

                        }
                    
                    }
                    writer.close();
                    return;

                
             } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println(e);
                }catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    mutex.release();        // release the semaphore for other threads to use.
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
     * checkOut method is called by client to write the check out time to database.
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

                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    int i = 0;

                    /**
                     * check if NRIC, name and location then write the check out time.
                     */
                    for (String[] row : allData) {
                        System.out.println("....");
                        if (row[0].equals(NRIC)) {
                            System.out.println(NRIC);
                            if (row[1].equals(name)) {
                                System.out.println(name);
                                if (row[4].equals(location)) {
                                    System.out.println(location);
                                    if (row[3].equals("") || row[3].equals(null)) {
                                        row[3] = LocalDateTime.now().toString();

                                        CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH));
                                        writer.writeAll(allData);
                                        writer.flush();
                                        writer.close();
                                        notifyCheckout(NRIC, name, location, row[3]);
                                        System.out.println("Checked out" + NRIC + " " + name + " at " + location
                                                + " at " + row[3]);
                                        System.out.println(Thread.currentThread().getName());

                                    }
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


    @Override
    public void familyCheckOut(ArrayList<List<String>> info) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    mutex.acquire();

                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    String delegateNRIC = "";
                    String delegateName = "";
                    String delegateLocation = "";
                    /**
                     * check if NRIC, name and location then write the check out time.
                     */
                    for(int i=0;i<info.size();i++)
                    {                        
                        String nric = info.get(i).get(0);
                        String name = info.get(i).get(1);
                        String location = info.get(i).get(2);
                        System.out.println(info);
                        System.out.println(nric);
                       
                        for (String[] row : allData) {
                            System.out.println("....");
                            if (row[0].equals(nric)) {
                                System.out.println(nric);
                                if (row[1].equals(name)) {
                                    System.out.println(name);
                                    if (row[4].equals(location)) {
                                        System.out.println(location);
                                        if (row[3].equals("") || row[3].equals(null)) {
                                            row[3] = LocalDateTime.now().toString();

                                            CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH));
                                            writer.writeAll(allData);
                                            writer.flush();
                                            writer.close();
                                            if(i==0)
                                            {
                                                delegateNRIC = nric;
                                                delegateName = name;
                                                delegateLocation = location;
                                                System.out.println("Checked out" + nric + " " + name + " at " + location
                                                + " at " + row[3]);
                                                System.out.println(Thread.currentThread().getName());
                                            }
                                            else if(i == info.size()-1)                                               
                                            {   
                                                notifyCheckout(delegateNRIC, delegateName, delegateLocation, row[3]);
                                                System.out.println("Checked out" + delegateNRIC + " " + delegateName + " at " + delegateLocation
                                                + " at " + row[3]);
                                            }
                                            else
                                            {
                                                System.out.println("Checked out" + nric + " " + name + " at " + location
                                                + " at " + row[3]);
                                            }


                                        }
                                    }
                                }
                            }
                        }
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
                            if (row[4].toString().contains(location)) {
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
                                    notifyClient(row[0], location, checkInTime, checkOutTime);

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
     * not used.
     * @deprecated
     */
    private void read() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(CSV_PATH);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    for (String[] col : allData) {

                        System.out.println(
                                col[0] + ", " + col[1] + ", " + col[2] + ", " + col[3] + ", " + col[4] + ", " + col[5]);

                    }

                    csvReader.close();

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
     * setRemoteClient method is used to save the invoked object by clients and callback to notify client 
     * that check in is successful.
     * @param remote RemoteClientInterface invoked object by client.
     * @param NRIC String NRIC of client.
     * @param name String name of client.
     * @param location String location of check in.
     * @param time String time of check in.'yyyy-MM-dd'T'HH:mm:ss' format.
     */
    private void setRemoteClient(RemoteClientInterface remote, String NRIC, String name, String location, String time) {
        SafeEntryDatabase.clientRemoteObjState.put(NRIC, remote);
        try {
            SafeEntryDatabase.clientRemoteObjState.get(NRIC).confirmCheckIn(NRIC, name, location, time);
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
    private void notifyClient(String NRIC, String location, String from, String to) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRIC).notifyCovid(location, from, to);
        return;

    }

    /**
     * notifyCheckout method callback to give feedback to client that check out is successful.
     * @param NRIC String NRIC of client.
     * @param name String name of client.
     * @param location String location of check out.
     * @param time String time of check out.'yyyy-MM-dd'T'HH:mm:ss' format.
     * @throws RemoteException
     */
    private void notifyCheckout(String NRIC, String name, String location, String time) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRIC).confirmCheckOut(NRIC, name, location, time);
        return;

    }
}
