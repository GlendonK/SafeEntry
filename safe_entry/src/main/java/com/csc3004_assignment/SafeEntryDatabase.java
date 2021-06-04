package com.csc3004_assignment;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;
import java.util.Date;

public class SafeEntryDatabase {
    public SafeEntryDatabase() {
        
    }

    public void checkIn(String NRIC, String name, String location, String rmi) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("safe_entry_db.csv", true));
            Date date = new Date();
            
            String line1[] = {NRIC, name, date.toString(), "", location, rmi};

            writer.writeNext(line1);
            writer.close();


        } catch (IOException e) {
            
            e.printStackTrace();
        }
    }
}
