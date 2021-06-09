
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
import java.util.concurrent.Semaphore;

public class SafeEntryDatabase extends java.rmi.server.UnicastRemoteObject implements Database {
    private static HashMap<String, RemoteClientInterface> clientRemoteObjState = new HashMap<String, RemoteClientInterface>();
    private static Semaphore mutex = new Semaphore(1);
    private final String CSV_PATH = "safe_entry_db.csv";

    public SafeEntryDatabase() throws java.rmi.RemoteException {
        // super();

    }

    @Override
    public void checkIn(String NRIC, String name, String location, RemoteClientInterface remote) {

        final String time = LocalDateTime.now().toString();

        final String line1[] = { NRIC, name, time, "", location, "not infected" };

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mutex.acquire();
                    System.out.println("mutex aquired");
                    setRemoteClient(remote, NRIC, name, location, time);
                    System.out.println("Checking In " + NRIC + " " + name + " at " + location);
                    CSVWriter writer = new CSVWriter(
                            new FileWriter(CSV_PATH, true));
                    System.out.println(Thread.currentThread().getName());

                    //** test semophore */
                    // for (int i = 0; i < 10; i++) {
                    //     writer.writeNext(line1);
                    //     Thread.sleep(1000);
                    // }

                    writer.writeNext(line1);
                    writer.close();
                    return;

                } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println(e);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
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
    public void read() throws RemoteException {

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

                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        });
        thread.start();

        return;

    }

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

    @Override
    public void delete() throws RemoteException {
        return;

    }

    @Override
    public void setRemoteClient(RemoteClientInterface remote, String NRIC, String name, String location, String time) {
        SafeEntryDatabase.clientRemoteObjState.put(NRIC, remote);
        try {
            SafeEntryDatabase.clientRemoteObjState.get(NRIC).confirmCheckIn(NRIC, name, location, time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyClient(String NRIC, String location, String from, String to) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRIC).notifyCovid(location, from, to);
        return;

    }

    @Override
    public void notifyCheckout(String NRIC, String name, String location, String time) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRIC).confirmCheckOut(NRIC, name, location, time);
        return;

    }
}
