import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App 
{
    public static void main(String[] args) {
        final String csv_path = "C:/Users/glend/Desktop/safe/safe_entry/src/safe_entry_db.csv";

        

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                   FileReader fileReader = new FileReader(csv_path);

                   CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();

                   List<String[]> allData = csvReader.readAll();

                   for (String[] row : allData) {
                    
                    System.out.println(row);
                }

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            
        });
        thread.start();
        
        return;
    }
}
