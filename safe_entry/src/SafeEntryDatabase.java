
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;
import java.util.Date;

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
                    CSVWriter writer = new CSVWriter(new FileWriter("safe_entry_db.csv", true));

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
            thread.join(1000);;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;

    }

    @Override
    public void checkOut() {
        return;

    }
}
