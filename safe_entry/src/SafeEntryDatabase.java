
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.util.List;

public class SafeEntryDatabase extends java.rmi.server.UnicastRemoteObject implements Database {
    public SafeEntryDatabase() throws java.rmi.RemoteException {
        // super();
    }

    @Override
    public void checkIn(String NRIC, String name, String location, String rmi) {

        final String line1[] = { NRIC, name, LocalDateTime.now().toString(), "", location, "not infected", rmi };

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("In Thread");
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

                    for (String[] col : allData) {
                        System.out.println("....");
                        if (allData.get(i)[0].equals(NRIC)) {
                            System.out.println(NRIC);
                            if (allData.get(i)[1].equals(name)) {
                                System.out.println(name);
                                if (allData.get(i)[4].equals(location)) {
                                    System.out.println(location);
                                    if (allData.get(i)[3].equals("") || allData.get(i)[3].equals(null)) {
                                        allData.get(i)[3] = LocalDateTime.now().toString();

                                        CSVWriter writer = new CSVWriter(new FileWriter(csv_path));
                                        writer.writeAll(allData);
                                        writer.flush();
                                        writer.close();
                                        System.out.println("Checked out at: " + allData.get(i)[3]);


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
                            col[0] + ", " + col[1] + ", " + col[2] + ", " + col[3] + ", " + col[4] + ", " + col[5] + ", " + col[6]);

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

                    for (String[] col: allData) {
                        System.out.println(allData.get(i)[4]+" .....");
                        if (allData.get(i)[4].toString().contains(location)) {
                            System.out.println(allData.get(i)[4]);
                            long infectedCheckIn =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(checkInTime).getTime();
                            long infectedCheckout =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(checkOutTime).getTime();
                            long checkIn =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(allData.get(i)[2]).getTime();
                            long checkOut = 0;

                            if (allData.get(i)[3].equals("") || allData.get(i)[3].equals(null) ) {
                                
                                checkOut =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(allData.get(i)[3]).getTime();
                            }
                              

                            if (checkIn >= infectedCheckIn || checkOut >= infectedCheckIn || checkIn <= infectedCheckout) {
                                
                                    allData.get(i)[5] = "infected";
                                    CSVWriter writer = new CSVWriter(new FileWriter(csv_path));
                                    writer.writeAll(allData);
                                    writer.flush();
                                    writer.close();

                                    System.out.println("infected location and time updated.");

                                    //** callback here */
                                
                            }
                        }
                        i++;
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
}
