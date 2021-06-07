
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.util.HashMap;
import java.util.List;

public class SafeEntryDatabase extends java.rmi.server.UnicastRemoteObject implements Database {
    private static HashMap<String, RemoteClientInterface> clientRemoteObjState = new HashMap<String, RemoteClientInterface>();
    public SafeEntryDatabase() throws java.rmi.RemoteException {
        // super();
        
    }

    //* !TODO: mutex flag to prevent concurrent write access to CSV file. 
    //* need thread joins and notify .... -.- 

    @Override
    public void checkIn(String NRIC, String name, String location, RemoteClientInterface remote) {

        final String line1[] = { NRIC, name, LocalDateTime.now().toString(), "", location, "not infected" };

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    setRemoteClient(remote, NRIC);
                    System.out.println("Checking In...");
                    CSVWriter writer = new CSVWriter(new FileWriter("C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv", true));

                    writer.writeNext(line1);
                    writer.close();
                    return;

                } catch (IOException e) {

                    e.printStackTrace();
                    System.out.println(e);
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
        final String csv_path = "C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv";

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(csv_path);

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

                                        CSVWriter writer = new CSVWriter(new FileWriter(csv_path));
                                        writer.writeAll(allData);
                                        writer.flush();
                                        writer.close();
                                        System.out.println("Checked out at: " + row[3]);


                                    }
                                }
                            }
                        }
                        i++;
                    }
                    
                } catch (Exception e) {
                    System.out.println(e);
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
        final String csv_path = "C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv";

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(csv_path);

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
    public void updateInfectedLocation(String location, String checkInTime, String checkOutTime) throws RemoteException {
        final String csv_path = "C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv";

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(csv_path);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).build();

                    List<String[]> allData = csvReader.readAll();

                    int i = 0;

                    if (allData.size() > 0 ) {

                        for (String[] row: allData) {
                            //System.out.println(row[4]+" .....");
                            if (row[4].toString().contains(location)) {
                                //System.out.println(row[4]);
                                long infectedCheckIn =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(checkInTime).getTime();
                                long infectedCheckout =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(checkOutTime).getTime();
                                long checkIn =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(row[2]).getTime();
                                long checkOut = 0;
                                
                                if (!row[3].isEmpty() ) {
                                    System.out.println("THE CHECK OUT TIME: "+row[3].toString());
                                    checkOut =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(row[3]).getTime();
                                } 
                                
                                
                                  
    
                                if (checkIn >= infectedCheckIn || checkOut >= infectedCheckIn || checkIn <= infectedCheckout) {
                                    
                                        row[5] = "infected";
                                        CSVWriter writer = new CSVWriter(new FileWriter(csv_path));
                                        writer.writeAll(allData);
                                        writer.flush();
                                        writer.close();
    
                                        System.out.println("infected location and time updated.");
    
                                        //** callback here */
                                        System.out.println("NPE???");
                                        notifyClient(row[0]);
                                        
                                }
                            }
                            i++;
                        }
    
                    } else if (allData.size() < 1) {
                        System.out.println("NO DATA");
                    }

                    
                    csvReader.close();

                } catch (Exception e) {
                    System.out.println(e);
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
    public void setRemoteClient(RemoteClientInterface remote, String NRIC) {
        SafeEntryDatabase.clientRemoteObjState.put(NRIC, remote);
        try {
            SafeEntryDatabase.clientRemoteObjState.get(NRIC).confirmCheckIn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyClient(String NRIC) throws RemoteException {
        SafeEntryDatabase.clientRemoteObjState.get(NRIC).notifyCovid();
        return;
        
    }
}
