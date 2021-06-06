
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.rmi.RemoteException;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.util.Date;
import java.util.List;

public class SafeEntryDatabase extends java.rmi.server.UnicastRemoteObject implements Database {
    public SafeEntryDatabase() throws java.rmi.RemoteException {
        // super();
    }

    @Override
    public void checkIn(String NRIC, String name, String location, String rmi) {
        Date date = new Date();

        final String line1[] = { NRIC, name, date.toString(), "", location, rmi };

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
        return;

    }

    @Override
    public void checkOut(String NRIC, String name, String location, String rmi) {
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

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();

                    List<String[]> allData = csvReader.readAll();

                    for (String[] row : allData) {

                        System.out.println(
                                row[0] + ", " + row[1] + ", " + row[2] + ", " + row[3] + ", " + row[4] + ", " + row[5] + ", " + row[6]);

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
    public void update(String location) throws RemoteException {
        final String csv_path = "C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv";

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    FileReader fileReader = new FileReader(csv_path);

                    CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();

                    List<String[]> allData = csvReader.readAll();

                    for (String[] row : allData) {

                        System.out.println(
                                row[0] + ", " + row[1] + ", " + row[2] + ", " + row[3] + ", " + row[4] + ", " + row[5] + ", " + row[6]);

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
    public void delete() throws RemoteException {
        return;

    }
}
